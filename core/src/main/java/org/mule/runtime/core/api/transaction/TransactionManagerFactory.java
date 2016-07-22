/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.runtime.core.api.transaction;

import org.mule.runtime.core.api.config.MuleConfiguration;

import javax.transaction.TransactionManager;

/**
 * <code>TranactionManagerFactory</code> is a factory class for creating a transaction manager for the MuleServer.
 */
public interface TransactionManagerFactory {

  /**
   * Creates of obtains the jta transaction manager to use for mule transactions
   *
   * @param config Mule configuration parameters
   * @return the transaction manager to use
   * @throws Exception if the transaction manager cannot be located or created
   */
  TransactionManager create(MuleConfiguration config) throws Exception;
}
