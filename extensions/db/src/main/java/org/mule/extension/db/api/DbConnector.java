/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.db.api;

import org.mule.extension.db.api.config.GenericDbConfig;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connector.Providers;

@Extension(name = "DB Connector", description = "Connector for connecting to relation Databases through the JDBC API")
@Configurations({GenericDbConfig.class})
@Operations({StandardFileSystemOperations.class})
@Providers(LocalFileConnectionProvider.class)
public class DbConnector
{

}
