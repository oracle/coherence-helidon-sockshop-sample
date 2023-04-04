/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.orders;

import java.util.Collections;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import static jakarta.ws.rs.core.Response.Status.NOT_ACCEPTABLE;

/**
 * Exception mapper for {@code OrderException}s.
 */
@ApplicationScoped
@Provider
public class OrderExceptionMapper
        implements ExceptionMapper<OrderException> {
    @Override
    public Response toResponse(OrderException exception)
    {
        return Response
                .status(NOT_ACCEPTABLE)
                .entity(Collections.singletonMap("message", exception.getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}