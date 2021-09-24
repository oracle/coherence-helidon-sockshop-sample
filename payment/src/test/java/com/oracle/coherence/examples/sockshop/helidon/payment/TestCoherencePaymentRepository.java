/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.payment;

import com.oracle.coherence.cdi.Name;
import com.tangosol.net.NamedMap;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import static javax.interceptor.Interceptor.Priority.APPLICATION;

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
