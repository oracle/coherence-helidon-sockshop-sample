package com.oracle.coherence.examples.sockshop.helidon.catalog;

import com.tangosol.net.Coherence;
import com.tangosol.net.NamedCache;
import io.helidon.microprofile.server.Server;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;

public class MetricsIT {
	protected static Server SERVER;

	private NamedCache<String, Sock> socks;

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
		socks = Coherence.getInstance().getSession().getCache("socks");

		socks.put("example", new Sock());
		socks.put("another", new Sock());

		given().
				port(9612).
				accept(JSON).
		when().
				get("/metrics").
		then().
				statusCode(200);

		given().
				port(9612).
				accept(JSON).
		when().
				get("/metrics/Coherence.Cache.Size?name=socks&tier=back").
		then().
				statusCode(200).
		assertThat().
				body("size()", is(1)).
		        body("[0].tags.name", is("socks"),
						"[0].value", is(2));
	}
}
