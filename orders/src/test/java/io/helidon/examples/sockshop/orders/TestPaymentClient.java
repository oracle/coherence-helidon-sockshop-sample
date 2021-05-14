/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.orders;

import javax.enterprise.context.ApplicationScoped;

import static io.helidon.examples.sockshop.orders.TestDataFactory.payment;

@Mock
@ApplicationScoped
public class TestPaymentClient implements PaymentClient {
   public TestPaymentClient() {
   }

   public Payment authorize(PaymentRequest request) {
      return payment(request.getCustomer().getId());
   }
}
