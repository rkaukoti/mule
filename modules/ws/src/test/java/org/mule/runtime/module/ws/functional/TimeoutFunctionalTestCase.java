/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.ws.functional;

import org.junit.Rule;
import org.junit.Test;
import org.mule.functional.functional.EventCallback;
import org.mule.functional.junit4.FlowRunner;
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.runtime.core.api.MuleEventContext;
import org.mule.runtime.core.api.MuleMessage;
import org.mule.runtime.core.util.concurrent.Latch;
import org.mule.tck.junit4.rule.DynamicPort;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class TimeoutFunctionalTestCase extends FunctionalTestCase
{

    @Rule
    public DynamicPort dynamicPort = new DynamicPort("port");

    @Override
    protected String getConfigFile()
    {
        return "timeout-config.xml";
    }

    @Test
    public void flowAndSessionVarsAreNotRemovedAfterTimeout() throws Exception
    {
        final Latch serverLatch = new Latch();

        getFunctionalTestComponent("server").setEventCallback(new EventCallback()
        {
            @Override
            public void eventReceived(MuleEventContext context, Object component) throws Exception
            {
                serverLatch.await();
            }
        });

        FlowRunner runner = flowRunner("client").withPayload("<echo/>");
        runner.buildEvent().setTimeout(1);
        runner.run();
        serverLatch.release();

        MuleMessage message = muleContext.getClient().request("test://out", RECEIVE_TIMEOUT);

        assertThat(message.<String>getOutboundProperty("flowVar"), equalTo("testFlowVar"));
        assertThat(message.<String>getOutboundProperty("sessionVar"), equalTo("testSessionVar"));
    }
}
