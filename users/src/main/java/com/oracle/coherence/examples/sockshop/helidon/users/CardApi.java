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

public interface CardApi {
    @GET
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Return all cards associated with a user")
    @APIResponses({
          @APIResponse(responseCode = "200", description = "if retrieval is successful")
    })
    Response getAllCards();

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Register a credit card for a user; no-op if the card exist")
    @APIResponses({
          @APIResponse(responseCode = "200", description = "if card is successfully registered")
    })
    Response registerCard(@Parameter(description = "Add card request") CardsResource.AddCardRequest req);

    @GET
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Return card for the specified identifier")
    Card getCard(@Parameter(description = "Card identifier")
                       @PathParam("id") CardId id);

    @DELETE
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Delete card for the specified identifier")
    @APIResponses({
          @APIResponse(responseCode = "200", description = "if card is successfully deleted")
    })
    Response deleteCard(@Parameter(description = "Card identifier")
                           @PathParam("id") CardId id);
}
