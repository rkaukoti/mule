/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.runtime.core.processor;

import org.mule.runtime.core.api.MuleEvent;
import org.mule.runtime.core.api.MuleException;
import org.mule.runtime.core.api.processor.MessageProcessor;
import org.mule.runtime.core.api.processor.MessageProcessorChain;
import org.mule.runtime.core.util.ObjectUtils;

import java.util.Collections;
import java.util.List;


public class NullMessageProcessor implements MessageProcessorChain {

  public MuleEvent process(MuleEvent event) throws MuleException {
    return event;
  }

  @Override
  public String toString() {
    return ObjectUtils.toString(this);
  }

  public List<MessageProcessor> getMessageProcessors() {
    return Collections.emptyList();
  }

  public String getName() {
    return null;
  }

}
