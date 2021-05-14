/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.orders;

import java.io.Serializable;

import javax.json.bind.annotation.JsonbProperty;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Customer information.
 */
@Data
@NoArgsConstructor
public class Customer implements Serializable {
    /**
     * Customer identifier.
     */
    @JsonbProperty("username")
    @Schema(description = "Customer identifier")
    private String id;

    /**
     * First name.
     */
    @Schema(description = "First name")
    private String firstName;

    /**
     * Last name.
     */
    @Schema(description = "Last name")
    private String lastName;

    /**
     * Customer's email.
     */
    @Schema(description = "Customer's email")
    private String email;

    @Builder
    Customer(String id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
