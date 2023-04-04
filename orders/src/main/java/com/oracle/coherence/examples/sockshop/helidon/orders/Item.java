/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.orders;

import java.io.Serializable;

import jakarta.json.bind.annotation.JsonbTransient;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Representation of a single order item.
 */
@Data
@NoArgsConstructor
public class Item implements Serializable {
    /**
     * The item identifier.
     */
    @Schema(description = "The item identifier")
    private String itemId;

    /**
     * The item quantity.
     */
    @Schema(description = "The item quantity")
    private int quantity;

    /**
     * The item's price per unit.
     */
    @Schema(description = "The item's price per unit")
    private float unitPrice;

    /**
     * The order this item belongs to, for JPA optimization.
     */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonbTransient
    private Order order;

    @Builder
    Item(String itemId, int quantity, float unitPrice) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
}
