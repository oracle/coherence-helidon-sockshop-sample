/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.orders;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.core.MediaType;

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
