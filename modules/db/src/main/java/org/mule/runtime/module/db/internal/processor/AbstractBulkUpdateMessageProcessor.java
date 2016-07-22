/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the terms of
 * the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.mule.runtime.module.db.internal.processor;

import org.mule.runtime.core.api.MuleEvent;
import org.mule.runtime.core.util.StringUtils;
import org.mule.runtime.module.db.internal.domain.autogeneratedkey.AutoGeneratedKeyStrategy;
import org.mule.runtime.module.db.internal.domain.autogeneratedkey.NoAutoGeneratedKeyStrategy;
import org.mule.runtime.module.db.internal.domain.executor.BulkQueryExecutorFactory;
import org.mule.runtime.module.db.internal.domain.query.QueryType;
import org.mule.runtime.module.db.internal.domain.transaction.TransactionalAction;
import org.mule.runtime.module.db.internal.resolver.database.DbConfigResolver;
import org.mule.runtime.module.db.internal.resolver.query.QueryResolver;

import java.util.Iterator;
import java.util.List;

/**
 * Defines a base class for single query bulk update operations
 */
public abstract class AbstractBulkUpdateMessageProcessor extends AbstractDbMessageProcessor {

  protected final QueryResolver queryResolver;
  protected final BulkQueryExecutorFactory bulkUpdateExecutorFactory;
  protected final List<QueryType> validQueryTypes;
  protected AutoGeneratedKeyStrategy autoGeneratedKeyStrategy;
  protected String source;

  public AbstractBulkUpdateMessageProcessor(DbConfigResolver dbConfigResolver, TransactionalAction transactionalAction,
      List<QueryType> validQueryTypes, QueryResolver queryResolver, BulkQueryExecutorFactory bulkUpdateExecutorFactory) {
    super(dbConfigResolver, transactionalAction);
    this.validQueryTypes = validQueryTypes;
    this.queryResolver = queryResolver;
    this.autoGeneratedKeyStrategy = new NoAutoGeneratedKeyStrategy();
    this.bulkUpdateExecutorFactory = bulkUpdateExecutorFactory;
  }

  protected Iterator<Object> getIterator(MuleEvent muleEvent) {
    Object bulkData;

    if (StringUtils.isEmpty(source)) {
      bulkData = muleEvent.getMessage().getPayload();
    } else {
      bulkData = muleContext.getExpressionManager().evaluate(source, muleEvent);
    }

    return getIterator(bulkData);
  }

  private Iterator<Object> getIterator(Object bulkData) {
    if (bulkData instanceof Iterable) {
      return ((Iterable<Object>) bulkData).iterator();
    } else if (bulkData instanceof Iterator) {
      return (Iterator<Object>) bulkData;
    } else {
      throw new IllegalArgumentException(String.format("Bulk mode operations require Iterable/Iterator as input. Got %s instead",
          bulkData != null ? bulkData.getClass().getCanonicalName() : "null"));
    }
  }

  @Override
  protected List<QueryType> getValidQueryTypes() {
    return validQueryTypes;
  }

  @Override
  public String getSource() {
    return source;
  }

  @Override
  public void setSource(String source) {
    this.source = source;
  }

  public void setAutoGeneratedKeyStrategy(AutoGeneratedKeyStrategy autoGeneratedKeyStrategy) {
    this.autoGeneratedKeyStrategy = autoGeneratedKeyStrategy;
  }
}
