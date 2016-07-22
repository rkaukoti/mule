/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.runtime.config.spring.factories;

import org.mule.runtime.core.api.exception.MessagingExceptionHandler;
import org.mule.runtime.core.api.exception.MessagingExceptionHandlerAware;
import org.mule.runtime.core.api.processor.MessageProcessor;
import org.mule.runtime.core.api.processor.MessageProcessorBuilder;
import org.mule.runtime.core.api.transaction.TransactionFactory;
import org.mule.runtime.core.processor.DelegateTransactionFactory;
import org.mule.runtime.core.processor.TransactionalInterceptingMessageProcessor;
import org.mule.runtime.core.processor.chain.DefaultMessageProcessorChainBuilder;
import org.mule.runtime.core.transaction.MuleTransactionConfig;
import org.springframework.beans.factory.FactoryBean;

import java.util.List;

public class TransactionalMessageProcessorsFactoryBean implements FactoryBean {

  protected List messageProcessors;
  protected MessagingExceptionHandler exceptionListener;
  protected String action;

  public Class getObjectType() {
    return TransactionalInterceptingMessageProcessor.class;
  }

  public void setMessageProcessors(List messageProcessors) {
    this.messageProcessors = messageProcessors;
  }

  public Object getObject() throws Exception {
    DefaultMessageProcessorChainBuilder builder = new DefaultMessageProcessorChainBuilder();
    builder.setName("'transaction' child processor chain");
    TransactionalInterceptingMessageProcessor txProcessor = new TransactionalInterceptingMessageProcessor();
    txProcessor.setExceptionListener(this.exceptionListener);
    MuleTransactionConfig transactionConfig = createTransactionConfig(this.action);
    txProcessor.setTransactionConfig(transactionConfig);
    transactionConfig.setFactory(getTransactionFactory());
    builder.chain(txProcessor);
    for (Object processor : messageProcessors) {
      if (processor instanceof MessageProcessor) {
        builder.chain((MessageProcessor) processor);
      } else if (processor instanceof MessageProcessorBuilder) {
        builder.chain((MessageProcessorBuilder) processor);
      } else {
        throw new IllegalArgumentException(
            "MessageProcessorBuilder should only have MessageProcessor's or MessageProcessorBuilder's configured");
      }
      if (processor instanceof MessagingExceptionHandlerAware) {
        ((MessagingExceptionHandlerAware) processor).setMessagingExceptionHandler(exceptionListener);
      }
    }
    return builder.build();
  }

  protected TransactionFactory getTransactionFactory() {
    return new DelegateTransactionFactory();
  }

  protected MuleTransactionConfig createTransactionConfig(String action) {
    MuleTransactionConfig transactionConfig = new MuleTransactionConfig();
    transactionConfig.setActionAsString(action);
    return transactionConfig;
  }

  public boolean isSingleton() {
    return false;
  }

  public void setExceptionListener(MessagingExceptionHandler exceptionListener) {
    this.exceptionListener = exceptionListener;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
