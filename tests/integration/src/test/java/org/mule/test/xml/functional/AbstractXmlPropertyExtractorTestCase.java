/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.test.xml.functional;

import org.junit.Test;
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.runtime.core.api.MessagingException;
import org.mule.runtime.core.api.MuleMessage;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public abstract class AbstractXmlPropertyExtractorTestCase extends FunctionalTestCase {

  @Override
  protected String getConfigFile() {
    return "org/mule/module/xml/property-extractor-test.xml";
  }

  protected abstract Object getMatchMessage() throws Exception;

  protected abstract Object getErrorMessage() throws Exception;

  @Test
  public void testMatch() throws Exception {
    MuleMessage message = flowRunner("test").withPayload(getMatchMessage()).run().getMessage();

    assertNotNull(message);
    assertThat(message.getPayload(), is("match"));
  }

  @Test
  public void testError() throws Exception {
    MessagingException e = flowRunner("test").withPayload(getErrorMessage()).runExpectingException();
    assertThat(e.getMessage(),
               is("Execution of the expression \"payload.childBean.value\" failed. (org.mule.runtime.core.api.expression.ExpressionRuntimeException)."));
  }
}
