/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.runtime.core.el.mvel.datatype;

import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.core.api.MuleEvent;

import static org.mule.runtime.core.el.mvel.MessageVariableResolverFactory.FLOW_VARS;

/**
 * Resolves data type for flow var when flowVars['x'] syntax is used
 */
public class FlowVarExpressionDataTypeResolver extends AbstractVariableExpressionDataTypeResolver
{

    public FlowVarExpressionDataTypeResolver()
    {
        super(FLOW_VARS);
    }

    @Override
    protected DataType getVariableDataType(MuleEvent event, String propertyName)
    {
        return event.getFlowVariableDataType(propertyName);
    }
}
