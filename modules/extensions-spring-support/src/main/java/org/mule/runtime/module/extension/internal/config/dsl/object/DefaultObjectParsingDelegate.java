/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the terms of
 * the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.config.dsl.object;

import org.mule.metadata.api.model.ObjectType;
import org.mule.runtime.config.spring.dsl.api.AttributeDefinition;
import org.mule.runtime.extension.xml.dsl.api.DslElementDeclaration;
import org.mule.runtime.module.extension.internal.runtime.resolver.ValueResolver;

import static org.mule.runtime.config.spring.dsl.api.AttributeDefinition.Builder.fromChildConfiguration;

/**
 * Default {@link ObjectParsingDelegate} which accepts any {@link ObjectType} and parses it as a {@link ValueResolver}
 *
 * @since 4.0
 */
public class DefaultObjectParsingDelegate implements ObjectParsingDelegate {

  /**
   * @param objectType an {@link ObjectType}
   * @return {@code true}
   */
  @Override
  public boolean accepts(ObjectType objectType) {
    return true;
  }

  /**
   * Parses the given {@code objectType} as a {@link ValueResolver}
   *
   * @param name the element name
   * @param objectType a {@link ObjectType}
   * @param elementDsl the {@link DslElementDeclaration} of the parsed element
   * @return a {@link AttributeDefinition.Builder}
   */
  @Override
  public AttributeDefinition.Builder parse(String name, ObjectType objectType, DslElementDeclaration elementDsl) {
    AttributeDefinition.Builder builder = fromChildConfiguration(ValueResolver.class);
    if (elementDsl.isWrapped()) {
      builder.withWrapperIdentifier(elementDsl.getElementName());
    } else {
      builder.withIdentifier(elementDsl.getElementName());
    }

    return builder;
  }
}
