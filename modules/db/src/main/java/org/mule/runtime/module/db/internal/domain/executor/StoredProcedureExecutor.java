/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the terms of
 * the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.mule.runtime.module.db.internal.domain.executor;

import org.mule.runtime.module.db.internal.domain.autogeneratedkey.AutoGeneratedKeyStrategy;
import org.mule.runtime.module.db.internal.domain.autogeneratedkey.NoAutoGeneratedKeyStrategy;
import org.mule.runtime.module.db.internal.domain.connection.DbConnection;
import org.mule.runtime.module.db.internal.domain.query.Query;
import org.mule.runtime.module.db.internal.domain.statement.StatementFactory;
import org.mule.runtime.module.db.internal.result.statement.StatementResultHandler;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Executes stored procedure queries
 */
public class StoredProcedureExecutor extends AbstractSingleQueryExecutor {

  private final StatementResultHandler statementResultHandler;

  public StoredProcedureExecutor(StatementFactory statementFactory, StatementResultHandler statementResultHandler) {
    super(statementFactory);
    this.statementResultHandler = statementResultHandler;
  }

  @Override
  protected Object doExecuteQuery(DbConnection connection, Statement statement, Query query) throws SQLException {
    return doExecuteQuery(connection, statement, query, new NoAutoGeneratedKeyStrategy());
  }

  @Override
  protected Object doExecuteQuery(DbConnection connection, Statement statement, Query query,
      AutoGeneratedKeyStrategy autoGeneratedKeyStrategy) throws SQLException {
    autoGeneratedKeyStrategy.execute(statement, query.getQueryTemplate());

    return statementResultHandler.processStatement(connection, statement, query.getQueryTemplate(), autoGeneratedKeyStrategy);
  }

}
