/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.orders;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public interface OrderApi {
    @GET
    @Path("search/customerId")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Return the orders for the specified customer")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "if orders exist"),
        @APIResponse(responseCode = "404", description = "if orders do not exist")
    })
    Response getOrdersForCustomer(@Parameter(description = "Customer identifier")
                                  @QueryParam("custId") String customerId);

    @GET
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Return the order for the specified order")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "if the order exist"),
        @APIResponse(responseCode = "404", description = "if the order doesn't exist")
    })
    Response getOrder(@Parameter(description = "Order identifier")
                      @PathParam("id") String orderId);

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Place a new order for the specified order request")
    @APIResponses({
          @APIResponse(responseCode = "201", description = "if the request is successfully processed"),
          @APIResponse(responseCode = "406", description = "if the payment is not authorized")
    })
    Response newOrder(@Context UriInfo uriInfo,
                      @Parameter(description = "Order request") NewOrderRequest request);
}
