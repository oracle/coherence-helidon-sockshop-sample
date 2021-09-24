/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.shipping;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public interface ShippingApi {
    @GET
    @Path("{orderId}")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Return the Shipment for the specified order")
    Shipment getShipmentByOrderId(@Parameter(description = "Order identifier")
                                  @PathParam("orderId") String orderId);

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Ship the specified shipping request")
    Shipment ship(@Parameter(description = "Shipping request") ShippingRequest req);
}
