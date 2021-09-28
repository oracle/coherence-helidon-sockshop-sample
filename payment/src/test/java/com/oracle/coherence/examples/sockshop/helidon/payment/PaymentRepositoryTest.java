/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.payment;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.oracle.coherence.examples.sockshop.helidon.payment.TestDataFactory.auth;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * Abstract base class containing tests for all
 * {@link PaymentRepository} implementations.
 */
public abstract class PaymentRepositoryTest {

    private final TestPaymentRepository payments = getPaymentRepository();

    protected abstract TestPaymentRepository getPaymentRepository();

    @BeforeEach
    void setup() {
        payments.clear();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void testSaveAuthorization() {
        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        payments.saveAuthorization(auth("A123", time,true, "Payment processed"));

        Collection<? extends Authorization> auths = payments.findAuthorizationsByOrder("A123");
        assertThat(auths.size(), is(1));

        Authorization auth = auths.stream().findFirst().get();
        assertThat(auth.getOrderId(), is("A123"));
        assertThat(auth.getTime(), is(time));
        assertThat(auth.isAuthorised(), is(true));
        assertThat(auth.getMessage(), is("Payment processed"));
        assertThat(auth.getError(), nullValue());
    }


    @Test
    void testFindAuthorizationsByOrder() {
        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        payments.saveAuthorization(auth("A123", time, new Err("Payment service unavailable")));
        payments.saveAuthorization(auth("A123", time.plusSeconds(5), false, "Payment declined"));
        payments.saveAuthorization(auth("A123", time.plusSeconds(10), true, "Payment processed"));
        payments.saveAuthorization(auth("B456", time, true, "Payment processed"));

        assertThat(payments.findAuthorizationsByOrder("A123").size(), is(3));
        assertThat(payments.findAuthorizationsByOrder("B456").size(), is(1));
        assertThat(payments.findAuthorizationsByOrder("C789").size(), is(0));
    }
}
