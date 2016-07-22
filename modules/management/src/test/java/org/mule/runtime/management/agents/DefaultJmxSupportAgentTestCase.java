/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the terms of
 * the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.runtime.management.agents;

import org.junit.Test;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.module.management.agent.AbstractJmxAgent;
import org.mule.runtime.module.management.agent.DefaultJmxSupportAgent;
import org.mule.runtime.module.management.agent.FixedHostRmiClientSocketFactory;
import org.mule.tck.junit4.AbstractMuleContextTestCase;

import java.util.Map;

import javax.management.remote.rmi.RMIConnectorServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DefaultJmxSupportAgentTestCase extends AbstractMuleContextTestCase {
  public static void doTestHostPropertyEnablesClientSocketFactory(MuleContext muleContext) {
    DefaultJmxSupportAgent agent = new DefaultJmxSupportAgent();
    agent.setMuleContext(muleContext);
    agent.setHost("127.0.0.1");
    AbstractJmxAgent jmxAgent = agent.createJmxAgent();
    Map props = jmxAgent.getConnectorServerProperties();
    assertNotNull(props);
    assertEquals("JMX ConnectorServer properties should've been merged", 2, props.size());
    assertTrue("Property shouldn't have been removed", props.containsKey(RMIConnectorServer.JNDI_REBIND_ATTRIBUTE));
    assertTrue("Property should've been added", props.containsKey(RMIConnectorServer.RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE));
    Object ref = props.get(RMIConnectorServer.RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE);
    assertNotNull(ref);
    assertTrue(ref instanceof FixedHostRmiClientSocketFactory);
  }

  @Test
  public void testHostPropertyEnablesClientSocketFactory() throws Exception {
    doTestHostPropertyEnablesClientSocketFactory(muleContext);
  }
}
