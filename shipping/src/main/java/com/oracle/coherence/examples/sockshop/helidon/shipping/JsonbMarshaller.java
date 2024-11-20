/*
 * Copyright (c) 2024, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.helidon.shipping;


import io.grpc.MethodDescriptor;
import io.helidon.grpc.core.MarshallerSupplier;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


/**
 * An implementation of a gRPC {@link MethodDescriptor.Marshaller} that
 * uses JSONB for serialization.
 *
 * @param <T> the type of value to be marshalled
 */
public class JsonbMarshaller<T>
        implements MethodDescriptor.Marshaller<T>
    {

    private static final Jsonb JSONB = JsonbBuilder.create();

    private final Class<T> clazz;

    /**
     * Construct {@code JsonbMarshaller} instance.
     *
     * @param clazz the type of object to marshall
     */
    JsonbMarshaller(Class<T> clazz)
        {
        this.clazz = clazz;
        }

    @Override
    public InputStream stream(T obj)
        {
        return new ByteArrayInputStream(JSONB.toJson(obj).getBytes(StandardCharsets.UTF_8));
        }

    @Override
    public T parse(InputStream in)
        {
        return JSONB.fromJson(in, clazz);
        }

    /**
     * A {@link MarshallerSupplier} implementation that supplies
     * instances of {@link JsonbMarshaller}.
     */
    @Dependent
    @Named("jsonb")
    public static class Supplier
            implements MarshallerSupplier
        {
        @Override
        public <T> MethodDescriptor.Marshaller<T> get(Class<T> clazz)
            {
            return new JsonbMarshaller<>(clazz);
            }
        }
    }
