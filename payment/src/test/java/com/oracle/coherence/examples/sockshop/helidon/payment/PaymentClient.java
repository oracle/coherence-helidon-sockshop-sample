/*
 * Copyright (c) 2020, 2024, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.payment;

import io.helidon.grpc.api.Grpc;

import java.util.Collection;

@Grpc.GrpcService("PaymentGrpc")
@Grpc.GrpcMarshaller("jsonb")
public interface PaymentClient {
    @Grpc.Unary
    Authorization authorize(PaymentRequest request);

    @Grpc.Unary
    Collection<? extends Authorization> getOrderAuthorizations(String orderId);
}
