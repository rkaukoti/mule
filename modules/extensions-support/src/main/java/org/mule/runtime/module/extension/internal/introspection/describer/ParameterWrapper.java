/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.describer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Optional;

public class ParameterWrapper implements ExtensionParameter
{

    private final Parameter parameter;

    public ParameterWrapper(Parameter parameter)
    {
        this.parameter = parameter;
    }

    @Override
    public TypeWrapper<?> getType()
    {
        return new TypeWrapper<>(parameter.getType());
    }

    @Override
    public Annotation[] getAnnotations()
    {
        return parameter.getAnnotations();
    }

    @Override
    public <A extends Annotation> Optional<A> getAnnotation(Class<A> annotationClass)
    {
        return Optional.ofNullable(parameter.getAnnotation(annotationClass));
    }

    @Override
    public String getName()
    {
        return parameter.getName();
    }

    public Parameter getParameter(){
        return parameter;
    }
}
