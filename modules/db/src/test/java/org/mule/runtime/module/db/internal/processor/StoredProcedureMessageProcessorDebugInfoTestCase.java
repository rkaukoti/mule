/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.mule.runtime.module.db.internal.processor;

import org.mule.runtime.module.db.internal.domain.query.QueryType;
import org.mule.tck.size.SmallTest;

import static org.mule.runtime.module.db.internal.domain.query.QueryType.STORE_PROCEDURE_CALL;

@SmallTest
public class StoredProcedureMessageProcessorDebugInfoTestCase
    extends AbstractParameterizedSingleQueryMessageProcessorDebugInfoTestCase {

  @Override
  protected AbstractSingleQueryDbMessageProcessor createMessageProcessor() {
    return new StoredProcedureMessageProcessor(dbConfigResolver, queryResolver, null, null, false);
  }

  @Override
  protected String getSqlText() {
    return "{ call getTestRecord(?, ?) }";
  }

  @Override
  protected QueryType getQueryType() {
    return STORE_PROCEDURE_CALL;
  }
}
