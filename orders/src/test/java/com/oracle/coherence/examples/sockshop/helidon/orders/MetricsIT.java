package com.oracle.coherence.examples.sockshop.helidon.orders;

import io.helidon.microprofile.server.Server;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static javax.ws.rs.core.Response.Status.CREATED;
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
	void testCreateOrder() {
		String baseUri = "http://localhost:" + SERVER.port();
		NewOrderRequest req = NewOrderRequest.builder()
				.customer(URI.create(baseUri + "/customers/homer"))
				.address(URI.create(baseUri + "/addresses/homer:1"))
				.card(URI.create(baseUri + "/cards/homer:1234"))
				.items(URI.create(baseUri + "/carts/homer/items"))
				.build();

		given().
				port(SERVER.port()).
				body(req).
				contentType(ContentType.JSON).
				accept(ContentType.JSON).
		when().
				post("/orders").
		then().
				statusCode(CREATED.getStatusCode()).
				body("total", is(14.0f),
						"status", is("CREATED"));

		given().
				port(9612).
				accept(JSON).
		when().
				get("/metrics/Coherence.Cache.Size?name=orders&tier=back").
		then().
				statusCode(200).
		assertThat().
				body("size()", is(1)).
		        body("[0].tags.name", is("orders"),
						"[0].value", is(1));
	}
}
