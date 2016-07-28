/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.describer;


import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.UseConfig;
import org.mule.runtime.extension.api.introspection.Named;

public interface ExtensionParameter extends OfType, Annotated, Named
{

    default boolean shouldBeAdvertised()
    {
        return !(isAnnotatedWith(UseConfig.class) || isAnnotatedWith(Connection.class));
    }

    default boolean isRequired()
    {
        return !(isAnnotatedWith(Optional.class));
    }

    default java.util.Optional<String> defaultValue()
    {
        java.util.Optional<String> optionalDefaultValue = java.util.Optional.empty();
        if (!isRequired())
        {
            final java.util.Optional<Optional> annotation = getAnnotation(Optional.class);
            final Optional optionalAnnotation = annotation.get();
            final String defaultValue = optionalAnnotation.defaultValue();
            if (!defaultValue.equals(Optional.NULL))
            {
                optionalDefaultValue = java.util.Optional.of(defaultValue);
            }
        }
        return optionalDefaultValue;
    }

    default String getFinalName()
    {
        final java.util.Optional<Alias> alias = getAnnotation(Alias.class);
        return alias.isPresent() ? alias.get().value() : getName();
    }
}
