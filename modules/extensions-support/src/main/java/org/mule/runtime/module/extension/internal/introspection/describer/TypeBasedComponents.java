/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.describer;

import org.mule.runtime.extension.api.annotation.Parameter;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

public class TypeBasedComponents<T> extends TypeWrapper<T> implements WithParameters
{

    public TypeBasedComponents(Class<T> aClass)
    {
        super(aClass);
    }

    @Override
    public List<ExtensionParameter> getParameters()
    {
        return getAnnotatedFields(Parameter.class)
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public List<ExtensionParameter> getParametersAnnotatedWith(Class<? extends Annotation> annotationClass)
    {
        return getAnnotatedFields(Parameter.class)
                .stream()
                .filter(field -> field.getAnnotation(annotationClass).isPresent())
                .collect(Collectors.toList());
    }
}
