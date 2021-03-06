/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket.protocol;

import static org.junit.Assert.assertEquals;
import org.mule.extension.socket.api.connection.tcp.protocol.DirectProtocol;
import org.mule.extension.socket.api.socket.tcp.TcpProtocol;
import org.mule.runtime.core.util.IOUtils;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;

import org.junit.Test;

@SmallTest
public class DefaultProtocolTestCase extends AbstractMuleTestCase
{

    private TcpProtocol protocol;
    private int expectedLength;

    public DefaultProtocolTestCase()
    {
        this(new DirectProtocol(), SlowInputStream.FULL_LENGTH);
    }

    protected DefaultProtocolTestCase(TcpProtocol protocol, int expectedLength)
    {
        this.protocol = protocol;
        this.expectedLength = expectedLength;
    }

    @Test
    public void testRead() throws Exception
    {
        byte[] result = IOUtils.toByteArray(protocol.read(new SlowInputStream()));
        assertEquals(expectedLength, result.length);
    }

    protected TcpProtocol getProtocol()
    {
        return protocol;
    }

}
