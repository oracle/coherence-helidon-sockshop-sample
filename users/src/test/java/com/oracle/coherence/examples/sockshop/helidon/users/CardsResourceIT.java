/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.users;

import jakarta.enterprise.inject.spi.CDI;

import io.helidon.microprofile.server.Server;

import io.restassured.RestAssured;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.http.ContentType.JSON;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

/**
 * Integration tests for {@link CardsResource}.
 */
public class CardsResourceIT
    {
    private static Server SERVER;
    private UserRepository users;

    /**
     * This will start the application on ephemeral port to avoid port conflicts.
     * We can discover the actual port by calling {@link Server#port()} method afterwards.
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
    @Disabled("https://github.com/rest-assured/rest-assured/issues/1651")
    public void testRegisterCard() {
        users.register(new User("foo", "passfoo", "foo@weavesocks.com", "foouser", "pass"));
        given().
            contentType(JSON).
            body(new CardsResource.AddCardRequest("3691369136913691", "01/21", "789", "foouser")).
        when().
            post("/cards").
        then().
            statusCode(200).
            body("id", containsString("foouser"));
    }

    @Test
    public void testGetCard() {
        User u = users.getOrCreate("cardUser");
        u.setUsername("cardUser");
        CardId cardId = u.addCard(new Card("3691369136913691", "01/21", "789")).getId();
        users.register(u);
        given().
            pathParam("id", cardId.toString()).
        when().
            get("/cards/{id}").
        then().
            statusCode(OK.getStatusCode()).
            body("longNum", containsString("3691"),
                    "ccv", is("789"));
    }

    @Test
    public void testDeleteCard() {
        User u = users.getOrCreate("cardUser");
        u.setUsername("cardUser");
        CardId cardId = u.addCard(new Card("3691369136913691", "01/21", "789")).getId();
        users.register(u);
        given().
            pathParam("id", cardId.toString()).
        when().
            get("/cards/{id}").
        then().
            statusCode(OK.getStatusCode());
    }

    @Test
    public void testGetAllCards() {
        when().
            get("/cards").
        then().
            statusCode(OK.getStatusCode());
    }
}
