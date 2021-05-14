/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.users;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.oracle.coherence.cdi.Name;
import com.tangosol.net.NamedMap;
import org.eclipse.microprofile.opentracing.Traced;

import java.util.Collection;

/**
 * An implementation of {@link io.helidon.examples.sockshop.users.UserRepository}
 * that that uses Coherence as a backend data store.
 */
@ApplicationScoped
@Traced
public class CoherenceUserRepository implements UserRepository {

    protected NamedMap<String, User> users;

    @Inject
    public CoherenceUserRepository(@Name("users") NamedMap<String, User> users) {
        this.users = users;
    }

    @Override
    public Address getAddress(AddressId id) {
        return getOrCreate(id.getUser()).getAddress(id.getAddressId());
    }

    @Override
    public AddressId addAddress(String userID, Address address) {
        return users.invoke(userID, entry -> {
            User u = entry.getValue(new User(entry.getKey()));
            Address addr = u.addAddress(address);
            entry.setValue(u);
            return addr.getId();
        });
    }

    @Override
    public void removeAddress(AddressId id) {
        String userID = id.getUser();
        users.invoke(userID, entry -> {
            User u = entry.getValue(new User(entry.getKey()));
            entry.setValue(u.removeAddress(id.getAddressId()));
            return null;
        });
    }

    @Override
    public CardId addCard(String userID, Card card) {
        return users.invoke(userID, entry -> {
            User u = entry.getValue(new User(entry.getKey()));
            Card c = u.addCard(card);
            entry.setValue(u);
            return c.getId();
        });
    }

    @Override
    public Card getCard(CardId id) {
        return getOrCreate(id.getUser()).getCard(id.getCardId());
    }

    @Override
    public void removeCard(CardId id) {
        String userId = id.getUser();
        users.invoke(userId, entry -> {
            User u = entry.getValue(new User(entry.getKey()));
            entry.setValue(u.removeCard(id.getCardId()));
            return null;
        });
    }

    @Override
    public Collection<? extends User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getOrCreate(String id) {
        return users.getOrDefault(id, new User(id));
    }

    @Override
    public User getUser(String id) {
        return users.get(id);
    }

    @Override
    public User removeUser(String id) {
        return users.remove(id);
    }

    @Override
    public boolean authenticate(String username, String password) {
        return users.invoke(username, entry -> {
            User u = entry.getValue(new User(entry.getKey()));
            return u.authenticate(password);
        });
    }

    @Override
    public User register(User user) {
        return users.putIfAbsent(user.getUsername(), user);
    }
}
