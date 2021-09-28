/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.orders;

import java.lang.annotation.Annotation;
import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Qualifier
@Retention(RUNTIME)
public @interface Mock {
    public static Mock INSTANCE = new Mock(){
        @Override
        public Class<? extends Annotation> annotationType() {
            return Mock.class;
        }
    };
}
