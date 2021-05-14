/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.users;

import javax.enterprise.inject.spi.CDI;

import io.helidon.microprofile.server.Server;

import io.restassured.RestAssured;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

/**
 * Integration tests for {@link AddressesResource}.
 */

public class AddressesResourceIT {
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
    public void testRegisterAddress() {
        users.register(new User("foo", "passfoo", "foo@weavesocks.com", "foouser", "pass"));
        given().
            contentType(JSON).
            body(new AddressesResource.AddAddressRequest("16", "huntington", "lexington", "01886", "us", "foouser")).
        when().
            post("/addresses").
        then().
            statusCode(200).
            body("id", containsString("foouser"));
    }

    @Test
    public void testGetAddress() {
        User u = new User("foo", "passfoo", "foo@weavesocks.com", "foouser", "pass");
        AddressId addrId = u.addAddress(new Address("555", "woodbury St", "Westford", "01886", "USA")).getId();
        users.register(u);

        given().
              pathParam("id", addrId.toString()).
        when().
              get("/addresses/{id}").
        then().
            statusCode(OK.getStatusCode()).
            body("number", is("555"),
                    "city", is("Westford"));
    }

    @Test
    public void testDeleteAddress() {
        User u = users.getOrCreate("foouser");
        u.setUsername("foouser");
        AddressId addrId = u.addAddress(new Address("555", "woodbury St", "Westford", "01886", "USA")).getId();
        users.register(u);

        given().
            pathParam("id", addrId.toString()).
        when().
            delete("/addresses/{id}").
        then().
            statusCode(200);
    }
}
