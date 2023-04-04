/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.payment;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jakarta.enterprise.inject.spi.CDI;

import io.helidon.microprofile.server.Server;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.oracle.coherence.examples.sockshop.helidon.payment.TestDataFactory.auth;
import static com.oracle.coherence.examples.sockshop.helidon.payment.TestDataFactory.paymentRequest;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

/**
 * Integration tests for {@link PaymentResource}.
 */
public class PaymentResourceIT {
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

    private TestPaymentRepository payments;

    @BeforeEach
    void setup() throws Exception {
        // Configure RestAssured to run tests against our application
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = SERVER.port();

        payments = CDI.current().select(TestPaymentRepository.class).get();
        payments.clear();
    }

    @Test
    void testSuccessfulAuthorization() {
        given().
                body(paymentRequest("A123", 50)).
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
        when().
                post("/payments").
        then().
                statusCode(OK.getStatusCode()).
                body("authorised", is(true),
                     "message", is("Payment authorized.")
                );
    }

    @Test
    void testDeclinedAuthorization() {
        given().
                body(paymentRequest("A123", 150)).
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
        when().
                post("/payments").
        then().
                statusCode(OK.getStatusCode()).
                body("authorised", is(false),
                     "message", is("Payment declined: amount exceeds 100.00")
                );
    }

    @Test
    void testInvalidPaymentAmount() {
        given().
                body(paymentRequest("A123", -50)).
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
        when().
                post("/payments").
        then().
                statusCode(OK.getStatusCode()).
                body("authorised", is(false),
                     "message", is("Invalid payment amount.")
                );
    }

    @Test
    void testFindPaymentsByOrder() {
        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        payments.saveAuthorization(auth("A123", time, new Err("Payment service unavailable")));
        payments.saveAuthorization(auth("A123", time.plusSeconds(5), false, "Payment declined"));
        payments.saveAuthorization(auth("A123", time.plusSeconds(10), true, "Payment processed"));
        payments.saveAuthorization(auth("B456", time, true, "Payment processed"));

        when().
                get("/payments/{orderId}", "A123").
        then().
                statusCode(OK.getStatusCode()).
                body("$", hasSize(3));

        when().
                get("/payments/{orderId}", "B456").
        then().
                statusCode(OK.getStatusCode()).
                body("$", hasSize(1));

        when().
                get("/payments/{orderId}", "C789").
        then().
                statusCode(OK.getStatusCode()).
                body("$", hasSize(0));
    }
}
