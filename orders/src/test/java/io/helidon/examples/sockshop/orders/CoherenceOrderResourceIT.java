/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.orders;

import java.net.URI;
import java.time.LocalDate;

import com.oracle.bedrock.testsupport.deferred.Eventually;

import io.helidon.microprofile.server.Server;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import io.restassured.mapper.ObjectMapperType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.enterprise.inject.spi.CDI;

import static io.helidon.examples.sockshop.orders.Order.Status.PAYMENT_FAILED;
import static io.helidon.examples.sockshop.orders.Order.Status.SHIPPED;

import static io.helidon.examples.sockshop.orders.TestDataFactory.order;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;

import static io.restassured.RestAssured.when;
import static javax.ws.rs.core.Response.Status.CREATED;

import static javax.ws.rs.core.Response.Status.NOT_ACCEPTABLE;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

/**
 * Integration tests for {@link io.helidon.examples.sockshop.orders.OrderResource},
 * using Coherence for persistence.
 */
public class CoherenceOrderResourceIT {

    protected static Server SERVER;

    /**
     * This will start the application on ephemeral port to avoid port conflicts.
     * We can discover the actual port by calling {@link io.helidon.microprofile.server.Server#port()} method afterwards.
     */
    @BeforeAll
    static void startServer() {
        // disable global tracing so we can start server in multiple test suites
        System.setProperty("tracing.global", "false");
        SERVER = Server.builder().port(0).build().start();
    }

    /**
     * Stop the server, as we cannot have multiple servers started at the same time.
     */
    @AfterAll
    static void stopServer() {
        SERVER.stop();
    }

    protected TestOrderRepository orders;

    @BeforeEach
    protected void setup() {
        // Configure RestAssured to run tests against our application
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = SERVER.port();

        orders = CDI.current().select(TestOrderRepository.class).get();
        orders.clear();
    }

    @Test
    protected void testGetMissingOrder() {
        when().
                get("/orders/{orderId}", "XYZ").
                then().
                statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    protected void testGetOrder() {
        Order order = order("homer", 1);
        orders.saveOrder(order);
        Order saved = get("/orders/{orderId}", order.getOrderId()).as(Order.class, ObjectMapperType.JSONB);

        assertThat(saved, is(order));
    }

    @Test
    protected void testFindOrdersByCustomerId() {
        orders.saveOrder(order("homer", 1));
        orders.saveOrder(order("homer", 2));
        orders.saveOrder(order("marge", 5));

        given().
                queryParam("custId", "homer").
                when().
                get("/orders/search/customerId").
                then().
                statusCode(OK.getStatusCode()).
                body("_embedded.customerOrders.total", containsInAnyOrder(1f, 5f));

        given().
                queryParam("custId", "marge").
                when().
                get("/orders/search/customerId").
                then().
                statusCode(OK.getStatusCode()).
                body("_embedded.customerOrders.total", containsInAnyOrder(55f));

        given().
                queryParam("custId", "bart").
                when().
                get("/orders/search/customerId").

                then().
                statusCode(NOT_FOUND.getStatusCode());
    }


    @Test
    protected void testInvalidOrder() {
        NewOrderRequest req = NewOrderRequest.builder().build();

        given().
                body(req).
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
                when().
                post("/orders").
                then().
                statusCode(NOT_ACCEPTABLE.getStatusCode())
                .body("message", is("Invalid order request. Order requires customer, address, card and items."));
    }

    @Test
    protected void testCreateOrder() {
        String baseUri = "http://localhost:" + SERVER.port();
        NewOrderRequest req = NewOrderRequest.builder()
                .customer(URI.create(baseUri + "/customers/homer"))
                .address(URI.create(baseUri + "/addresses/homer:1"))
                .card(URI.create(baseUri + "/cards/homer:1234"))
                .items(URI.create(baseUri + "/carts/homer/items"))
                .build();

        given().
                body(req).
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
        when().
                post("/orders").
        then().
                statusCode(CREATED.getStatusCode()).
                body("total", is(14.0f),
                     "status", is("CREATED"));

        final String orderId = orders.getLastOrderId();
        Eventually.assertDeferred(() -> orders.get(orderId).getStatus(), is(SHIPPED));

        Order order = orders.get(orderId);
        assertThat(order.getPayment().isAuthorised(), is(true));
        assertThat(order.getShipment().getCarrier(), is("UPS"));
        assertThat(order.getShipment().getDeliveryDate(), is(LocalDate.now().plusDays(2)));
    }

    @Test
    protected void testPaymentFailure() {
        String baseUri = "http://localhost:" + SERVER.port();
        NewOrderRequest req = NewOrderRequest.builder()
                .customer(URI.create(baseUri + "/customers/lisa"))
                .address(URI.create(baseUri + "/addresses/lisa:1"))
                .card(URI.create(baseUri + "/cards/lisa:1234"))
                .items(URI.create(baseUri + "/carts/lisa/items"))
                .build();

        given().
                body(req).
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
        when().
                post("/orders").
        then().
                statusCode(CREATED.getStatusCode()).
                body("total", is(14.0f),
                    "status", is("CREATED"));

        final String orderId = orders.getLastOrderId();
        Eventually.assertDeferred(() -> orders.get(orderId).getStatus(), is(PAYMENT_FAILED));

        Order order = orders.get(orderId);
        assertThat(order.getPayment().isAuthorised(), is(false));
        assertThat(order.getPayment().getMessage(), is("Unable to parse authorization packet"));
    }

    @Test
    protected void testPaymentDeclined() {
        String baseUri = "http://localhost:" + SERVER.port();
        NewOrderRequest req = NewOrderRequest.builder()
                .customer(URI.create(baseUri + "/customers/bart"))
                .address(URI.create(baseUri + "/addresses/bart:1"))
                .card(URI.create(baseUri + "/cards/bart:1234"))
                .items(URI.create(baseUri + "/carts/bart/items"))
                .build();

        given().
                body(req).
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
        when().
                post("/orders").
        then().
                statusCode(CREATED.getStatusCode()).
                body("total", is(14.0f),
                    "status", is("CREATED"));

        final String orderId = orders.getLastOrderId();
        Eventually.assertDeferred(() -> orders.get(orderId).getStatus(), is(PAYMENT_FAILED));

        Order order = orders.get(orderId);
        assertThat(order.getPayment().isAuthorised(), is(false));
        assertThat(order.getPayment().getMessage(), is("Minors need parent approval"));
    }
}