/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.mule.runtime.core.el.mvel.datatype;

import org.mule.mvel2.ast.ASTNode;
import org.mule.mvel2.ast.Assignment;
import org.mule.runtime.core.api.MuleEvent;
import org.mule.runtime.core.api.MuleMessage;
import org.mule.runtime.core.metadata.TypedValue;

import static org.mule.runtime.core.el.mvel.MessageVariableResolverFactory.MESSAGE_PAYLOAD;
import static org.mule.runtime.core.el.mvel.MessageVariableResolverFactory.PAYLOAD;

/**
 * Propagates data type when payload is used as enrichment target
 */
public class PayloadEnricherDataTypePropagator extends AbstractEnricherDataTypePropagator {

  @Override
  protected boolean doPropagate(MuleEvent event, TypedValue typedValue, ASTNode node) {
    if (node instanceof Assignment) {
      String assignmentVar = ((Assignment) node).getAssignmentVar();

      if (PAYLOAD.equals(assignmentVar) || MESSAGE_PAYLOAD.equals(assignmentVar)) {
        event.setMessage(MuleMessage.builder(event.getMessage()).payload(typedValue.getValue())
            .mediaType(typedValue.getDataType().getMediaType()).build());
        return true;
      }

    }
    return false;
  }

}
