/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.describer;

import org.mule.metadata.api.ClassTypeLoader;
import org.mule.metadata.api.model.MetadataType;

public interface OfType
{

    TypeWrapper<?> getType();

    default MetadataType getMetadataType(ClassTypeLoader typeLoader){
        return typeLoader.load(getType().getDeclaredClass());
    }
}
