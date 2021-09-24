/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.users;

import java.util.Map;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;

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
