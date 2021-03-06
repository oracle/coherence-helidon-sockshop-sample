/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.orders;

import java.io.Serializable;


import lombok.Builder;
import lombok.Data;

/**
 * Shipping request that is sent to Shipping service for processing.
 */
@Data
@Builder
public class ShippingRequest implements Serializable {
    /**
     * Order identifier.
     */
    private String orderId;

    /**
     * Customer information.
     */
    private Customer customer;

    /**
     * Shipping address.
     */
    private Address address;

    /**
     * The number of items in the order.
     */
    private int itemCount;
}
