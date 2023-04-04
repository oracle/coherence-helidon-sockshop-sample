/*
 * Copyright (c) 2020,2022 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.carts;

import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * Implementation of the Cart Service REST API.
 */
@ApplicationScoped
@Path("/carts-async")
public class CartResourceAsync implements CartApiAsync {

    @Inject
    private CartRepositoryAsync carts;

    @Override
    public CompletionStage<Cart> getCart(String customerId) {
        return carts.getOrCreateCart(customerId);
    }

    @Override
    public CompletionStage<Response> deleteCart(String customerId) {
        return carts.deleteCart(customerId)
                .thenApply(deleted ->
                        deleted
                         ? Response.accepted().build()
                         : Response.status(Response.Status.NOT_FOUND).build());
    }

    @Override
    public CompletionStage<Response> mergeCarts(String customerId, String sessionId) {
        return carts.mergeCarts(customerId, sessionId)
                .thenApply(fMerged ->
                       fMerged
                        ? Response.accepted().build()
                        : Response.status(Response.Status.NOT_FOUND).build());
    }

    @Override
    public ItemsApiAsync getItems(String customerId) {
        return new ItemsResourceAsync(carts, customerId);
    }
}
