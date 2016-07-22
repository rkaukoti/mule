/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.compatibility.transport.jms.vendors;

import org.junit.Test;
import org.mule.compatibility.transport.jms.DefaultJmsTopicResolver;
import org.mule.compatibility.transport.jms.JmsConnector;
import org.mule.compatibility.transport.jms.JmsTopicResolver;
import org.mule.functional.junit4.FunctionalTestCase;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class WebsphereEmbeddedJmsConnectorTestCase extends FunctionalTestCase {

  @Override
  protected String getConfigFile() {
    return "websphere-config.xml";
  }

  @Test
  public void testDefaultConfig() throws Exception {
    JmsConnector c = (JmsConnector) muleContext.getRegistry().lookupObject("jmsConnector");
    assertNotNull(c);

    // TODO has to be confirmed for Websphere
    assertTrue(c.isEagerConsumer());
    JmsTopicResolver resolver = c.getTopicResolver();
    assertNotNull("Topic resolver must not be null.", resolver);
    assertTrue("Wrong topic resolver configured on the connector.", resolver instanceof DefaultJmsTopicResolver);
  }
}
