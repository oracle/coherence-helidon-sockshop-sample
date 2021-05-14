/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.orders;

import javax.enterprise.context.ApplicationScoped;

import static io.helidon.examples.sockshop.orders.TestDataFactory.shipment;

@Mock
@ApplicationScoped
public class TestShippingClient implements ShippingClient {
   public TestShippingClient() {
   }

   public Shipment ship(ShippingRequest request) {
      return shipment(request.getCustomer().getId());
   }
}
