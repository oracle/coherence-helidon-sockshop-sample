/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.orders;

import io.helidon.microprofile.grpc.client.GrpcChannel;
import io.helidon.microprofile.grpc.core.GrpcMarshaller;
import io.helidon.microprofile.grpc.core.Grpc;
import io.helidon.microprofile.grpc.core.Unary;

@Grpc(name = "ShippingGrpc")
@GrpcChannel(name = "shipping")
@GrpcMarshaller("jsonb")
public interface ShippingClient {
    @Unary
    Shipment ship(ShippingRequest request);
}
