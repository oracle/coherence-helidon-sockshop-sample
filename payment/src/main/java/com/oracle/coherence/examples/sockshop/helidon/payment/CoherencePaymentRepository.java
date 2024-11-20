/*
 * Copyright (c) 2020, 2024, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.payment;

import com.oracle.coherence.cdi.Name;

import com.tangosol.net.NamedMap;

import io.opentelemetry.instrumentation.annotations.WithSpan;

import jakarta.annotation.PostConstruct;

import jakarta.enterprise.context.ApplicationScoped;

import jakarta.inject.Inject;

import java.util.Collection;

import static com.tangosol.util.Filters.equal;

/**
 * An implementation of {@link PaymentRepository}
 * that that uses Coherence as a backend data store.
 */
@ApplicationScoped
public class CoherencePaymentRepository implements PaymentRepository {
    protected final NamedMap<AuthorizationId, Authorization> payments;

    @Inject
    CoherencePaymentRepository(@Name("payments") NamedMap<AuthorizationId, Authorization> payments) {
        this.payments = payments;
    }

    @PostConstruct
    void createIndexes() {
        payments.addIndex(Authorization::getOrderId, false, null);
    }

    @Override
    @WithSpan
    public void saveAuthorization(Authorization auth) {
        payments.put(auth.getId(), auth);
    }

    @Override
    @WithSpan
    public Collection<? extends Authorization> findAuthorizationsByOrder(String orderId) {
        return payments.values(equal(Authorization::getOrderId, orderId));
    }
}
