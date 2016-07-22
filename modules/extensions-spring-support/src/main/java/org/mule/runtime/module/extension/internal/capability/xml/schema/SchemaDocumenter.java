/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.capability.xml.schema;

import org.mule.runtime.core.api.MuleRuntimeException;
import org.mule.runtime.core.config.i18n.MessageFactory;
import org.mule.runtime.core.util.CollectionUtils;
import org.mule.runtime.extension.api.annotation.Configuration;
import org.mule.runtime.extension.api.annotation.Parameter;
import org.mule.runtime.extension.api.annotation.ParameterGroup;
import org.mule.runtime.extension.api.introspection.ExtensionModel;
import org.mule.runtime.extension.api.introspection.declaration.fluent.ConfigurationDeclaration;
import org.mule.runtime.extension.api.introspection.declaration.fluent.ExtensionDeclaration;
import org.mule.runtime.extension.api.introspection.declaration.fluent.OperationDeclaration;
import org.mule.runtime.extension.api.introspection.declaration.fluent.ParameterDeclaration;
import org.mule.runtime.module.extension.internal.util.IntrospectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import static org.mule.runtime.module.extension.internal.capability.xml.schema.AnnotationProcessorUtils.getFieldsAnnotatedWith;
import static org.mule.runtime.module.extension.internal.capability.xml.schema.AnnotationProcessorUtils.getJavaDocSummary;
import static org.mule.runtime.module.extension.internal.capability.xml.schema.AnnotationProcessorUtils.getMethodDocumentation;
import static org.mule.runtime.module.extension.internal.capability.xml.schema.AnnotationProcessorUtils.getOperationMethods;
import static org.mule.runtime.module.extension.internal.capability.xml.schema.AnnotationProcessorUtils.getTypeElementsAnnotatedWith;

/**
 * Utility class that picks a {@link ExtensionDeclaration} on which a {@link ExtensionModel} has already been described and
 * enriches such description with the javadocs extracted from the extension's acting classes.
 * <p>
 * This is necessary because such documentation is not available on runtime, thus this class uses the annotation processor's AST
 * access to extract it
 *
 * @since 3.7.0
 */
final class SchemaDocumenter {

  private ProcessingEnvironment processingEnv;

  SchemaDocumenter(ProcessingEnvironment processingEnv) {
    this.processingEnv = processingEnv;
  }

  /**
   * Sets the description of the given {@code declaration} and its inner configs and operations by extracting information of the
   * AST tree represented by {@code extensionElement} and {@code roundEnvironment}
   *
   * @param extensionDeclaration a {@link ExtensionDeclaration} on which configurations and operations have already been declared
   * @param extensionElement a {@link TypeElement} generated by an annotation {@link Processor}
   * @param roundEnvironment a {@link RoundEnvironment} generated by an annotation {@link Processor}
   */
  void document(ExtensionDeclaration extensionDeclaration, TypeElement extensionElement, RoundEnvironment roundEnvironment) {
    extensionDeclaration.setDescription(getJavaDocSummary(processingEnv, extensionElement));
    documentConfigurations(extensionDeclaration, extensionElement, roundEnvironment);
    documentOperations(roundEnvironment, processingEnv, extensionDeclaration);
  }


  private void documentOperations(RoundEnvironment roundEnv, ProcessingEnvironment processingEnv,
      ExtensionDeclaration extensionDeclaration) {
    final Map<String, Element> methods = getOperationMethods(roundEnv, processingEnv);

    try {
      for (OperationDeclaration operation : extensionDeclaration.getOperations()) {
        Element method = methods.get(operation.getName());

        // there are two cases in which method can be null:
        // * A synthetic operation which was not defined in any class but added by a model property
        // * An extension which operations are defined across multiple classes and the one being processed is not
        // the one which defined the operation being processed
        if (method == null) {
          continue;
        }

        MethodDocumentation documentation = getMethodDocumentation(processingEnv, method);
        operation.setDescription(documentation.getSummary());
        documentOperationParameters(operation, documentation);
      }
    } catch (Exception e) {
      throw new MuleRuntimeException(MessageFactory.createStaticMessage("Exception found while trying to document XSD schema"),
          e);
    }
  }

  private void documentOperationParameters(OperationDeclaration operation, MethodDocumentation documentation) {
    for (ParameterDeclaration parameter : operation.getParameters()) {
      String description = documentation.getParameters().get(parameter.getName());
      if (description != null) {
        parameter.setDescription(description);
      }
    }
  }

  private void documentConfigurations(ExtensionDeclaration extensionDeclaration, TypeElement extensionElement,
      RoundEnvironment roundEnvironment) {
    if (extensionDeclaration.getConfigurations().size() > 1) {
      for (TypeElement configurationElement : getTypeElementsAnnotatedWith(Configuration.class, roundEnvironment)) {
        ConfigurationDeclaration configurationDeclaration = findMatchingConfiguration(extensionDeclaration, configurationElement);
        documentConfigurationParameters(configurationDeclaration.getParameters(), configurationElement);
      }
    } else {
      documentConfigurationParameters(extensionDeclaration.getConfigurations().get(0).getParameters(), extensionElement);
    }
  }

  private void documentConfigurationParameters(Collection<ParameterDeclaration> parameters, final TypeElement element) {
    final Map<String, VariableElement> variableElements = getFieldsAnnotatedWith(element, Parameter.class);
    TypeElement traversingElement = element;
    while (traversingElement != null && !Object.class.getName().equals(traversingElement.getQualifiedName().toString())) {
      Class<?> declaringClass = AnnotationProcessorUtils.classFor(traversingElement, processingEnv);
      for (ParameterDeclaration parameter : parameters) {
        Field field = IntrospectionUtils.getField(declaringClass, parameter);
        if (field != null && variableElements.containsKey(field.getName())) {
          parameter.setDescription(getJavaDocSummary(processingEnv, variableElements.get(field.getName())));
        }
      }

      traversingElement = (TypeElement) processingEnv.getTypeUtils().asElement(traversingElement.getSuperclass());
    }

    for (VariableElement variableElement : getFieldsAnnotatedWith(element, ParameterGroup.class).values()) {
      TypeElement typeElement = (TypeElement) processingEnv.getTypeUtils().asElement(variableElement.asType());
      documentConfigurationParameters(parameters, typeElement);
    }
  }

  private ConfigurationDeclaration findMatchingConfiguration(ExtensionDeclaration extensionDeclaration,
      final TypeElement configurationElement) {
    return (ConfigurationDeclaration) CollectionUtils.find(extensionDeclaration.getConfigurations(), object -> {
      Configuration configuration = configurationElement.getAnnotation(Configuration.class);
      ConfigurationDeclaration configurationDeclaration = (ConfigurationDeclaration) object;
      return configurationDeclaration.getName().equals(configuration.name());
    });
  }
}
