/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.users;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public interface CustomerApi {
    @GET
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Return all customers; or empty collection if no customer found")
    @APIResponses({
          @APIResponse(responseCode = "200", description = "if the retrieval is successful")
    })
    Response getAllCustomers();

    @GET
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Return customer for the specified identifier")
    @APIResponses({
          @APIResponse(responseCode = "200", description = "if the retrieval is successful")
    })
    Response getCustomer(@Parameter(description = "Customer identifier")
                         @PathParam("id") String id);

    @DELETE
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Delete customer for the specified identifier")
    @APIResponses({
          @APIResponse(responseCode = "200", description = "if the delete is successful")
    })
    Response deleteCustomer(@Parameter(description = "Customer identifier")
                            @PathParam("id") String id);

    @GET
    @Path("{id}/cards")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Return all cards for the specified customer identifier")
    @APIResponses({
          @APIResponse(responseCode = "200", description = "if the retrieval is successful")
    })
    Response getCustomerCards(@Parameter(description = "Customer identifier")
                              @PathParam("id") String id);

    @GET
    @Path("{id}/addresses")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Return all addresses for the specified customer identifier")
    @APIResponses({
          @APIResponse(responseCode = "200", description = "if the retrieval is successful")
    })
    Response getCustomerAddresses(@Parameter(description = "Customer identifier")
                                  @PathParam("id") String id);
}
