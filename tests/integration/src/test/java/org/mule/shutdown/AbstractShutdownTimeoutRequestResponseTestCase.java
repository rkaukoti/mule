/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.shutdown;

import org.junit.Before;
import org.junit.Rule;
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.runtime.core.api.DefaultMuleException;
import org.mule.runtime.core.api.MuleEvent;
import org.mule.runtime.core.api.MuleException;
import org.mule.runtime.core.api.processor.MessageProcessor;
import org.mule.runtime.core.util.concurrent.Latch;
import org.mule.tck.junit4.rule.DynamicPort;

public abstract class AbstractShutdownTimeoutRequestResponseTestCase extends FunctionalTestCase
{

    protected static int WAIT_TIME = 2000;
    protected static Latch waitLatch;

    @Rule
    public DynamicPort httpPort = new DynamicPort("httpPort");

    @Before
    public void setUpWaitLatch() throws Exception
    {
        waitLatch = new Latch();
    }

    private static class BlockMessageProcessor implements MessageProcessor
    {

        @Override
        public MuleEvent process(MuleEvent event) throws MuleException
        {
            waitLatch.release();

            try
            {
                Thread.sleep(WAIT_TIME);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                throw new DefaultMuleException(e);
            }

            return event;
        }
    }
}
