/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the terms of
 * the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.runtime.management.agents;

import org.junit.Test;
import org.mule.functional.junit4.DomainFunctionalTestCase;

import static org.mule.runtime.management.agents.DefaultJmxSupportAgentTestCase.doTestHostPropertyEnablesClientSocketFactory;

public class DefaultJmxSupportAgentWithDomainTestCase extends DomainFunctionalTestCase {

  @Override
  protected String getDomainConfig() {
    return "agent/empty-domain-config.xml";
  }

  @Override
  public ApplicationConfig[] getConfigResources() {
    return new ApplicationConfig[] {new ApplicationConfig("app", new String[] {"agent/jmx-agent-app-config.xml"})};
  }

  @Test
  public void testHostPropertyEnablesClientSocketFactory() throws Exception {
    doTestHostPropertyEnablesClientSocketFactory(getMuleContextForApp("app"));
  }
}
