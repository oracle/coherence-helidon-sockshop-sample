/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.users;

import javax.enterprise.inject.spi.CDI;

import io.helidon.microprofile.server.Server;

import io.restassured.RestAssured;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;

/**
 * Integration tests for {@link AddressesResource}.
 */

public class CustomerResourceIT {
    private static Server SERVER;
    private UserRepository users;

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

    @BeforeEach
    void setup() {
        // Configure RestAssured to run tests against our application
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = SERVER.port();
        users = getUserRepository();
        User user = new User("Test", "User", "user@weavesocks.com", "user", "pass");
        user.addCard(new Card("1234123412341234", "12/19", "123"));
        user.addAddress(new Address("123", "Main St", "Springfield", "12123", "USA"));
        users.register(user);
    }

    protected UserRepository getUserRepository() {
        return CDI.current().select(UserRepository.class).get();
    }

    @Test
    public void testAllCustomers() {
        when().
            get("/customers").
        then().log().all().
            statusCode(200).
                body("size()", is(1));
    }

    @Test
    void testGetCustomer() {
        when().
            get("/customers/{id}", "user").
        then().log().all().
            statusCode(200).
            body("firstName", is("Test"));
    }

    @Test
    void testDeleteCustomer() {
        given().
            pathParam("id", "user").
        when().
            delete("/customers/{id}").
        then().
            statusCode(200).
            body("status", is(true));
    }

    @Test
    void testGetCustomerCards() {
        when().
            get("/customers/{id}/cards", "user").
        then().
            statusCode(200).
            body("size()", is(1));
    }

    @Test
    void testGetCustomerAddresses() {
        when().
            get("/customers/{id}/addresses", "user").
        then().
            statusCode(200).
            body("size()", is(1));
    }
}
