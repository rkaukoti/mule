/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.describer;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.introspection.Named;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OperationType extends MethodWrapper implements WithParameters, WithReturnType, Named, Annotated
{

    private final Method method;

    public OperationType(Method method)
    {
        super(method);
        this.method = method;
    }

    public List<ExtensionParameter> getParameters()
    {
        return Stream
                .of(method.getParameters())
                .map(ParameterWrapper::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExtensionParameter> getParametersAnnotatedWith(Class<? extends Annotation> annotationClass)
    {
        return Stream
                .of(method.getParameters())
                .filter(parameter -> parameter.getAnnotation(annotationClass) != null)
                .map(ParameterWrapper::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getName()
    {
        final Optional<Alias> alias = getAnnotation(Alias.class);
        if (alias.isPresent())
        {
            return alias.get().value();
        }
        return method.getName();
    }

    @Override
    public Annotation[] getAnnotations()
    {
        return method.getAnnotations();
    }

    @Override
    public <A extends Annotation> Optional<A> getAnnotation(Class<A> annotationClass)
    {
        return Optional.ofNullable(method.getAnnotation(annotationClass));
    }

    public Method getDeclaredMethod(){
        return method;
    }

    @Override
    public Type getReturnType()
    {
        return method.getReturnType();
    }
}
