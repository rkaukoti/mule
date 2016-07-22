/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the terms of
 * the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.mule.runtime.module.db.internal.metadata;

import org.mule.common.DefaultResult;
import org.mule.common.Result;
import org.mule.common.metadata.DefaultListMetaDataModel;
import org.mule.common.metadata.DefaultMetaData;
import org.mule.common.metadata.DefaultParameterizedMapMetaDataModel;
import org.mule.common.metadata.DefaultPojoMetaDataModel;
import org.mule.common.metadata.DefaultSimpleMetaDataModel;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataModel;
import org.mule.common.metadata.datatype.DataType;
import org.mule.runtime.module.db.internal.domain.autogeneratedkey.AutoGeneratedKeyStrategy;
import org.mule.runtime.module.db.internal.domain.query.Query;
import org.mule.runtime.module.db.internal.resolver.database.DbConfigResolver;

import java.sql.PreparedStatement;

/**
 * Provides metadata for update/insert/delete queries
 */
public class UpdateMetadataProvider extends AbstractQueryMetadataProvider {

  private final AutoGeneratedKeyStrategy autoGeneratedKeyStrategy;

  public UpdateMetadataProvider(DbConfigResolver dbConfigResolver, Query query, AutoGeneratedKeyStrategy autoGeneratedKeyStrategy) {
    super(dbConfigResolver, query);
    this.autoGeneratedKeyStrategy = autoGeneratedKeyStrategy;
  }

  @Override
  public Result<MetaData> getStaticOutputMetadata() {
    DefaultMetaData defaultMetaData;
    if (autoGeneratedKeyStrategy != null && autoGeneratedKeyStrategy.returnsAutoGeneratedKeys()) {
      MetaDataModel recordModel = new DefaultParameterizedMapMetaDataModel(new DefaultSimpleMetaDataModel(DataType.STRING),
          new DefaultPojoMetaDataModel(Object.class));
      DefaultListMetaDataModel listModel = new DefaultListMetaDataModel(recordModel);
      defaultMetaData = new DefaultMetaData(listModel);
    } else {
      DefaultSimpleMetaDataModel updateCountModel = new DefaultSimpleMetaDataModel(DataType.DOUBLE);
      defaultMetaData = new DefaultMetaData(updateCountModel);
    }

    return new DefaultResult<MetaData>(defaultMetaData);
  }

  @Override
  public Result<MetaData> getDynamicOutputMetadata(PreparedStatement statement) {

    return getStaticOutputMetadata();
  }
}
