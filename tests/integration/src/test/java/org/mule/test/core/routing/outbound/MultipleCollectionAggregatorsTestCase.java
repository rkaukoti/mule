/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the terms of
 * the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.test.core.routing.outbound;

import org.junit.Test;
import org.mule.functional.junit4.FunctionalTestCase;

import static org.junit.Assert.assertTrue;

public class MultipleCollectionAggregatorsTestCase extends FunctionalTestCase {

  @Override
  protected String getConfigFile() {
    return "multiple-collection-aggregators-config-flow.xml";
  }

  @Test
  public void testStartsCorrectly() {
    assertTrue(muleContext.isStarted());
  }
}
