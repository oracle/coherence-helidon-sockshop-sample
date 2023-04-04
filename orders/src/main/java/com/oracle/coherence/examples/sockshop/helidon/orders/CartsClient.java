/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.orders;

import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * Client-side interface for Carts REST service.
 */
@RegisterRestClient(baseUri = "http://carts/")
public interface CartsClient {
   /**
    * Get cart items.
    *
    * @param cartId  cart identifier
    *
    * @return cart items from the specified cart
    */
   @Path("/carts/{cartId}/items")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   public List<Item> cart(@PathParam("cartId") String cartId);
}
