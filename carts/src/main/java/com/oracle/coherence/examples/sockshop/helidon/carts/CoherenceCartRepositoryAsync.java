/*
 * Copyright (c) 2020, 2024 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.carts;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;

import com.oracle.coherence.cdi.Name;
import com.tangosol.net.AsyncNamedMap;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * An implementation of {@link CartRepository} that that uses Coherence as a backend data
 * store.
 */
@ApplicationScoped
@Alternative
@Priority(APPLICATION)
public class CoherenceCartRepositoryAsync implements CartRepositoryAsync {
    protected final AsyncNamedMap<String, Cart> carts;

    @Inject
    CoherenceCartRepositoryAsync(@Name("carts") AsyncNamedMap<String, Cart> carts) {
        this.carts = carts;
    }

    @WithSpan
    @Override
    public CompletionStage<Boolean> deleteCart(String customerId) {
        return carts.remove(customerId).thenApply(Objects::nonNull);
    }

    @WithSpan
    @Override
    public CompletionStage<Boolean> mergeCarts(String targetId, String sourceId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        carts.remove(sourceId)
                .whenComplete((source, t1) -> {
                    if (t1 != null) {
                        future.completeExceptionally(t1);
                    } else {
                        if (source == null) {
                            future.complete(false);
                        } else {
                            carts.invoke(targetId, entry -> {
                                Cart cart = entry.getValue(new Cart(entry.getKey()));
                                entry.setValue(cart.merge(source));
                                return null;
                            }).whenComplete((target, t2) -> {
                                if (t2 != null) {
                                    future.completeExceptionally(t2);
                                } else {
                                    future.complete(true);
                                }
                            });
                        }
                    }
                });

        return future;
    }

    @WithSpan
    @Override
    public CompletionStage<Item> getItem(String cartId, String itemId) {
        return getOrCreateCart(cartId).thenApply(cart -> cart.getItem(itemId));
    }

    @WithSpan
    @Override
    public CompletionStage<List<Item>> getItems(String cartId) {
        return getOrCreateCart(cartId).thenApply(Cart::getItems);
    }

    @WithSpan
    @Override
    public CompletionStage<Item> addItem(String cartId, Item item) {
        return carts.invoke(cartId, entry -> {
            Cart cart = entry.getValue(new Cart(entry.getKey()));
            Item newItem = cart.add(item);
            entry.setValue(cart);
            return newItem;
        });
    }

    @WithSpan
    @Override
    public CompletionStage<Item> updateItem(String cartId, Item item) {
        return carts.invoke(cartId, entry -> {
            Cart cart = entry.getValue(new Cart(entry.getKey()));
            Item newItem = cart.update(item);
            entry.setValue(cart);
            return newItem;
        });
    }

    @WithSpan
    @Override
    public CompletionStage<Void> deleteItem(String cartId, String itemId) {
        return carts.invoke(cartId, entry -> {
            Cart cart = entry.getValue(new Cart(entry.getKey()));
            cart.remove(itemId);
            entry.setValue(cart);
            return null;
        }).thenAccept(cart -> {});
    }

    @WithSpan
    @Override
    public CompletionStage<Cart> getOrCreateCart(String customerId) {
        return carts.computeIfAbsent(customerId, v -> new Cart(customerId));
    }
}
