/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.orders;

import java.util.Collections;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.Status.NOT_ACCEPTABLE;

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