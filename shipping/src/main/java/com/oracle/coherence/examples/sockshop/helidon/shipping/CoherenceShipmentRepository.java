/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.shipping;

import javax.enterprise.context.ApplicationScoped;

import javax.inject.Inject;

import com.oracle.coherence.cdi.Name;
import com.tangosol.net.NamedMap;
import org.eclipse.microprofile.opentracing.Traced;

import java.util.Map;

/**
 * An implementation of {@link ShipmentRepository}
 * that that uses Coherence as a backend data store.
 */
@ApplicationScoped
@Traced
public class CoherenceShipmentRepository implements ShipmentRepository {
    protected Map<String, Shipment> shipments;

    @Inject
    public CoherenceShipmentRepository(@Name("shipments") NamedMap<String, Shipment> shipments) {
        this.shipments = shipments;
    }

    @Override
    public Shipment getShipment(String orderId) {
        return shipments.get(orderId);
    }

    @Override
    public void saveShipment(Shipment shipment) {
        shipments.put(shipment.getOrderId(), shipment);
    }
}
