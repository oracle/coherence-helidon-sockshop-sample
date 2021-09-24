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
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for {@link UserResource}.
 */

public class UserResourceIT {
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
        users.removeUser("foouser");
        users.removeUser("baruser");
    }

    protected UserRepository getUserRepository() {
        return CDI.current().select(UserRepository.class).get();
    }

    @Test
    public void testAuthentication() {
        users.register(new User("foo", "passfoo", "foo@weavesocks.com", "foouser", "pass"));
        given().auth().preemptive().basic("foouser", "pass").
        when().
            get("/login").
        then().
            assertThat().
            statusCode(200);
    }

    @Test
    public void testRegister() {
        users.removeUser("baruser");
        given().
            contentType(JSON).
            body(new User("bar", "passbar", "bar@weavesocks.com", "baruser", "pass")).
        when().
            post("/register").
        then().
            statusCode(200).
            body("id", is("baruser"));
    }
}
