/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.orders;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

import com.oracle.coherence.cdi.Name;
import com.tangosol.net.NamedMap;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;

@Alternative
@Priority(APPLICATION + 5)
public class TestCoherenceOrderRepository extends CoherenceOrderRepository
        implements TestOrderRepository {
    private String lastOrderId;

    @Inject
    public TestCoherenceOrderRepository(@Name("orders") NamedMap<String, Order> orders) {
        super(orders);
    }

    public void clear() {
        orders.clear();
    }

    @Override
    public void saveOrder(Order order) {
        super.saveOrder(order);
        lastOrderId = order.getOrderId();
    }

    @Override
    public String getLastOrderId() {
        return lastOrderId;
    }
}
