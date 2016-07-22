/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.runtime.module.db.internal.config.processor;

import org.mule.runtime.module.db.internal.domain.executor.StoredProcedureExecutorFactory;
import org.mule.runtime.module.db.internal.metadata.NullMetadataProvider;
import org.mule.runtime.module.db.internal.processor.StoredProcedureMessageProcessor;
import org.mule.runtime.module.db.internal.result.statement.EagerStatementResultHandler;
import org.mule.runtime.module.db.internal.result.statement.StreamingStatementResultHandler;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class StoredProcedureProcessorBeanDefinitionParser extends AbstractResultSetHandlerProcessorDefinitionParser
{

    @Override
    protected Class<?> getBeanClass(Element element)
    {
        return StoredProcedureMessageProcessor.class;
    }

    @Override
    protected void doParse(Element element, ParserContext context, BeanDefinitionBuilder builder)
    {
        parseAutoGeneratedKeys(element, builder);

        super.doParse(element, context, builder);

    }

    @Override
    protected Object createExecutorFactory(Element element)
    {
        BeanDefinitionBuilder executorFactoryBean = BeanDefinitionBuilder.genericBeanDefinition(StoredProcedureExecutorFactory.class);

        executorFactoryBean.addConstructorArgValue(parseStatementFactory(element));

        BeanDefinitionBuilder statementResultHandlerBean;
        if (streaming)
        {
            statementResultHandlerBean = BeanDefinitionBuilder.genericBeanDefinition(StreamingStatementResultHandler.class);
        }
        else
        {
            statementResultHandlerBean = BeanDefinitionBuilder.genericBeanDefinition(EagerStatementResultHandler.class);
        }
        statementResultHandlerBean.addConstructorArgReference(resultSetHandlerBeanName);

        executorFactoryBean.addConstructorArgValue(statementResultHandlerBean.getBeanDefinition());

        return executorFactoryBean.getBeanDefinition();
    }

    @Override
    protected Object getMetadataProvider()
    {
        return new NullMetadataProvider();
    }
}
