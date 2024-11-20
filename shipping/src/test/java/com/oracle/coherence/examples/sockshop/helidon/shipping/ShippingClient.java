/*
 * Copyright (c) 2020, 2024 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.shipping;

import io.helidon.grpc.api.Grpc;

@Grpc.GrpcService("ShippingGrpc")
@Grpc.GrpcMarshaller("jsonb")
public interface ShippingClient {
    @Grpc.Unary
    Shipment ship(ShippingRequest request);

    @Grpc.Unary
    Shipment getShipmentByOrderId(String orderId);
}