/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the terms of
 * the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.runtime.config.spring.parsers.collection;

import org.mule.runtime.config.spring.MuleHierarchicalBeanDefinitionParserDelegate;
import org.mule.runtime.config.spring.parsers.generic.ChildDefinitionParser;
import org.springframework.beans.factory.config.MapFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates a single Map and processes standard Spring sub elements. The map is injected into the parent object (the enclosing XML element).
 */
public class ChildMapDefinitionParser extends ChildDefinitionParser {

  public ChildMapDefinitionParser(String setterMethod) {
    super(setterMethod, HashMap.class);
    addBeanFlag(MuleHierarchicalBeanDefinitionParserDelegate.MULE_NO_RECURSE);
  }

  protected Class getBeanClass(Element element) {
    return MapFactoryBean.class;
  }

  protected void parseChild(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
    super.parseChild(element, parserContext, builder);
    Map parsedMap = parserContext.getDelegate().parseMapElement(element, builder.getRawBeanDefinition());
    builder.addPropertyValue("sourceMap", parsedMap);
    builder.addPropertyValue("targetMapClass", super.getBeanClass(element));
  }

}
