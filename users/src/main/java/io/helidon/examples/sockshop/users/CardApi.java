/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.users;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

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
