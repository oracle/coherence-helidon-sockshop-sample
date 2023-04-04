/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.catalog;

import jakarta.ws.rs.GET;

import org.eclipse.microprofile.openapi.annotations.Operation;

/**
 * REST API for {@code /catalog} service.
 */
public interface TagApi {
    @GET
    @Operation(summary = "Return all tags")
    TagsResource.Tags getTags();
}
