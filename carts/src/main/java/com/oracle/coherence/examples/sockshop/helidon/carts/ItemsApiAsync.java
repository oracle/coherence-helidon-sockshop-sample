/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.carts;

import java.util.List;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * REST API for {@code /items} sub-resource.
 */
public interface ItemsApiAsync {
    @GET
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Return the list of products in the customer's shopping cart")
    @APIResponse(
            responseCode = "200",
            description = "The list of products in the customer's shopping cart",
            content = @Content(mediaType = APPLICATION_JSON,
                               schema = @Schema(type = SchemaType.ARRAY,
                                                implementation = Item.class)
            ))
    CompletionStage<List<Item>> getItems();

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Add item to the shopping cart",
               description = "This operation will add item to the shopping cart if it "
                       + "doesn't already exist, or increment quantity by the specified "
                       + "number of items if it does")
    @APIResponse(responseCode = "201",
                 description = "Added item",
                 content = @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = Item.class)))
    CompletionStage<Response> addItem(@RequestBody(description = "Item to add to the cart") Item item);

    @GET
    @Path("{itemId}")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Return specified item from the shopping cart")
    @APIResponses({
        @APIResponse(responseCode = "200",
                     description = "If specified item exists in the cart",
                     content = @Content(mediaType = APPLICATION_JSON,
                                        schema = @Schema(implementation = Item.class))),
        @APIResponse(responseCode = "404",
                     description = "If specified item does not exist in the cart")
    })
    CompletionStage<Response> getItem(
            @Parameter(name = "itemId", description = "Item identifier")
            @PathParam("itemId") String itemId);

    @DELETE
    @Path("{itemId}")
    @Operation(summary = "Remove specified item from the shopping cart, if it exists")
    @APIResponse(responseCode = "202", description = "Regardless of whether the specified item exists in the cart")
    CompletionStage<Response> deleteItem(
            @Parameter(name = "itemId", description = "Item identifier")
            @PathParam("itemId") String itemId);

    @PATCH
    @Consumes(APPLICATION_JSON)
    @Operation(summary = "Update item in a shopping cart",
               description = "This operation will add item to the shopping cart if it "
                       + "doesn't already exist, or replace it with the specified item "
                       + "if it does")
    @APIResponse(responseCode = "202", description = "Regardless of whether the specified item exists in the cart")
    CompletionStage<Response> updateItem(@RequestBody(description = "Item to update") Item item);
}
