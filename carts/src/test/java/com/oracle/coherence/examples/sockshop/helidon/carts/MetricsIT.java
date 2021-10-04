package com.oracle.coherence.examples.sockshop.helidon.carts;

import io.helidon.microprofile.server.Server;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
	void testAddItem() {
		given().
				port(SERVER.port()).
				contentType(JSON).
				body(new Item("X1", 0, 10f)).
		when().
				post("/carts/{cartId}/items", "C1").
		then().
				statusCode(CREATED.getStatusCode()).
				body("itemId", is("X1"),
						"quantity", is(1),
						"unitPrice", is(10f));

		given().
				port(9612).
				accept(JSON).
		when().
				get("/metrics/Coherence.Cache.Size?name=carts&tier=back").
		then().
				statusCode(200).
		assertThat().
				body("size()", is(1)).
				body("[0].tags.name", is("carts"),
						"[0].value", is(1));
	}
}
