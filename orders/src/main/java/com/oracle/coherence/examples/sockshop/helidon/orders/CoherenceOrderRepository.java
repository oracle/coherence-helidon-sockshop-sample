/*
 * Copyright (c) 2020, 2024, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.orders;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import java.util.Collection;
import java.util.Collections;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.oracle.coherence.cdi.Name;
import com.tangosol.net.NamedMap;
import com.tangosol.util.Filters;

/**
 * An implementation of {@link OrderRepository}
 * that that uses Coherence as a backend data store.
 */
@ApplicationScoped
public class CoherenceOrderRepository implements OrderRepository {
    protected NamedMap<String, Order> orders;

    @Inject
    public CoherenceOrderRepository(@Name("orders") NamedMap<String, Order> orders) {
        this.orders = orders;
    }

    @WithSpan
    @Override
    public Collection<? extends Order> findOrdersByCustomer(String customerId) {
        Collection<Order> customerOrders = orders.values(Filters.equal(o -> ((Order) o).getCustomer().getId(), customerId), null);
        return customerOrders.isEmpty() ? Collections.EMPTY_LIST : customerOrders;
    }

    @WithSpan
    @Override
    public Order get(String orderId) {
        return orders.get(orderId);
    }

    @WithSpan
    @Override
    public void saveOrder(Order order) {
        orders.put(order.getOrderId(), order);
    }
}
