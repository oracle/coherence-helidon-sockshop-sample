/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.payment;

import com.oracle.coherence.cdi.Name;
import com.tangosol.net.NamedMap;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;

@Alternative
@Priority(APPLICATION + 5)
public class TestCoherencePaymentRepository extends CoherencePaymentRepository implements TestPaymentRepository {
    @Inject
    TestCoherencePaymentRepository(@Name("payments") NamedMap<AuthorizationId, Authorization> payments) {
        super(payments);
    }

    @Override
    public void clear() {
        payments.clear();
    }
}
