/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.shipping;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import com.oracle.coherence.cdi.Name;
import com.tangosol.net.NamedMap;

import static javax.interceptor.Interceptor.Priority.APPLICATION;

@Alternative
@Priority(APPLICATION + 5)
public class TestCoherenceShipmentRepository extends CoherenceShipmentRepository implements TestShipmentRepository {
    @Inject
    TestCoherenceShipmentRepository(@Name("shipments") NamedMap<String, Shipment> shipments) {
        super(shipments);
    }

    @Override
    public void clear() {
        shipments.clear();
    }
}
