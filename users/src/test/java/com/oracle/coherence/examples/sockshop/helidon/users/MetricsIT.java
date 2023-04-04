/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.users;

import io.helidon.microprofile.server.Server;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;

public class MetricsIT {
	protected static Server SERVER;

	@BeforeAll
	static void startServer() {
		SERVER = Server.builder().port(0).build().start();
	}

	@AfterAll
	static void stopServer() {
		SERVER.stop();
	}

	@BeforeEach
	void setup() {
		RestAssured.reset();
		RestAssured.baseURI = "http://localhost";
	}

	@Test
	@Disabled("https://github.com/rest-assured/rest-assured/issues/1651")
	public void testRegister() {
		given().
				port(SERVER.port()).
				contentType(JSON).
				body(new User("bar", "passbar", "bar@weavesocks.com", "baruser", "pass")).
		when().
				post("/register").
		then().
				statusCode(200).
				body("id", is("baruser"));

		given().
				port(9612).
				accept(JSON).
		when().
				get("/metrics/Coherence.Cache.Size?name=users&tier=back").
		then().
				statusCode(200).
		assertThat().
				body("size()", is(1)).
				body("[0].tags.name", is("users"),
						"[0].value", is(1));
	}
}
