/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.shipping;

import io.helidon.microprofile.server.Server;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static jakarta.ws.rs.core.Response.Status.OK;
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
	void testFedEx() {
		given().
				port(SERVER.port()).
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

		given().
				port(9612).
				accept(JSON).
		when().
				get("/metrics/Coherence.Cache.Size?name=shipments&tier=back").
		then().
				statusCode(200).
		assertThat().
				body("size()", is(1)).
				body("[0].tags.name", is("shipments"),
						"[0].value", is(1));
	}
}
