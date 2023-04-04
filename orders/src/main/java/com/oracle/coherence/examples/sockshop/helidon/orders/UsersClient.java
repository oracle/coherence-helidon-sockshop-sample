/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.orders;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "http://user/")
public interface UsersClient {
   @Path("/addresses/{addressId}")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   public Address address(@PathParam("addressId") String addressId);

   @Path("/cards/{cardId}")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   public Card card(@PathParam("cardId") String cardId);

   @Path("/customers/{customerId}")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   public Customer customer(@PathParam("customerId") String customerId);
}
