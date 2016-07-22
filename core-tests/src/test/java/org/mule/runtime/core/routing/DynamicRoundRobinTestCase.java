/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the terms of
 * the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.runtime.core.routing;


import org.junit.Test;
import org.mule.runtime.core.api.MuleEvent;

import static org.junit.Assert.assertEquals;

public class DynamicRoundRobinTestCase extends AbstractDynamicRoundRobinTestCase {

  @Test
  public void testDynamicRoundRobinWithDefaultIdentifier() throws Exception {
    DynamicRoundRobin dynamicRoundRobin = getDynamicRoundRobin(getDynamicRouteResolver());
    MuleEvent eventToProcessId1 = getEventWithId(ID_1);
    MuleEvent eventToProcessId2 = getEventWithId(ID_2);
    assertEquals(LETTER_A, getPayloadAsString(dynamicRoundRobin.process(eventToProcessId1).getMessage()));
    assertEquals(LETTER_B, getPayloadAsString(dynamicRoundRobin.process(eventToProcessId2).getMessage()));
    assertEquals(LETTER_C, getPayloadAsString(dynamicRoundRobin.process(eventToProcessId1).getMessage()));
    assertEquals(LETTER_A, getPayloadAsString(dynamicRoundRobin.process(eventToProcessId2).getMessage()));
  }

  @Test
  public void testDynamicRoundRobinWithIdentifier() throws Exception {
    DynamicRoundRobin dynamicRoundRobin = getDynamicRoundRobin(getIdentifiableDynamicRouteResolver());
    MuleEvent eventToProcessId1 = getEventWithId(ID_1);
    MuleEvent eventToProcessId2 = getEventWithId(ID_2);
    assertEquals(LETTER_A, getPayloadAsString(dynamicRoundRobin.process(eventToProcessId1).getMessage()));
    assertEquals(LETTER_A, getPayloadAsString(dynamicRoundRobin.process(eventToProcessId2).getMessage()));
    assertEquals(LETTER_B, getPayloadAsString(dynamicRoundRobin.process(eventToProcessId1).getMessage()));
    assertEquals(LETTER_B, getPayloadAsString(dynamicRoundRobin.process(eventToProcessId2).getMessage()));
  }

  private DynamicRoundRobin getDynamicRoundRobin(DynamicRouteResolver dynamicRouteResolver) throws Exception {
    DynamicRoundRobin dynamicRoundRobin = new DynamicRoundRobin();
    dynamicRoundRobin.setMuleContext(muleContext);
    dynamicRoundRobin.setDynamicRouteResolver(dynamicRouteResolver);
    dynamicRoundRobin.initialise();
    return dynamicRoundRobin;
  }

}
