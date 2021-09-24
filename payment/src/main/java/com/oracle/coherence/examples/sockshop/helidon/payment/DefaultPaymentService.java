/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.payment;

import java.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.annotation.Metric;
import org.eclipse.microprofile.opentracing.Traced;

/**
 * Trivial {@link PaymentService} implementation for demo and testing purposes.
 * <p/>
 * It approves all payment requests with the total amount lower or equal
 * to the {@code payment.limit} configuration property (100 by default),
 * and declines all requests above that amount.
 */
@ApplicationScoped
@Traced
public class DefaultPaymentService implements PaymentService {
    /**
     * Payment limit
     */
    private float paymentLimit;

    @Inject
    @Metric(name = "payment.success")
    private Counter paymentSuccess;

    @Inject
    @Metric(name = "payment.failure")
    private Counter paymentFailure;

    /**
     * Construct {@code DefaultPaymentService} instance.
     */
    public DefaultPaymentService() {
    }

     /**
     * Construct {@code DefaultPaymentService} instance with {@link Counter}s for testing purposes.
     */
    public DefaultPaymentService(float paymentLimit, Counter paymentSuccess, Counter paymentFailure) {
        this.paymentSuccess = paymentSuccess;
        this.paymentFailure = paymentFailure;
        this.paymentLimit = paymentLimit;
    }

    /**
     * Construct {@code DefaultPaymentService} instance.
     *
     * @param paymentLimit payment limit
     */
    @Inject
    public DefaultPaymentService(@ConfigProperty(name = "payment.limit", defaultValue = "100") float paymentLimit) {
        this.paymentLimit = paymentLimit;
    }

    @Override
    public Authorization authorize(String orderId, String firstName, String lastName, Card card, Address address, float amount) {
        boolean fAuthorized = amount > 0 && amount <= paymentLimit;

        String message = fAuthorized ? "Payment authorized." :
                amount <= 0 ? "Invalid payment amount." :
                        "Payment declined: amount exceeds " + String.format("%.2f", paymentLimit);

        if (fAuthorized) {
            paymentSuccess.inc();
        } else {
            paymentFailure.inc();
        }

        return Authorization.builder()
                .orderId(orderId)
                .time(LocalDateTime.now())
                .authorised(fAuthorized)
                .message(message)
                .build();
    }
}
