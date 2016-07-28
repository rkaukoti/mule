/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.describer;

import static java.util.Collections.emptyList;

import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.Parameter;
import org.mule.runtime.extension.api.annotation.connector.Providers;
import org.mule.runtime.module.extension.internal.util.IntrospectionUtils;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExtensionType<T> extends TypeWrapper<T> implements WithConfigurations, WithProviders, WithOperations, WithParameters
{

    public ExtensionType(Class<T> aClass)
    {
        super(aClass);
    }

    @Override
    public List<TypeWrapper<?>> getConfigurations()
    {
        final Optional<Configurations> optionalConfigurations = this.getAnnotation(Configurations.class);
        if (optionalConfigurations.isPresent())
        {
            final Configurations configurations = optionalConfigurations.get();
            return Stream
                    .of(configurations.value())
                    .map(TypeWrapper::new)
                    .collect(Collectors.toList());
        }
        return emptyList();
    }

    @Override
    public List<TypeWrapper<?>> getConnectionProviders()
    {
        final Optional<Providers> optionalProvider = this.getAnnotation(Providers.class);

        if (optionalProvider.isPresent())
        {
            final Providers providers = optionalProvider.get();
            return Stream
                    .of(providers.value())
                    .map(TypeWrapper::new)
                    .collect(Collectors.toList());
        }
        return emptyList();
    }

    @Override
    public List<OperationType> getOperations()
    {
        final Optional<Operations> optionalOperations = this.getAnnotation(Operations.class);
        if (optionalOperations.isPresent())
        {
            final Operations operations = optionalOperations.get();
            return Stream
                    .of(operations.value())
                    .map(IntrospectionUtils::getOperationMethods)
                    .flatMap(Collection::stream)
                    .map(OperationType::new)
                    .collect(Collectors.toList());
        }
        return emptyList();
    }

    @Override
    public List<ExtensionParameter> getParameters()
    {
        return getAnnotatedFields(Parameter.class).stream().collect(Collectors.toList());
    }

    @Override
    public List<ExtensionParameter> getParametersAnnotatedWith(Class<? extends Annotation> annotationClass)
    {
        return null;
    }
}
