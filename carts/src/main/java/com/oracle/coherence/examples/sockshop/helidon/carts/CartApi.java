/*
 * Copyright (c) 2020,2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.carts;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * REST API for {@code /carts} service.
 */
public interface CartApi {
    @GET
    @Path("{customerId}")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Return customer's shopping cart")
    Cart getCart(@Parameter(name = "customerId", description = "Customer identifier")
                 @PathParam("customerId") String customerId);

    @DELETE
    @Path("{customerId}")
    @Operation(summary = "Delete customer's shopping cart")
    @APIResponses({
        @APIResponse(responseCode = "202", description = "if the shopping cart was successfully deleted"),
        @APIResponse(responseCode = "404", description = "if the shopping cart doesn't exist")
    })
    Response deleteCart(@Parameter(name = "customerId", description = "Customer identifier")
                        @PathParam("customerId") String customerId);

    @GET
    @Path("{customerId}/merge")
    @Operation(summary = "Merge one shopping cart into another",
               description = "Customer can add products to a shopping cart anonymously, "
                       + "but when she logs in the anonymous shopping cart needs to be merged "
                       + "into the customer's own shopping cart")
    @APIResponses({
        @APIResponse(responseCode = "202", description = "if the shopping carts were successfully merged"),
        @APIResponse(responseCode = "404", description = "if the session shopping cart doesn't exist")
    })
    Response mergeCarts(@Parameter(name = "customerId", description = "Customer identifier")
                        @PathParam("customerId") String customerId,
                        @Parameter(name = "sessionId", required = true, description = "Anonymous session identifier")
                        @QueryParam("sessionId") String sessionId);

    @Path("{customerId}/items")
    @Operation
    ItemsApi getItems(
            @Parameter(name = "customerId", description = "Customer identifier")
            @PathParam("customerId") String customerId);
}
