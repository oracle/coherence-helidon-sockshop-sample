/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.shipping;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * Abstract base class containing tests for all
 * {@link ShipmentRepository} implementations.
 */
public abstract class ShipmentRepositoryTest {

    private final TestShipmentRepository shipments = getShipmentRepository();

    protected abstract TestShipmentRepository getShipmentRepository();

    @BeforeEach
    void setup() {
        shipments.clear();
    }

    @Test
    void testSaveShipment() {
        LocalDate deliveryDate = LocalDate.now().plusDays(2);
        shipments.saveShipment(TestDataFactory.shipment("A123", "UPS", "1Z999AA10123456784", deliveryDate));

        Shipment s = shipments.getShipment("A123");
        assertThat(s.getOrderId(), is("A123"));
        assertThat(s.getCarrier(), is("UPS"));
        assertThat(s.getTrackingNumber(), is("1Z999AA10123456784"));
        assertThat(s.getDeliveryDate(), is(deliveryDate));
    }


    @Test
    void testGetShipmentByOrder() {
        assertThat(shipments.getShipment("A123"), nullValue());

        LocalDate deliveryDate = LocalDate.now().plusDays(2);
        shipments.saveShipment(TestDataFactory.shipment("A123", "UPS", "1Z999AA10123456784", deliveryDate));

        Shipment s = shipments.getShipment("A123");
        assertThat(s.getOrderId(), is("A123"));
        assertThat(s.getCarrier(), is("UPS"));
        assertThat(s.getTrackingNumber(), is("1Z999AA10123456784"));
        assertThat(s.getDeliveryDate(), is(deliveryDate));
    }
}
