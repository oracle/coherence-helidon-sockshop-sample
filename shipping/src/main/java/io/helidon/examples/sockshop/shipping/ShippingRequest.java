/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.shipping;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Shipping request that is received from Order service.
 */
@Data
@NoArgsConstructor
@Schema(description = "Shipping request that is received from Order service")
public class ShippingRequest implements Serializable {
    /**
     * Order identifier.
     */
    @Schema(description = "Order identifier")
    private String orderId;

    /**
     * Shipping address.
     */
    @Schema(description = "Shipping address")
    private Address address;

    /**
     * The number of items in the order.
     */
    @Schema(description = "The number of items in the order")
    private int itemCount;

    @Builder
    ShippingRequest(String orderId, Address address, int itemCount) {
        this.orderId = orderId;
        this.address = address;
        this.itemCount = itemCount;
    }
}
