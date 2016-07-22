/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.runtime.core.model.resolvers;

import org.mule.runtime.core.api.MuleEventContext;
import org.mule.runtime.core.api.transformer.TransformerException;
import org.mule.runtime.core.util.ClassUtils;

/**
 * Allows for arguments with no parameters to be called. Regardless of the payload of the current event, this resolver will always
 * only look for No-Arg servic methods.
 * <p/>
 * Note that the {@link org.mule.runtime.core.model.resolvers.ReflectionEntryPointResolver} supports the resolution of no-arg
 * service methods if the event payload received is of type {@link org.mule.runtime.api.message.NullPayload}.
 *
 * @see org.mule.runtime.core.model.resolvers.ReflectionEntryPointResolver
 * @see org.mule.runtime.api.message.NullPayload
 */
public class NoArgumentsEntryPointResolver extends AbstractArgumentEntryPointResolver {

  @Override
  protected Class<?>[] getMethodArgumentTypes(Object[] payload) {
    return ClassUtils.NO_ARGS_TYPE;
  }

  @Override
  protected Object[] getPayloadFromMessage(MuleEventContext context) throws TransformerException {
    return ClassUtils.NO_ARGS;
  }
}
