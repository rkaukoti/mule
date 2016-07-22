/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.mule.mvel;

import org.junit.Test;
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.runtime.core.api.MuleMessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MvelCompiledExpressionCachingTestCase extends FunctionalTestCase {

  @Override
  protected String getConfigFile() {
    return "mvel-compiled-expression-caching-config.xml";
  }

  @Test
  public void testIssue() throws Exception {
    MuleMessage response = flowRunner("foo-flow").withPayload(TEST_MESSAGE).run().getMessage();

    assertThat(getPayloadAsString(response), equalTo("SUCCESS"));
  }
}
