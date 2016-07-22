/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.runtime.core.transformer.wire;

import org.mule.runtime.core.api.transformer.wire.WireFormat;
import org.mule.runtime.core.transformer.simple.ByteArrayToMuleMessage;
import org.mule.runtime.core.transformer.simple.MuleMessageToByteArray;

import static org.junit.Assert.assertEquals;

public class SerializedMuleMessageWireFormatTestCase extends AbstractMuleMessageWireFormatTestCase {

  @Override
  protected WireFormat getWireFormat() throws Exception {
    return createObject(SerializedMuleMessageWireFormat.class);
  }

  @Override
  public void testGetDefaultInboundTransformer() throws Exception {
    SerializedMuleMessageWireFormat wireFormat = (SerializedMuleMessageWireFormat) getWireFormat();
    assertEquals(ByteArrayToMuleMessage.class, wireFormat.getInboundTransformer().getClass());
  }

  @Override
  public void testGetDefaultOutboundTransformer() throws Exception {
    SerializedMuleMessageWireFormat wireFormat = (SerializedMuleMessageWireFormat) getWireFormat();
    assertEquals(MuleMessageToByteArray.class, wireFormat.getOutboundTransformer().getClass());
  }

}
