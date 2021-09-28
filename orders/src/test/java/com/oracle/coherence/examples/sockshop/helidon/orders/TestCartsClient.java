/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.orders;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import static com.oracle.coherence.examples.sockshop.helidon.orders.TestDataFactory.items;

@Mock
@ApplicationScoped
public class TestCartsClient implements CartsClient {
   public TestCartsClient() {
   }

   public List<Item> cart(String cartId) {
      return items(3);
   }
}
