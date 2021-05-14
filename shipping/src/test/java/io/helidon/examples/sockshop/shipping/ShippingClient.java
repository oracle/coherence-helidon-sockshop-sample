/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.shipping;

import io.helidon.microprofile.grpc.core.GrpcMarshaller;
import io.helidon.microprofile.grpc.core.Grpc;
import io.helidon.microprofile.grpc.core.Unary;

@Grpc(name = "ShippingGrpc")
@GrpcMarshaller("jsonb")
public interface ShippingClient {
    @Unary
    Shipment ship(ShippingRequest request);

    @Unary
    Shipment getShipmentByOrderId(String orderId);
}