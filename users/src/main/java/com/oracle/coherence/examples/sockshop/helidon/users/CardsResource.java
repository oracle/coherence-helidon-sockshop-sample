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

@ApplicationScoped
@Path("/cards")
public class CardsResource implements CardApi {

    @Inject
    private UserRepository users;

    @Override
    public Response getAllCards() {
        return Response.ok(JsonHelpers.embed("card", Collections.emptyList())).build();
    }

    @Override
    public Response registerCard(AddCardRequest req) {
        Card card = new Card(req.longNum, req.expires, req.ccv);
        CardId id = users.addCard(req.userID, card);

        return Response.ok(JsonHelpers.obj().add("id", id.toString()).build()).build();
    }

    @Override
    public Card getCard(CardId id) {
        return users.getCard(id).mask();
    }

    @Override
    public Response deleteCard(CardId id) {
        try {
            users.removeCard(id);
            return status(true);
        }
        catch (RuntimeException e) {
            return status(false);
        }
    }

    // --- helpers ----------------------------------------------------------

    private static Response status(boolean fSuccess) {
        return Response.ok(JsonHelpers.obj().add("status", fSuccess).build()).build();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddCardRequest {
        public String longNum;
        public String expires;
        public String ccv;
        public String userID;
    }
}
