/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.describer;

import java.lang.annotation.Annotation;
import java.util.Optional;

public interface Annotated
{

    Annotation[] getAnnotations();

    <A extends Annotation> Optional<A> getAnnotation(Class<A> annotationClass);

    default boolean isAnnotatedWith(Class<? extends Annotation> annotation)
    {
        for (Annotation foundAnnotation : getAnnotations())
        {
            if (foundAnnotation.annotationType().isAssignableFrom(annotation))
            {
                return true;
            }
        }
        return false;
    }
}
