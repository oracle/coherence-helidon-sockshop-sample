/*
 * Copyright (c) 2020, 2024 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.carts;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

import com.oracle.coherence.cdi.Name;

import com.tangosol.net.NamedMap;

import java.util.List;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;

/**
 * An implementation of {@link CartRepository}
 * that that uses Coherence as a backend data store.
 */
@ApplicationScoped
@Alternative
@Priority(APPLICATION)
public class CoherenceCartRepository implements CartRepository {
    protected final NamedMap<String, Cart> carts;

    @Inject
    CoherenceCartRepository(@Name("carts") NamedMap<String, Cart> carts) {
        this.carts = carts;
    }

    @WithSpan
    @Override
    public Cart getOrCreateCart(String customerId) {
        return carts.computeIfAbsent(customerId, v -> new Cart(customerId));
    }

    @WithSpan
    @Override
    public boolean mergeCarts(String targetId, String sourceId) {
        final Cart source = carts.remove(sourceId);
        if (source == null) {
            return false;
        }

        carts.invoke(targetId, entry -> {
            Cart cart = entry.getValue(new Cart(entry.getKey()));
            entry.setValue(cart.merge(source));
            return null;
        });

        return true;
    }

    @WithSpan
    @Override
    public boolean deleteCart(String customerId) {
        return null != carts.remove(customerId);
    }

    @WithSpan
    @Override
    public Item getItem(String cartId, String itemId) {
        return getOrCreateCart(cartId).getItem(itemId);
    }

    @WithSpan
    @Override
    public List<Item> getItems(String cartId) {
        return getOrCreateCart(cartId).getItems();
    }

    @WithSpan
    @Override
    public Item addItem(String cartId, Item item) {
        return carts.invoke(cartId, entry -> {
            Cart cart = entry.getValue(new Cart(entry.getKey()));
            Item newItem = cart.add(item);
            entry.setValue(cart);
            return newItem;
        });
    }

    @WithSpan
    @Override
    public Item updateItem(String cartId, Item item) {
        return carts.invoke(cartId, entry -> {
            Cart cart = entry.getValue(new Cart(entry.getKey()));
            Item newItem = cart.update(item);
            entry.setValue(cart);
            return newItem;
        });
    }

    @WithSpan
    @Override
    public void deleteItem(String cartId, String itemId) {
        carts.invoke(cartId, entry -> {
            Cart cart = entry.getValue(new Cart(entry.getKey()));
            cart.remove(itemId);
            entry.setValue(cart);
            return null;
        });
    }
}
