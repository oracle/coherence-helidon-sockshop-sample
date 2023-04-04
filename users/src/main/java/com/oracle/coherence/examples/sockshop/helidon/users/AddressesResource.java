/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.users;

import java.util.Collections;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import static com.oracle.coherence.examples.sockshop.helidon.users.JsonHelpers.embed;
import static com.oracle.coherence.examples.sockshop.helidon.users.JsonHelpers.obj;

@ApplicationScoped
@Path("/addresses")
public class AddressesResource implements AddressApi{

    @Inject
    private UserRepository users;

    @Override
    public Response getAllAddresses() {
        return Response.ok(embed("address", Collections.emptyList())).build();
    }

    @Override
    public Response registerAddress(AddAddressRequest req) {
        Address address = new Address(req.number, req.street, req.city, req.postcode, req.country);
        AddressId id = users.addAddress(req.userID, address);

        return Response.ok(obj().add("id", id.toString()).build()).build();
    }

    @Override
    public Address getAddress(AddressId id) {
        return users.getAddress(id);
    }

    @Override
    public Response deleteAddress(AddressId id) {
        try {
            users.removeAddress(id);
            return status(true);
        }
        catch (RuntimeException e) {
            return status(false);
        }
    }

    // --- helpers ----------------------------------------------------------

    private static Response status(boolean fSuccess) {
        return Response.ok(obj().add("status", fSuccess).build()).build();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddAddressRequest {
        public String number;
        public String street;
        public String city;
        public String postcode;
        public String country;
        public String userID;
    }
}
