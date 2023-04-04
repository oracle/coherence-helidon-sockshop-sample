/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.users;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public interface AddressApi {
    @GET
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Return all addresses associated with a user; or an empty list if no address found")
    @APIResponses({
          @APIResponse(responseCode = "200", description = "if the retrieval is successful")
    })
    Response getAllAddresses();

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Register address for a user; no-op if the address exist")
    @APIResponses({
          @APIResponse(responseCode = "200", description = "if address is successfully registered")
    })
    Response registerAddress(@Parameter(description = "Add Address request") AddressesResource.AddAddressRequest req);

    @GET
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Return addresses for the specified identifier")
    Address getAddress(@Parameter(description = "Address identifier")
                       @PathParam("id") AddressId id);

    @DELETE
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Delete address for the specified identifier")
    @APIResponses({
          @APIResponse(responseCode = "200", description = "if address is successfully deleted")
    })
    Response deleteAddress(@Parameter(description = "Address identifier")
                           @PathParam("id") AddressId id);
}
