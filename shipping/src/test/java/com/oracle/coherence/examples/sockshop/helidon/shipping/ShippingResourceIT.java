/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.shipping;

import java.time.LocalDate;

import javax.enterprise.inject.spi.CDI;

import io.helidon.microprofile.server.Server;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.is;

/**
 * Integration tests for {@link ShippingResource}.
 */
public class ShippingResourceIT {
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

    private TestShipmentRepository shipments;

    @BeforeEach
    void setup() {
        // Configure RestAssured to run tests against our application
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = SERVER.port();

        shipments = CDI.current().select(TestShipmentRepository.class).get();
        shipments.clear();
    }

    @Test
    void testFedEx() {
        given().
                body(TestDataFactory.shippingRequest("A123", 1)).
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
        when().
                post("/shipping").
        then().
                statusCode(OK.getStatusCode()).
                body("carrier", is("FEDEX"),
                     "deliveryDate", is(LocalDate.now().plusDays(1).toString())
                );
    }

    @Test
    void testUPS() {
        given().
                body(TestDataFactory.shippingRequest("A456", 3)).
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
        when().
                post("/shipping").
        then().
                statusCode(OK.getStatusCode()).
                body("carrier", is("UPS"),
                     "deliveryDate", is(LocalDate.now().plusDays(3).toString())
                );
    }

    @Test
    void testUSPS() {
        given().
                body(TestDataFactory.shippingRequest("A789", 10)).
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
        when().
                post("/shipping").
        then().
                statusCode(OK.getStatusCode()).
                body("carrier", is("USPS"),
                     "deliveryDate", is(LocalDate.now().plusDays(5).toString())
                );
    }

    @Test
    void testGetShipmentByOrder() {
        LocalDate deliveryDate = LocalDate.now().plusDays(2);
        shipments.saveShipment(TestDataFactory.shipment("A123", "UPS", "1Z999AA10123456784", deliveryDate));

        when().
                get("/shipping/{orderId}", "A123").
        then().
                statusCode(OK.getStatusCode()).
                body("carrier", is("UPS"),
                     "trackingNumber", is("1Z999AA10123456784"),
                     "deliveryDate", is(deliveryDate.toString())
                );

        when().
                get("/shipments/{orderId}", "B456").
        then().
                statusCode(NOT_FOUND.getStatusCode());
    }
}
