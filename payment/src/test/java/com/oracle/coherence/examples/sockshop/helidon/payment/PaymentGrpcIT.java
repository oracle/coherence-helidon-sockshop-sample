/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.payment;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.enterprise.inject.spi.CDI;

import io.helidon.microprofile.grpc.client.GrpcProxyBuilder;
import io.helidon.microprofile.server.Server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.oracle.coherence.examples.sockshop.helidon.payment.TestDataFactory.auth;
import static com.oracle.coherence.examples.sockshop.helidon.payment.TestDataFactory.paymentRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

/**
 * Integration tests for {@link PaymentGrpc}.
 */
public class PaymentGrpcIT {
    protected static Server SERVER;
    private static PaymentClient CLIENT;

    /**
     * This will start the application on ephemeral port to avoid port conflicts.
     * We can discover the actual port by calling {@link Server#port()} method afterwards.
     */
    @BeforeAll
    static void startServer() {
        // disable global tracing so we can start server in multiple test suites
        System.setProperty("tracing.global", "false");
        System.setProperty("grpc.port", "0");
        SERVER = Server.builder().port(0).build().start();
        CLIENT = GrpcProxyBuilder.create(PaymentClient.class).build();
    }

    /**
     * Stop the server, as we cannot have multiple servers started at the same time.
     */
    @AfterAll
    static void stopServer() {
        SERVER.stop();
    }

    private TestPaymentRepository payments;

    @BeforeEach
    void setup() {
        payments = CDI.current().select(TestPaymentRepository.class).get();
        payments.clear();
    }

    @Test
    void testSuccessfulAuthorization() {
        Authorization authorization = CLIENT.authorize(paymentRequest("A123", 50));
        assertThat(authorization.isAuthorised(), is(true));
        assertThat(authorization.getMessage(), is("Payment authorized."));
    }

    @Test
    void testDeclinedAuthorization() {
        Authorization authorization = CLIENT.authorize(paymentRequest("A123", 150));
        assertThat(authorization.isAuthorised(), is(false));
        assertThat(authorization.getMessage(), is("Payment declined: amount exceeds 100.00"));
    }

    @Test
    void testInvalidPaymentAmount() {
        Authorization authorization = CLIENT.authorize(paymentRequest("A123", -50));
        assertThat(authorization.isAuthorised(), is(false));
        assertThat(authorization.getMessage(), is("Invalid payment amount."));
    }

    @Test
    void testFindPaymentsByOrder() {
        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        payments.saveAuthorization(auth("A123", time, new Err("Payment service unavailable")));
        payments.saveAuthorization(auth("A123", time.plusSeconds(5), false, "Payment declined"));
        payments.saveAuthorization(auth("A123", time.plusSeconds(10), true, "Payment processed"));
        payments.saveAuthorization(auth("B456", time, true, "Payment processed"));

        assertThat(CLIENT.getOrderAuthorizations("A123"), hasSize(3));
        assertThat(CLIENT.getOrderAuthorizations("B456"), hasSize(1));
        assertThat(CLIENT.getOrderAuthorizations("C789"), hasSize(0));
    }
}
