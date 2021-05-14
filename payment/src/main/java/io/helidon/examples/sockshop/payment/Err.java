/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.payment;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Represents an unexpected error.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents an unexpected error")
public class Err implements Serializable {
    /**
     * Error description.
     */
    @Schema(description = "Error description")
    private String message;
}
