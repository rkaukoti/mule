/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the terms of
 * the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.mule.runtime.module.db.integration.config;

import org.junit.runners.Parameterized;
import org.mule.runtime.module.db.integration.TestDbConfig;
import org.mule.runtime.module.db.integration.model.AbstractTestDatabase;
import org.mule.runtime.module.db.integration.model.MySqlTestDatabase;

import java.util.Collections;
import java.util.List;

public class MySqlHostPortConfigTestCase extends AbstractHostPortConfigTestCase {
  public MySqlHostPortConfigTestCase(String dataSourceConfigResource, AbstractTestDatabase testDatabase) {
    super(dataSourceConfigResource, testDatabase);
  }

  @Parameterized.Parameters
  public static List<Object[]> parameters() {
    if (TestDbConfig.getMySqlResource().isEmpty()) {
      return Collections.emptyList();
    } else {
      return Collections.singletonList(new Object[] {"integration/config/mysql-host-port-db-config.xml", new MySqlTestDatabase()});
    }
  }

  @Override
  protected String getDatabasePortPropertyValue() {
    return "3306";
  }
}
