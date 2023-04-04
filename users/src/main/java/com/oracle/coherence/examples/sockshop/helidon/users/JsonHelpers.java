/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.users;

import java.util.Map;

import jakarta.json.Json;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonObjectBuilder;

import static java.util.Collections.singletonMap;

abstract class JsonHelpers {
    private static final JsonBuilderFactory JSON = Json.createBuilderFactory(null);

    static JsonObjectBuilder obj() {
        return JSON.createObjectBuilder();
    }

    static Map<String, Object> embed(String name, Object value) {
        return singletonMap("_embedded", singletonMap(name, value));
    }
}
