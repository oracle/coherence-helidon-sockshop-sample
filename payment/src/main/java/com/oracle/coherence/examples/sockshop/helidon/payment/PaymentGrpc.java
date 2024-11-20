/*
 * Copyright (c) 2020, 2024, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.payment;

import io.helidon.grpc.api.Grpc;

import jakarta.enterprise.context.ApplicationScoped;

import jakarta.inject.Inject;

import java.util.Collection;

import org.eclipse.microprofile.metrics.annotation.Counted;

/**
 * Implementation of the Payment Service gRPC API.
 */
@ApplicationScoped
@Grpc.GrpcService("PaymentGrpc")
@Grpc.GrpcMarshaller("jsonb")
public class PaymentGrpc {
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

    @Grpc.Unary
    public Collection<? extends Authorization> getOrderAuthorizations(String orderId) {
        return payments.findAuthorizationsByOrder(orderId);
    }

    @Grpc.Unary
    @Counted
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
