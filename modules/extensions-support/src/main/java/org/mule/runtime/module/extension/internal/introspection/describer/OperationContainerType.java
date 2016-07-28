/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.describer;

import org.mule.runtime.module.extension.internal.util.IntrospectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OperationContainerType<T> extends TypeWrapper<T> implements WithOperations
{

    private final Class<T> aClass;

    public OperationContainerType(Class<T> aClass)
    {
        super(aClass);
        this.aClass = aClass;
    }

    @Override
    public List<OperationType> getOperations()
    {
        return Stream
                .of(aClass)
                .map(IntrospectionUtils::getOperationMethods)
                .flatMap(Collection::stream)
                .map(OperationType::new)
                .collect(Collectors.toList());
    }
}
