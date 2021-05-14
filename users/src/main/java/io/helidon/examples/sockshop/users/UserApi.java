/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.users;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

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
