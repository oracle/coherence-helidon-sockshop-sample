/*
 * Copyright (c) 2020, 2024 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.shipping;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.enterprise.context.ApplicationScoped;

import jakarta.inject.Inject;

import com.oracle.coherence.cdi.Name;
import com.tangosol.net.NamedMap;

import java.util.Map;

/**
 * An implementation of {@link ShipmentRepository}
 * that that uses Coherence as a backend data store.
 */
@ApplicationScoped
public class CoherenceShipmentRepository implements ShipmentRepository {
    protected Map<String, Shipment> shipments;

    @Inject
    public CoherenceShipmentRepository(@Name("shipments") NamedMap<String, Shipment> shipments) {
        this.shipments = shipments;
    }

    @Override
    @WithSpan
    public Shipment getShipment(String orderId) {
        return shipments.get(orderId);
    }

    @Override
    @WithSpan
    public void saveShipment(Shipment shipment) {
        shipments.put(shipment.getOrderId(), shipment);
    }
}
