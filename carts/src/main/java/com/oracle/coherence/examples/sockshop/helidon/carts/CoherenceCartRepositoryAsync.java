/*
 * Copyright (c) 2020,2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.carts;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;

import com.oracle.coherence.cdi.Name;
import com.tangosol.net.AsyncNamedMap;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.opentracing.Traced;

/**
 * An implementation of {@link CartRepository} that that uses Coherence as a backend data
 * store.
 */
@ApplicationScoped
@Alternative
@Priority(APPLICATION)
@Traced
public class CoherenceCartRepositoryAsync implements CartRepositoryAsync {
    protected final AsyncNamedMap<String, Cart> carts;

    @Inject
    CoherenceCartRepositoryAsync(@Name("carts") AsyncNamedMap<String, Cart> carts) {
        this.carts = carts;
    }

    @Override
    public CompletionStage<Boolean> deleteCart(String customerId) {
        return carts.remove(customerId).thenApply(Objects::nonNull);
    }

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

    @Override
    public CompletionStage<Item> getItem(String cartId, String itemId) {
        return getOrCreateCart(cartId).thenApply(cart -> cart.getItem(itemId));
    }

    @Override
    public CompletionStage<List<Item>> getItems(String cartId) {
        return getOrCreateCart(cartId).thenApply(Cart::getItems);
    }

    @Override
    public CompletionStage<Item> addItem(String cartId, Item item) {
        return carts.invoke(cartId, entry -> {
            Cart cart = entry.getValue(new Cart(entry.getKey()));
            Item newItem = cart.add(item);
            entry.setValue(cart);
            return newItem;
        });
    }

    @Override
    public CompletionStage<Item> updateItem(String cartId, Item item) {
        return carts.invoke(cartId, entry -> {
            Cart cart = entry.getValue(new Cart(entry.getKey()));
            Item newItem = cart.update(item);
            entry.setValue(cart);
            return newItem;
        });
    }

    @Override
    public CompletionStage<Void> deleteItem(String cartId, String itemId) {
        return carts.invoke(cartId, entry -> {
            Cart cart = entry.getValue(new Cart(entry.getKey()));
            cart.remove(itemId);
            entry.setValue(cart);
            return null;
        }).thenAccept(cart -> {});
    }

    @Override
    public CompletionStage<Cart> getOrCreateCart(String customerId) {
        return carts.computeIfAbsent(customerId, v -> new Cart(customerId));
    }
}
