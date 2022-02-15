/*
 * Copyright (c) 2022 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.helidon.users;

import io.helidon.config.Config;
import io.helidon.microprofile.server.Server;
import io.helidon.tracing.TracerBuilder;

/**
 * Entry point into the application
 */
public class Application {
	public static void main(String... args) {
		Server.create().start();
	}
}
