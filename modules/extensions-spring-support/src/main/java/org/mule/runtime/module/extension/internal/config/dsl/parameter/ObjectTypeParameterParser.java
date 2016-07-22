/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.config.dsl.parameter;

import org.mule.metadata.api.model.ArrayType;
import org.mule.metadata.api.model.DictionaryType;
import org.mule.metadata.api.model.MetadataType;
import org.mule.metadata.api.model.ObjectFieldType;
import org.mule.metadata.api.model.ObjectType;
import org.mule.metadata.api.visitor.MetadataTypeVisitor;
import org.mule.runtime.config.spring.dsl.api.ComponentBuildingDefinition.Builder;
import org.mule.runtime.core.api.config.ConfigurationException;
import org.mule.runtime.extension.api.introspection.parameter.ExpressionSupport;
import org.mule.runtime.extension.xml.dsl.api.DslElementDeclaration;
import org.mule.runtime.extension.xml.dsl.api.resolver.DslElementResolver;
import org.mule.runtime.module.extension.internal.config.dsl.ExtensionDefinitionParser;
import org.mule.runtime.module.extension.internal.config.dsl.ExtensionParsingContext;
import org.mule.runtime.module.extension.internal.runtime.resolver.ValueResolver;

import static org.mule.metadata.utils.MetadataTypeUtils.getDefaultValue;
import static org.mule.runtime.config.spring.dsl.api.AttributeDefinition.Builder.fromFixedValue;
import static org.mule.runtime.config.spring.dsl.api.TypeDefinition.fromType;
import static org.mule.runtime.extension.api.introspection.declaration.type.TypeUtils.acceptsReferences;
import static org.mule.runtime.extension.api.introspection.declaration.type.TypeUtils.getExpressionSupport;

/**
 * A {@link ExtensionDefinitionParser} for parsing extension objects that can be defined as named top level elements and be placed
 * in the mule registry.
 * <p>
 * These objects are parsed as {@link ValueResolver}s which are later resolved by a {@link TopLevelParameterObjectFactory}
 * instance
 *
 * @since 4.0
 */
public class ObjectTypeParameterParser extends ExtensionDefinitionParser {

  private final ObjectType type;
  private final ClassLoader classLoader;
  private final DslElementDeclaration typeDsl;
  private final String name;
  private final String namespace;

  public ObjectTypeParameterParser(Builder definition, ObjectType type, ClassLoader classLoader,
      DslElementResolver dslElementResolver, ExtensionParsingContext context) {
    super(definition, dslElementResolver, context);
    this.type = type;
    this.classLoader = classLoader;
    this.typeDsl = dslElementResolver.resolve(type);
    this.name = typeDsl.getElementName();
    this.namespace = typeDsl.getElementNamespace();
  }

  public ObjectTypeParameterParser(Builder definition, String name, String namespace, ObjectType type, ClassLoader classLoader,
      DslElementResolver dslElementResolver, ExtensionParsingContext context) {
    super(definition, dslElementResolver, context);
    this.type = type;
    this.classLoader = classLoader;
    this.typeDsl = dslElementResolver.resolve(type);
    this.name = name;
    this.namespace = namespace;
  }

  @Override
  protected void doParse(Builder definitionBuilder) throws ConfigurationException {
    definitionBuilder.withIdentifier(name).withNamespace(namespace).withTypeDefinition(fromType(ValueResolver.class))
        .withObjectFactoryType(TopLevelParameterObjectFactory.class)
        .withConstructorParameterDefinition(fromFixedValue(type).build())
        .withConstructorParameterDefinition(fromFixedValue(classLoader).build());

    for (ObjectFieldType objectField : type.getFields()) {
      final MetadataType fieldType = objectField.getValue();
      final String fieldName = objectField.getKey().getName().getLocalPart();
      final boolean acceptsReferences = acceptsReferences(objectField);
      final Object defaultValue = getDefaultValue(fieldType).orElse(null);
      final ExpressionSupport expressionSupport = getExpressionSupport(fieldType);
      final DslElementDeclaration childDsl = typeDsl.getChild(fieldName).orElse(dslElementResolver.resolve(fieldType));

      fieldType.accept(new MetadataTypeVisitor() {

        @Override
        protected void defaultVisit(MetadataType metadataType) {
          parseAttributeParameter(fieldName, fieldName, metadataType, defaultValue, expressionSupport, false);
        }

        @Override
        public void visitObject(ObjectType objectType) {
          if (!parsingContext.isRegistered(childDsl.getElementName(), childDsl.getElementNamespace())) {
            parsingContext.registerObjectType(name, namespace, type);
            parseObjectParameter(fieldName, fieldName, objectType, defaultValue, expressionSupport, false, acceptsReferences,
                childDsl);
          } else {
            parseObject(fieldName, fieldName, objectType, defaultValue, expressionSupport, false, acceptsReferences, childDsl);
          }
        }

        @Override
        public void visitArrayType(ArrayType arrayType) {
          parseCollectionParameter(fieldName, fieldName, arrayType, defaultValue, expressionSupport, false, childDsl);
        }

        @Override
        public void visitDictionary(DictionaryType dictionaryType) {
          parseMapParameters(fieldName, fieldName, dictionaryType, defaultValue, expressionSupport, false, childDsl);
        }
      });
    }
  }
}
