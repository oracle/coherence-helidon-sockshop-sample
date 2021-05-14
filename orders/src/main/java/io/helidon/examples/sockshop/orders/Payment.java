/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.orders;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Payment authorization received from a Payment service.
 */
@Data
@NoArgsConstructor
public class Payment implements Serializable {
    /**
     * Flag specifying whether the payment was authorized.
     */
    @Schema(description = "Flag specifying whether the payment was authorized")
    private boolean authorised;

    /**
     * Approval or rejection message.
     */
    @Schema(description = "Approval or rejection message")
    private String  message;

    @Builder
    Payment(boolean authorised, String message) {
        this.authorised = authorised;
        this.message = message;
    }
}
