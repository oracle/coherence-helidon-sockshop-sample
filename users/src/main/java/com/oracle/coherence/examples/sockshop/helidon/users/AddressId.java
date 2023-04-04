/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.users;

import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.annotation.JsonbTypeAdapter;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * Composite key for the {@link Address class} when using JPA.
 */
@JsonbTypeAdapter(AddressId.JsonAdapter.class)
@Data
public class AddressId implements Serializable {
    /**
     * The customer Id that the address is associated with.
     */
    private String user;

    /**
     * The id for the address.
     */
    private String addressId;

    @Builder
    public AddressId(String id) {
        String[] parts = id.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Address Id is in the wrong format");
        }
        user = parts[0];
        addressId = parts[1];
    }

    /**
     * Default constructor.
     */
    public AddressId() {}

    /**
     * Construct an instance of {@code AddressId} with the specified parameters.
     */
    public AddressId(String user, String addressId) {
        this.user = user;
        this.addressId = addressId;
    }

    @Override
    public String toString() {
        return user + ":" + addressId;
    }

    public static class JsonAdapter implements JsonbAdapter<AddressId, String> {
        @Override
        public String adaptToJson(AddressId id) throws Exception {
            return id.toString();
        }

        @Override
        public AddressId adaptFromJson(String id) throws Exception {
            return new AddressId(id);
        }
    }
}
