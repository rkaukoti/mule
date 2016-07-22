/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the terms of
 * the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.runtime.config.spring.dsl.api;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;

import static java.lang.String.format;
import static org.hamcrest.core.Is.is;
import static org.junit.rules.ExpectedException.none;
import static org.mule.runtime.config.spring.dsl.api.ComponentBuildingDefinition.KEY_TYPE_CONVERTER_AND_NO_MAP_TYPE;
import static org.mule.runtime.config.spring.dsl.api.ComponentBuildingDefinition.TYPE_CONVERTER_AND_NO_SIMPLE_TYPE_MESSAGE_TEMPLATE;
import static org.mule.runtime.config.spring.dsl.api.ComponentBuildingDefinition.TYPE_CONVERTER_AND_UNKNOWN_TYPE_MESSAGE;
import static org.mule.runtime.config.spring.dsl.api.TypeDefinition.fromConfigurationAttribute;
import static org.mule.runtime.config.spring.dsl.api.TypeDefinition.fromType;

@SmallTest
public class ComponentBuildingDefinitionTestCase extends AbstractMuleTestCase {

  @Rule
  public ExpectedException expectException = none();
  private ComponentBuildingDefinition.Builder baseDefinition =
      new ComponentBuildingDefinition.Builder().withIdentifier("test").withNamespace("namespace");

  @Test
  public void simpleTypeWithTypeConverter() {
    baseDefinition.copy().withTypeDefinition(fromType(Integer.class)).withTypeConverter(getFakeTypeConverter()).build();
  }

  @Test
  public void typeFromConfigAndTypeConverter() {
    expectException.expectMessage(is(TYPE_CONVERTER_AND_UNKNOWN_TYPE_MESSAGE));
    baseDefinition.copy().withTypeDefinition(fromConfigurationAttribute("class")).withTypeConverter(getFakeTypeConverter()).build();
  }

  @Test
  public void noSimpleTypeWithTypeConverter() {
    expectException.expectMessage(is(format(TYPE_CONVERTER_AND_NO_SIMPLE_TYPE_MESSAGE_TEMPLATE, Object.class.getName())));
    baseDefinition.copy().withTypeDefinition(fromType(Object.class)).withTypeConverter(getFakeTypeConverter()).build();
  }

  @Test
  public void keyTypeConverterAndNoMapType() {
    expectException.expectMessage(is(KEY_TYPE_CONVERTER_AND_NO_MAP_TYPE));
    baseDefinition.copy().withTypeDefinition(fromType(Object.class)).withKeyTypeConverter(getFakeTypeConverter()).build();
  }

  private TypeConverter getFakeTypeConverter() {
    return o -> null;
  }

}
