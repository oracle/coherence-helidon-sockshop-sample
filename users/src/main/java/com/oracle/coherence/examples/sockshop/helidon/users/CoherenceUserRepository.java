/*
 * Copyright (c) 2020, 2024 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.users;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.oracle.coherence.cdi.Name;
import com.tangosol.net.NamedMap;

import java.util.Collection;

/**
 * An implementation of {@link UserRepository}
 * that that uses Coherence as a backend data store.
 */
@ApplicationScoped
public class CoherenceUserRepository implements UserRepository {

    protected NamedMap<String, User> users;

    @Inject
    public CoherenceUserRepository(@Name("users") NamedMap<String, User> users) {
        this.users = users;
    }

    @Override
    @WithSpan
    public Address getAddress(AddressId id) {
        return getOrCreate(id.getUser()).getAddress(id.getAddressId());
    }

    @Override
    @WithSpan
    public AddressId addAddress(String userID, Address address) {
        return users.invoke(userID, entry -> {
            User u = entry.getValue(new User(entry.getKey()));
            Address addr = u.addAddress(address);
            entry.setValue(u);
            return addr.getId();
        });
    }

    @Override
    @WithSpan
    public void removeAddress(AddressId id) {
        String userID = id.getUser();
        users.invoke(userID, entry -> {
            User u = entry.getValue(new User(entry.getKey()));
            entry.setValue(u.removeAddress(id.getAddressId()));
            return null;
        });
    }

    @Override
    @WithSpan
    public CardId addCard(String userID, Card card) {
        return users.invoke(userID, entry -> {
            User u = entry.getValue(new User(entry.getKey()));
            Card c = u.addCard(card);
            entry.setValue(u);
            return c.getId();
        });
    }

    @Override
    @WithSpan
    public Card getCard(CardId id) {
        return getOrCreate(id.getUser()).getCard(id.getCardId());
    }

    @Override
    @WithSpan
    public void removeCard(CardId id) {
        String userId = id.getUser();
        users.invoke(userId, entry -> {
            User u = entry.getValue(new User(entry.getKey()));
            entry.setValue(u.removeCard(id.getCardId()));
            return null;
        });
    }

    @Override
    @WithSpan
    public Collection<? extends User> getAllUsers() {
        return users.values();
    }

    @Override
    @WithSpan
    public User getOrCreate(String id) {
        return users.getOrDefault(id, new User(id));
    }

    @Override
    @WithSpan
    public User getUser(String id) {
        return users.get(id);
    }

    @Override
    @WithSpan
    public User removeUser(String id) {
        return users.remove(id);
    }

    @Override
    @WithSpan
    public boolean authenticate(String username, String password) {
        return users.invoke(username, entry -> {
            User u = entry.getValue(new User(entry.getKey()));
            return u.authenticate(password);
        });
    }

    @Override
    @WithSpan
    public User register(User user) {
        return users.putIfAbsent(user.getUsername(), user);
    }
}
