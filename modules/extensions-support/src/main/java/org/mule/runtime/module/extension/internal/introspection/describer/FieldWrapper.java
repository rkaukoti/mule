/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.describer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;

public class FieldWrapper implements ExtensionParameter
{

    private final Field field;

    public FieldWrapper(Field field)
    {
        this.field = field;
    }

    public Field getField()
    {
        return field;
    }

    @Override
    public String getName()
    {
        return field.getName();
    }

    @Override
    public Annotation[] getAnnotations()
    {
        return field.getAnnotations();
    }

    @Override
    public <A extends Annotation> Optional<A> getAnnotation(Class<A> annotationClass)
    {
        return Optional.ofNullable(field.getAnnotation(annotationClass));
    }

    @Override
    public TypeWrapper getType()
    {
        return new TypeWrapper<>(field.getType());
    }
}