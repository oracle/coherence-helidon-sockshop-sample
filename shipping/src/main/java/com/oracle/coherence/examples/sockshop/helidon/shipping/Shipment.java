/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.shipping;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Shipment information to send as a response to Order service.
 */
@Data
@NoArgsConstructor
@Entity
@Schema(description = "Shipment information to send as a response to Order service")
public class Shipment implements Serializable {
    /**
     * Order identifier.
     */
    @Id
    @Schema(description = "Order identifier")
    private String orderId;

    /**
     * Shipping carrier.
     */
    @Schema(description = "Shipping carrier")
    private String carrier;

    /**
     * Tracking number.
     */
    @Schema(description = "racking number")
    private String trackingNumber;

    /**
     * Estimated delivery date.
     */
    @Schema(description = "Estimated delivery date")
    private LocalDate deliveryDate;

    @Builder
    Shipment(String orderId, String carrier, String trackingNumber, LocalDate deliveryDate) {
        this.orderId = orderId;
        this.carrier = carrier;
        this.trackingNumber = trackingNumber;
        this.deliveryDate = deliveryDate;
    }
}
