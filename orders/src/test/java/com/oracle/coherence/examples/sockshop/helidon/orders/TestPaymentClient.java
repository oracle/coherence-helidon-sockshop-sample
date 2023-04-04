/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.orders;

import jakarta.enterprise.context.ApplicationScoped;

import static com.oracle.coherence.examples.sockshop.helidon.orders.TestDataFactory.payment;

@Mock
@ApplicationScoped
public class TestPaymentClient implements PaymentClient {
   public TestPaymentClient() {
   }

   public Payment authorize(PaymentRequest request) {
      return payment(request.getCustomer().getId());
   }
}
