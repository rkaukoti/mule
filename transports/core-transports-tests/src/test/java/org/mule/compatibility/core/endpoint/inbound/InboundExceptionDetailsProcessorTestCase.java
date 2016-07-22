/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.compatibility.core.endpoint.inbound;

import org.junit.Test;
import org.mule.compatibility.core.api.endpoint.InboundEndpoint;
import org.mule.compatibility.core.processor.AbstractMessageProcessorTestCase;
import org.mule.runtime.core.api.MuleEvent;
import org.mule.runtime.core.api.MuleMessage;
import org.mule.runtime.core.message.DefaultExceptionPayload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class InboundExceptionDetailsProcessorTestCase extends AbstractMessageProcessorTestCase {

  @Test
  public void testProcess() throws Exception {
    InboundEndpoint endpoint = createTestInboundEndpoint(null, null);
    InboundExceptionDetailsMessageProcessor mp = new InboundExceptionDetailsMessageProcessor(endpoint.getConnector());
    mp.setMuleContext(muleContext);
    MuleEvent event = createTestInboundEvent(endpoint);

    event.setMessage(MuleMessage.builder(event.getMessage()).exceptionPayload(new DefaultExceptionPayload(new RuntimeException()))
        .build());

    MuleEvent result = mp.process(event);

    assertNotNull(result);
    final int status = result.getMessage().getOutboundProperty("status", 0);
    assertEquals(500, status);
  }

}
