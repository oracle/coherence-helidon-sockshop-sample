/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.orders;

import jakarta.enterprise.context.ApplicationScoped;

import static com.oracle.coherence.examples.sockshop.helidon.orders.TestDataFactory.shipment;

@Mock
@ApplicationScoped
public class TestShippingClient implements ShippingClient {
   public TestShippingClient() {
   }

   public Shipment ship(ShippingRequest request) {
      return shipment(request.getCustomer().getId());
   }
}
