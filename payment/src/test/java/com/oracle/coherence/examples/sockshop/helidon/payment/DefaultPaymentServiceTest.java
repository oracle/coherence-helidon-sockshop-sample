/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.payment;

import java.time.LocalDateTime;

import org.eclipse.microprofile.metrics.Counter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.oracle.coherence.examples.sockshop.helidon.payment.TestDataFactory.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link DefaultPaymentService}.
 */
public class DefaultPaymentServiceTest {

    private Counter paymentSuccess = mock(Counter.class);
    private Counter paymentFailure = mock(Counter.class);
    private PaymentService service;

    @BeforeEach
    void initCounters() {
        service = new DefaultPaymentService(100, paymentSuccess, paymentFailure);
    }

    @Test
    void testSuccessfulAuthorization() {
        Authorization auth = service.authorize("A123", "Homer", "Simpson", card(), address(), 50);

        assertThat(auth.getOrderId(), is("A123"));
        assertThat(auth.getTime(), lessThanOrEqualTo(LocalDateTime.now()));
        assertThat(auth.isAuthorised(), is(true));
        assertThat(auth.getMessage(), is("Payment authorized."));
        assertThat(auth.getError(), nullValue());
    }

    @Test
    void testDeclinedAuthorization() {
        Authorization auth = service.authorize("A123", "Homer", "Simpson", card(), address(), 150);

        assertThat(auth.getOrderId(), is("A123"));
        assertThat(auth.getTime(), lessThanOrEqualTo(LocalDateTime.now()));
        assertThat(auth.isAuthorised(), is(false));
        assertThat(auth.getMessage(), is("Payment declined: amount exceeds 100.00"));
        assertThat(auth.getError(), nullValue());
    }

    @Test
    void testInvalidAmount() {
        Authorization auth = service.authorize("A123", "Homer", "Simpson", card(), address(), -25);

        assertThat(auth.getOrderId(), is("A123"));
        assertThat(auth.getTime(), lessThanOrEqualTo(LocalDateTime.now()));
        assertThat(auth.isAuthorised(), is(false));
        assertThat(auth.getMessage(), is("Invalid payment amount."));
        assertThat(auth.getError(), nullValue());
    }
}
