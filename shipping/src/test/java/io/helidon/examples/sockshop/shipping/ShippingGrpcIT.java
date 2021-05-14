/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.shipping;

import java.time.LocalDate;

import javax.enterprise.inject.spi.CDI;

import io.helidon.microprofile.grpc.client.GrpcProxyBuilder;
import io.helidon.microprofile.server.Server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.helidon.examples.sockshop.shipping.TestDataFactory.shipment;
import static io.helidon.examples.sockshop.shipping.TestDataFactory.shippingRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Integration tests for {@link ShippingResource} gRPC API.
 */
public class ShippingGrpcIT {
    protected static Server SERVER;
    private static ShippingClient CLIENT;

    /**
     * This will start the application on ephemeral port to avoid port conflicts.
     * We can discover the actual port by calling {@link io.helidon.microprofile.server.Server#port()} method afterwards.
     */
    @BeforeAll
    static void startServer() {
        // disable global tracing so we can start server in multiple test suites
        System.setProperty("tracing.global", "false");
        System.setProperty("grpc.port", "0");
        SERVER = Server.builder().port(0).build().start();
        CLIENT = GrpcProxyBuilder.create(ShippingClient.class).build();
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
        shipments = CDI.current().select(TestShipmentRepository.class).get();
        shipments.clear();
    }

    @Test
    void testFedEx() {
        Shipment shipment = CLIENT.ship(shippingRequest("A123", 1));
        assertThat(shipment.getCarrier(), is("FEDEX"));
        assertThat(shipment.getDeliveryDate(), is(LocalDate.now().plusDays(1)));
    }

    @Test
    void testUPS() {
        Shipment shipment = CLIENT.ship(shippingRequest("A456", 3));
        assertThat(shipment.getCarrier(), is("UPS"));
        assertThat(shipment.getDeliveryDate(), is(LocalDate.now().plusDays(3)));
    }

    @Test
    void testUSPS() {
        Shipment shipment = CLIENT.ship(shippingRequest("A789", 10));
        assertThat(shipment.getCarrier(), is("USPS"));
        assertThat(shipment.getDeliveryDate(), is(LocalDate.now().plusDays(5)));
    }

    @Test
    void testGetShipmentByOrder() {
        LocalDate deliveryDate = LocalDate.now().plusDays(2);
        shipments.saveShipment(shipment("A123", "UPS", "1Z999AA10123456784", deliveryDate));

        Shipment shipment = CLIENT.getShipmentByOrderId("A123");
        assertThat(shipment.getOrderId(), is("A123"));
        assertThat(shipment.getCarrier(), is("UPS"));
        assertThat(shipment.getTrackingNumber(), is("1Z999AA10123456784"));
        assertThat(shipment.getDeliveryDate(), is(deliveryDate));

    //    assertThat(client.getShipmentByOrderId("B456"), nullValue());
    }
}
