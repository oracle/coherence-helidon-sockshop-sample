/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.orders;

import jakarta.enterprise.inject.spi.CDI;

import io.helidon.microprofile.server.Server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests for Coherence repository implementation.
 */
class CoherenceOrderRepositoryIT {
    private TestOrderRepository orders = getOrderRepository();

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

    @BeforeEach
    void setup() {
        orders.clear();
    }

    /**
     * Stop the server, as we cannot have multiple servers started at the same time.
     */
    @AfterAll
    static void stopServer() {
        SERVER.stop();
    }

    @Test
    void testFindOrdersByCustomer() {
        orders.saveOrder(TestDataFactory.order("homer", 1));
        orders.saveOrder(TestDataFactory.order("homer", 2));
        orders.saveOrder(TestDataFactory.order("marge", 5));

        assertThat(orders.findOrdersByCustomer("homer").size(), is(2));
        assertThat(orders.findOrdersByCustomer("marge").size(), is(1));
    }

    @Test
    void testOrderCreation() {
        Order order = TestDataFactory.order("homer", 1);
        orders.saveOrder(order);

        assertThat(orders.get(order.getOrderId()), is(order));
    }

    public final TestOrderRepository getOrderRepository() {
        return CDI.current().select(TestOrderRepository.class).get();
    }
}