/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.test.config.spring;

import org.junit.Test;
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.runtime.core.api.component.JavaComponent;
import org.mule.runtime.core.api.object.ObjectFactory;
import org.mule.runtime.core.construct.Flow;

import static org.junit.Assert.assertTrue;

/**
 * Test to ensure that Mule always uses the real
 */
public class SpringAOPSpringBeanLookupTestCase extends FunctionalTestCase {

  @Override
  protected String getConfigFile() {
    return "org/mule/config/spring/spring-aop-springbeanlookup-config.xml";
  }

  @Override
  protected boolean isStartContext() {
    return false;
  }

  @Override
  protected boolean isDisposeContextPerClass() {
    return false;
  }

  @Test
  public void beanType() throws Exception {
    ObjectFactory prototype = getPrototypeSpringObjectFactory();
    ObjectFactory singleton = getSingletonSpringObjectFactory();

    assertProxy(prototype.getObjectClass());
    assertProxy(singleton.getObjectClass());
  }

  @Test
  public void beanTypeAfterInstantiation() throws Exception {
    ObjectFactory prototype = getPrototypeSpringObjectFactory();
    ObjectFactory singleton = getSingletonSpringObjectFactory();
    prototype.getInstance(muleContext);
    singleton.getInstance(muleContext);

    assertProxy(prototype.getObjectClass());
    assertProxy(singleton.getObjectClass());
  }

  @Test
  public void beanTypeContextStarted() throws Exception {
    muleContext.start();

    ObjectFactory prototype = getPrototypeSpringObjectFactory();
    ObjectFactory singleton = getSingletonSpringObjectFactory();

    assertProxy(prototype.getObjectClass());
    assertProxy(singleton.getObjectClass());
  }

  @Test
  public void beanTypeContextStartedAfterInstantiation() throws Exception {
    muleContext.start();

    ObjectFactory prototype = getPrototypeSpringObjectFactory();
    ObjectFactory singleton = getSingletonSpringObjectFactory();
    prototype.getInstance(muleContext);
    singleton.getInstance(muleContext);

    assertProxy(prototype.getObjectClass());
    assertProxy(singleton.getObjectClass());
  }

  private void assertProxy(Class<?> clazz) {
    assertTrue(clazz.getName().contains("$Proxy"));
  }

  private ObjectFactory getPrototypeSpringObjectFactory() throws Exception {
    return ((JavaComponent) ((Flow) getFlowConstruct("flow")).getMessageProcessors().get(0)).getObjectFactory();
  }

  private ObjectFactory getSingletonSpringObjectFactory() throws Exception {
    return ((JavaComponent) ((Flow) getFlowConstruct("flow")).getMessageProcessors().get(1)).getObjectFactory();
  }

}
