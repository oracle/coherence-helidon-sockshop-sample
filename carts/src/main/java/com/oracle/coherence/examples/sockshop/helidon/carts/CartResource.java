/*
 * Copyright (c) 2020,2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.carts;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * Implementation of the Cart Service REST API.
 */
@ApplicationScoped
@Path("/carts")
public class CartResource implements CartApi {

    @Inject
    private CartRepository carts;

    @Override
    public Cart getCart(String customerId) {
        return carts.getOrCreateCart(customerId);
    }

    @Override
    public Response deleteCart(String customerId) {
        return carts.deleteCart(customerId) ?
                Response.accepted().build() :
                Response.status(Response.Status.NOT_FOUND).build();
    }

    @Override
    public Response mergeCarts(String customerId, String sessionId) {
        boolean fMerged = carts.mergeCarts(customerId, sessionId);
        return fMerged
                ? Response.accepted().build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    @Override
    public ItemsApi getItems(String customerId) {
        return new ItemsResource(carts, customerId);
    }
}
