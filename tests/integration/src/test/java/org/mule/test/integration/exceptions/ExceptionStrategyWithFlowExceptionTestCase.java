/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.test.integration.exceptions;

import org.junit.Test;
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.runtime.core.api.MessagingException;
import org.mule.runtime.core.api.MuleEvent;
import org.mule.runtime.core.api.MuleException;
import org.mule.runtime.core.api.MuleMessage;
import org.mule.runtime.core.api.client.MuleClient;
import org.mule.runtime.core.api.processor.MessageProcessor;
import org.mule.runtime.core.message.ExceptionMessage;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ExceptionStrategyWithFlowExceptionTestCase extends FunctionalTestCase {

  @Override
  protected String getConfigFile() {
    return "org/mule/test/integration/exceptions/exception-strategy-with-flow-exception.xml";
  }

  @Test
  public void testFlowExceptionExceptionStrategy() throws Exception {
    flowRunner("customException").withPayload(getTestMuleMessage(TEST_MESSAGE)).asynchronously().run();
    MuleClient client = muleContext.getClient();
    MuleMessage message = client.request("test://out", RECEIVE_TIMEOUT);

    assertNotNull("request returned no message", message);
    assertTrue(message.getPayload() instanceof ExceptionMessage);
  }

  public static class ExceptionThrower implements MessageProcessor {

    @Override
    public MuleEvent process(MuleEvent event) throws MuleException {
      throw new MessagingException(event, null);
    }
  }
}
