/*
 * Copyright (c) 2020, 2024, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.carts;

import jakarta.enterprise.inject.spi.CDI;
import org.junit.jupiter.api.Disabled;


/**
 * Integration tests for {@link CartResourceAsync}.
 */
@Disabled("https://github.com/helidon-io/helidon/issues/8416")
public class CartResourceAsyncIT extends CartResourceIT {
    protected String getBasePath() {
        return "/carts-async";
    }

    protected CartRepository getCartsRepository() {
        return new SyncCartRepository(CDI.current().select(CartRepositoryAsync.class).get());
    }
}
