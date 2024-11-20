/*
 * Copyright (c) 2020, 2024, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.shipping;

import io.helidon.grpc.api.Grpc;

import io.helidon.microprofile.grpc.client.GrpcClientCdiExtension;
import io.helidon.microprofile.grpc.client.GrpcConfigurablePort;
import io.helidon.microprofile.grpc.server.GrpcMpCdiExtension;

import io.helidon.microprofile.testing.junit5.AddBean;
import io.helidon.microprofile.testing.junit5.AddExtension;
import io.helidon.microprofile.testing.junit5.HelidonTest;

import jakarta.enterprise.inject.spi.CDI;

import jakarta.inject.Inject;

import jakarta.ws.rs.client.WebTarget;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Integration tests for {@link ShippingResource} gRPC API.
 */
@HelidonTest
@AddBean(ShippingClient.class)
@AddExtension(GrpcMpCdiExtension.class)
@AddExtension(GrpcClientCdiExtension.class)
public class ShippingGrpcIT {
    

    /**
     * This will start the application on ephemeral port to avoid port conflicts.
     * We can discover the actual port by calling {@link io.helidon.microprofile.server.Server#port()} method afterwards.
     */
    @BeforeAll
    static void startServer() {
        // disable global tracing so we can start server in multiple test suites
        System.setProperty("tracing.global", "false");
        System.setProperty("grpc.port", "0");
    }


    @Inject
    private WebTarget target;

    @Inject
    @Grpc.GrpcProxy
    private ShippingClient client;

    private TestShipmentRepository shipments;

    @BeforeEach
    void setup() {
        if (client instanceof GrpcConfigurablePort client) {
            client.channelPort(target.getUri().getPort());
        }
        shipments = CDI.current().select(TestShipmentRepository.class).get();
        shipments.clear();
    }

    @Test
    void testFedEx() {
        Shipment shipment = client.ship(TestDataFactory.shippingRequest("A123", 1));
        assertThat(shipment.getCarrier(), is("FEDEX"));
        assertThat(shipment.getDeliveryDate(), is(LocalDate.now().plusDays(1)));
    }

    @Test
    void testUPS() {
        Shipment shipment = client.ship(TestDataFactory.shippingRequest("A456", 3));
        assertThat(shipment.getCarrier(), is("UPS"));
        assertThat(shipment.getDeliveryDate(), is(LocalDate.now().plusDays(3)));
    }

    @Test
    void testUSPS() {
        Shipment shipment = client.ship(TestDataFactory.shippingRequest("A789", 10));
        assertThat(shipment.getCarrier(), is("USPS"));
        assertThat(shipment.getDeliveryDate(), is(LocalDate.now().plusDays(5)));
    }

    @Test
    void testGetShipmentByOrder() {
        LocalDate deliveryDate = LocalDate.now().plusDays(2);
        shipments.saveShipment(TestDataFactory.shipment("A123", "UPS", "1Z999AA10123456784", deliveryDate));

        Shipment shipment = client.getShipmentByOrderId("A123");
        assertThat(shipment.getOrderId(), is("A123"));
        assertThat(shipment.getCarrier(), is("UPS"));
        assertThat(shipment.getTrackingNumber(), is("1Z999AA10123456784"));
        assertThat(shipment.getDeliveryDate(), is(deliveryDate));

    //    assertThat(client.getShipmentByOrderId("B456"), nullValue());
    }
}
