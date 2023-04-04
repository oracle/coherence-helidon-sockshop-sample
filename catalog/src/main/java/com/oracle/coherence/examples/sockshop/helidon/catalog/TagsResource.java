/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.catalog;

import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

/**
 * Implementation of the Catalog Service {@code /tags} API.
 */
@ApplicationScoped
@Path("/tags")
public class TagsResource implements TagApi {

    @Inject
    private CatalogRepository catalog;

    @Override
    public Tags getTags() {
        return new Tags(catalog.getTags());
    }

    public static class Tags {
        public Set<String> tags;
        public Object err;

        Tags(Set<String> tags) {
            this.tags = tags;
        }
    }
}
