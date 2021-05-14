/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.orders;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import lombok.extern.java.Log;

import org.eclipse.microprofile.metrics.annotation.Timed;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * Implementation of the Orders Service REST API.
 */
@ApplicationScoped
@Path("/orders")
@Log
public class OrderResource implements OrderApi {
    /**
     * Order repository to use.
     */
    @Inject
    private OrderRepository orders;

    /**
     * Order processor to use.
     */
    @Inject
    private OrderProcessor processor;

    @Inject
    protected CartsClient cartsService;

    @Inject
    protected UsersClient usersService;

    @Override
    public Response getOrdersForCustomer(String customerId) {
        Collection<? extends Order> customerOrders = orders.findOrdersByCustomer(customerId);
        if (customerOrders.isEmpty()) {
            return Response.status(NOT_FOUND).build();
        }
        return wrap(customerOrders);
    }

    private Response wrap(Object value) {
        Map<String, Map<String, Object>> map = Collections.singletonMap("_embedded", Collections.singletonMap("customerOrders", value));
        return Response.ok(map).build();
    }

    @Override
    public Response getOrder(String orderId) {
        Order order = orders.get(orderId);
        return order == null
                ? Response.status(NOT_FOUND).build()
                : Response.ok(order).build();
    }

    @Override
    @Timed
    public Response newOrder(UriInfo uriInfo, NewOrderRequest request) {
        log.info("Processing new order: " + request);

        if (request.address == null || request.customer == null || request.card == null || request.items == null) {
            throw new InvalidOrderException("Invalid order request. Order requires customer, address, card and items.");
        }

        String itemsPath = request.items.getPath();
        String addressPath = request.address.getPath();
        String cardPath = request.card.getPath();
        String customerPath = request.customer.getPath();
        if (!itemsPath.startsWith("/carts/") || !itemsPath.endsWith("/items") ||
            !addressPath.startsWith("/addresses/") ||
            !cardPath.startsWith("/cards/") ||
            !customerPath.startsWith("/customers/")) {
            throw new InvalidOrderException("Invalid order request. Order requires the URIs to have path /customers/xxx, /addresses/xxx, /cards/xxx and /carts/xxx/items.");
        }

        List<Item> items    = cartsService.cart(itemsPath.substring(7, itemsPath.length() - 6));
        Address    address  = usersService.address(addressPath.substring(11));
        Card       card     = usersService.card(cardPath.substring(7));
        Customer   customer = usersService.customer(customerPath.substring(11));

        Order order = Order.builder()
                .customer(customer)
                .address(address)
                .card(card)
                .items(items)
                .build();

        processor.processOrder(order);

        log.info("Created Order: " + order.getOrderId());
        return Response.status(CREATED).entity(order).build();
    }

    // ---- inner class: InvalidOrderException ------------------------------

    /**
     * An exception that is thrown if the arguments in the {@code NewOrderRequest}
     * are invalid.
     */
    public static class InvalidOrderException extends OrderException {
        public InvalidOrderException(String s) {
            super(s);
        }
    }
}
