/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.carts;

import javax.enterprise.inject.spi.CDI;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import io.helidon.microprofile.server.Server;

/**
 * Tests for Coherence repository implementation.
 */
class CoherenceCartRepositoryAsyncIT extends CartRepositoryTest {
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

    @Override
    protected CartRepository getCartRepository() {
        CartRepositoryAsync cartsAsync = CDI.current().select(CartRepositoryAsync.class).get();
        return new SyncCartRepository(cartsAsync);
    }
}
