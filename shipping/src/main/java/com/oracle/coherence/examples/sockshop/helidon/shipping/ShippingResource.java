/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.shipping;

import java.time.LocalDate;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

import io.helidon.microprofile.grpc.core.GrpcMarshaller;
import io.helidon.microprofile.grpc.core.Grpc;
import io.helidon.microprofile.grpc.core.Unary;

import org.eclipse.microprofile.metrics.annotation.Timed;

/**
 * Implementation of the Shipping Service REST and gRPC API.
 */
@ApplicationScoped
@Path("/shipping")
@Grpc(name = "ShippingGrpc")
@GrpcMarshaller("jsonb")
@Timed
public class ShippingResource implements ShippingApi {
    /**
     * Shipment repository to use.
     */
    @Inject
    private ShipmentRepository shipments;

    @Override
    @Unary
    public Shipment getShipmentByOrderId(String orderId) {
        return shipments.getShipment(orderId);
    }

    @Override
    @Unary
    public Shipment ship(ShippingRequest req) {
        // defaults
        String carrier = "USPS";
        String trackingNumber = "9205 5000 0000 0000 0000 00";
        LocalDate deliveryDate = LocalDate.now().plusDays(5);

        if (req.getItemCount() == 1) {  // use FedEx
            carrier = "FEDEX";
            trackingNumber = "231300687629630";
            deliveryDate = LocalDate.now().plusDays(1);
        }
        else if (req.getItemCount() <= 3) {  // use UPS
            carrier = "UPS";
            trackingNumber = "1Z999AA10123456784";
            deliveryDate = LocalDate.now().plusDays(3);
        }

        Shipment shipment = Shipment.builder()
                .orderId(req.getOrderId())
                .carrier(carrier)
                .trackingNumber(trackingNumber)
                .deliveryDate(deliveryDate)
                .build();

        shipments.saveShipment(shipment);

        return shipment;
    }
}
