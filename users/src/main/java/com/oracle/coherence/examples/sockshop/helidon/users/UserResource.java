/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.users;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import io.helidon.security.providers.httpauth.HttpBasicAuthProvider;

import static com.oracle.coherence.examples.sockshop.helidon.users.JsonHelpers.obj;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

/*
 * DISCLAIMER: This is a very naive and insecure implementation of user
 * authentication. It should not be used as an example/blueprint to
 * follow when implementing authentication in custom services. The code
 * below exists purely to provide compatibility with the original front
 * end written for SockShop
 */
@ApplicationScoped
@Path("/")
public class UserResource implements UserApi {
    static final String HEADER_AUTHENTICATION_REQUIRED = "WWW-Authenticate";
    static final String HEADER_AUTHENTICATION = "Authorization";
    static final String BASIC_PREFIX = "Basic ";

    private static final Logger LOGGER = Logger.getLogger(HttpBasicAuthProvider.class.getName());
    private static final Pattern CREDENTIAL_PATTERN = Pattern.compile("(.*):(.*)");

    @Inject
    private UserRepository users;

    @Override
    public Response login(String auth) {
        if (!auth.startsWith(BASIC_PREFIX)) {
            return fail("Basic authentication header is missing");
        }
        String  b64 = auth.substring(BASIC_PREFIX.length());
        String  usernameAndPassword = new String(Base64.getDecoder().decode(b64), StandardCharsets.UTF_8);
        Matcher matcher = CREDENTIAL_PATTERN.matcher(usernameAndPassword);
        if (!matcher.matches()) {
            LOGGER.finest(() -> "Basic authentication header with invalid content: " + usernameAndPassword);
            return fail("Basic authentication header with invalid content");
        }

        final String username = matcher.group(1);
        final String password = matcher.group(2);

        boolean fAuth = users.authenticate(username, password);

        if (fAuth) {
            JsonObject entity = obj()
                    .add("user",
                         obj().add("id", username))
                    .build();
            return Response.ok(entity).build();
        }
        else {
            return fail("Invalid username or password");
        }
    }

    @Override
    public Response register(User user) {
        User prev = users.register(user);
        if (prev != null) {
            return Response.status(CONFLICT).entity("User with that ID already exists").build();
        }
        return Response.ok(obj().add("id", user.getUsername()).build()).build();
    }

    // ---- helpers ---------------------------------------------------------

    private Response fail(String message) {
        return Response
                .status(UNAUTHORIZED)
                .header(HEADER_AUTHENTICATION_REQUIRED, "Basic realm=\"sockshop\"")
                .entity(message)
                .build();
    }
}
