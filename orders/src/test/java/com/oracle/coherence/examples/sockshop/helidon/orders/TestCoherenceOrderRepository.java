/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.orders;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import com.oracle.coherence.cdi.Name;
import com.tangosol.net.NamedMap;

import static javax.interceptor.Interceptor.Priority.APPLICATION;

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
