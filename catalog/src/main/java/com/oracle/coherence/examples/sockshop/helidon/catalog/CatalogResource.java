/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.catalog;

import java.io.InputStream;
import java.util.Collection;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * Implementation of the Catalog Service {@code /catalogue} API.
 */
@ApplicationScoped
@Path("/catalogue")
public class CatalogResource implements CatalogApi{
    private static final Logger LOGGER = Logger.getLogger(CatalogResource.class.getName());

    @Inject
    private CatalogRepository catalog;

    @Override
    public Collection<? extends Sock> getSocks(String tags, String order, int pageNum, int pageSize) {
        LOGGER.info("CatalogResource.getSocks: size=" + pageSize);
        return catalog.getSocks(tags, order, pageNum, pageSize);
    }

    @Override
    public Count getSockCount(String tags) {
        return new Count(catalog.getSockCount(tags));
    }

    @Override
    public Response getSock(String sockId) {
        Sock sock = catalog.getSock(sockId);
        return sock == null
                ? Response.status(NOT_FOUND).build()
                : Response.ok(sock).build();
    }

    @Override
    public Response getImage(String image) {
        InputStream img = getClass().getClassLoader().getResourceAsStream("web/images/" + image);
        return img == null
                ? Response.status(NOT_FOUND).build()
                : Response.ok(img).build();
    }

    public static class Count {
        public long size;
        public Object err;

        public Count(long size) {
            this.size = size;
        }
    }
}
