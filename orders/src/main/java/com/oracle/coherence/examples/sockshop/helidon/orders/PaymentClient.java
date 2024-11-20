/*
 * Copyright (c) 2020, 2024, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.orders;

import io.helidon.grpc.api.Grpc;

@Grpc.GrpcService("PaymentGrpc")
@Grpc.GrpcChannel("payment")
@Grpc.GrpcMarshaller("jsonb")
public interface PaymentClient {
   @Grpc.Unary
   Payment authorize(PaymentRequest request);
}
