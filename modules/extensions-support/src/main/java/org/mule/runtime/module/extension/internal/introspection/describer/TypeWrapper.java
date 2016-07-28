/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.describer;

import org.mule.runtime.extension.api.introspection.Named;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TypeWrapper<T> implements Annotated, Named
{

    private final Class<T> aClass;

    public TypeWrapper(Class<T> aClass){

        this.aClass = aClass;
    }

    public List<FieldWrapper> getFields(){
        return Stream.of(aClass.getDeclaredFields())
                .map(FieldWrapper::new)
                .collect(Collectors.toList());
    }

    public <A extends Annotation> Optional<A> getAnnotation(Class<A> annotationClass) {
        return Optional.ofNullable(aClass.getAnnotation(annotationClass));
    }

    public List<FieldWrapper> getAnnotatedFields(Class<? extends Annotation> annotation){
        return getFields().stream()
                .filter(field -> field.isAnnotatedWith(annotation))
                .collect(Collectors.toList());
    }

    public Class<T> getDeclaredClass(){
        return aClass;
    }

    @Override
    public Annotation[] getAnnotations()
    {
        return aClass.getAnnotations();
    }

    @Override
    public String getName()
    {
        return aClass.getSimpleName();
    }
}