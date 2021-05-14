/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.payment;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Implementation of the Payment Service REST API.
 */
@ApplicationScoped
@Path("/payments")
public class PaymentResource implements PaymentApi {
    /**
     * Payment repository to use.
     */
    @Inject
    private PaymentRepository payments;

    /**
     * Payment service to use.
     */
    @Inject
    private PaymentService paymentService;

    @Override
    public Response getOrderAuthorizations(String orderId) {
        return Response.ok(payments.findAuthorizationsByOrder(orderId)).build();
    }

    @Override
    public Authorization authorize(PaymentRequest paymentRequest) {
        String firstName = paymentRequest.getCustomer().getFirstName();
        String lastName  = paymentRequest.getCustomer().getLastName();

        Authorization auth = paymentService.authorize(
                paymentRequest.getOrderId(),
                firstName,
                lastName,
                paymentRequest.getCard(),
                paymentRequest.getAddress(),
                paymentRequest.getAmount());

        payments.saveAuthorization(auth);

        return auth;
    }


}
