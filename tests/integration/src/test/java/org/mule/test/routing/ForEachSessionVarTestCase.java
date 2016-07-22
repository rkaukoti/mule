/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.test.routing;

import org.junit.Test;
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.runtime.core.api.MuleEvent;
import org.mule.runtime.core.api.MuleException;
import org.mule.runtime.core.api.processor.MessageProcessor;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class ForEachSessionVarTestCase extends FunctionalTestCase {

  private static final String MY_SESSION_LIST = "mySessionList";
  protected static MuleEvent event;

  @Override
  protected String getConfigFile() {
    return "foreach-session-var-config.xml";
  }

  @Test
  public void testSessionVars() throws Exception {
    Collection<String> expectedArray = new ArrayList<>();
    expectedArray.add("Hello World A");
    expectedArray.add("Hello World B");

    flowRunner("test-foreachFlow1").withPayload(getTestMuleMessage()).run();

    // propierty should exist in the session and the message
    assertThat(event.getSession().<Collection<String>>getProperty(MY_SESSION_LIST), is(expectedArray));
    // removing the property from the session should affect the message as well
    event.getSession().removeProperty(MY_SESSION_LIST);
    assertThat(event.getSession().<Collection<String>>getProperty(MY_SESSION_LIST), is(nullValue()));
  }

  public static class EventSaverProcessor implements MessageProcessor {

    @Override
    public MuleEvent process(MuleEvent receivedEvent) throws MuleException {
      event = receivedEvent;
      return receivedEvent;
    }
  }

}
