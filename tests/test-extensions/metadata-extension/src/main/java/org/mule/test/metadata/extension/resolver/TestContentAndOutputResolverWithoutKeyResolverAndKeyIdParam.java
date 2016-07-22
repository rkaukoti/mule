/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the terms of
 * the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.test.metadata.extension.resolver;

import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.metadata.MetadataContext;
import org.mule.runtime.api.metadata.MetadataResolvingException;
import org.mule.runtime.api.metadata.resolving.MetadataContentResolver;
import org.mule.runtime.api.metadata.resolving.MetadataOutputResolver;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.mule.runtime.core.util.Preconditions.checkArgument;
import static org.mule.test.metadata.extension.MetadataConnection.PERSON;
import static org.mule.test.metadata.extension.resolver.TestMetadataResolverUtils.getMetadata;

public class TestContentAndOutputResolverWithoutKeyResolverAndKeyIdParam
    implements MetadataContentResolver<String>, MetadataOutputResolver<String> {

  private static final String KEY_SHOULD_BE_EMPTY = "Metadata resolvers without Key Resolver should get a NullMetadataKey as Key";

  @Override
  public MetadataType getContentMetadata(MetadataContext context, String key) throws MetadataResolvingException {
    checkArgument(isBlank(key), KEY_SHOULD_BE_EMPTY);
    return getMetadata(PERSON);
  }

  @Override
  public MetadataType getOutputMetadata(MetadataContext context, String key) throws MetadataResolvingException {
    checkArgument(isBlank(key), KEY_SHOULD_BE_EMPTY);
    return getMetadata(PERSON);
  }
}
