/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.payment;

import io.helidon.microprofile.grpc.core.GrpcMarshaller;
import io.helidon.microprofile.grpc.core.Grpc;
import io.helidon.microprofile.grpc.core.Unary;

import java.util.Collection;

@Grpc(name = "PaymentGrpc")
@GrpcMarshaller("jsonb")
public interface PaymentClient {
    @Unary
    Authorization authorize(PaymentRequest request);

    @Unary
    Collection<? extends Authorization> getOrderAuthorizations(String orderId);
}
