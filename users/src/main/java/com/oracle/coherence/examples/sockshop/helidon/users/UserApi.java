/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.users;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public interface UserApi {
    @GET
    @Path("login")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Basic user authentication")
    @APIResponses({
          @APIResponse(responseCode = "200", description = "if user is successfully authenticated"),
          @APIResponse(responseCode = "401", description = "if authentication fail")
    })
    Response login(@Parameter(description = "Basic authentication header")
                              @HeaderParam("Authorization") String auth);

    @POST
    @Path("register")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Register a user")
    @APIResponses({
          @APIResponse(responseCode = "200", description = "if user is successfully registered"),
          @APIResponse(responseCode = "409", description = "if the user is already registered")
    })
    Response register(@Parameter(description = "The user to be registered") User user);
}
