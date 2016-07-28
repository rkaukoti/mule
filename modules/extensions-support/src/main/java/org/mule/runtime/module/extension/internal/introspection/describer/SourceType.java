/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.describer;

import static org.mule.runtime.module.extension.internal.util.IntrospectionUtils.getSuperClassGenerics;

import org.mule.runtime.extension.api.runtime.source.Source;

import java.lang.reflect.Type;
import java.util.List;

public class SourceType<T extends Source> extends TypeBasedComponents<T> implements WithGenerics
{

    private final Class<T> aClass;

    public SourceType(Class<T> aClass)
    {
        super(aClass);
        this.aClass = aClass;
    }

    @Override
    public List<Type> getGenerics()
    {
        return getSuperClassGenerics(aClass, Source.class);
    }
}
