/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.runtime.module.http.functional.listener;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.StringEntity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mule.runtime.module.http.functional.AbstractHttpTestCase;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.tck.junit4.rule.SystemProperty;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class HttpListenerPathRoutingTestCase extends AbstractHttpTestCase {

  private final String testPath;
  @Rule
  public DynamicPort listenPort = new DynamicPort("port");
  @Rule
  public SystemProperty path = new SystemProperty("path", "path");
  @Rule
  public SystemProperty path2 = new SystemProperty("path2", "path2");
  @Rule
  public SystemProperty anotherPath = new SystemProperty("anotherPath", "anotherPath");
  @Rule
  public SystemProperty pathSubPath = new SystemProperty("path/subpath", "path/subpath");

  public HttpListenerPathRoutingTestCase(String path) {
    this.testPath = path;
  }

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {{"path"}, {"path2"}, {"anotherPath"}, {"path/subpath"}});
  }

  @Override
  protected String getConfigFile() {
    return "http-listener-path-routing-config.xml";
  }

  @Test
  public void callPath() throws Exception {
    final String url = String.format("http://localhost:%s/%s", listenPort.getNumber(), testPath);
    final Response response = Request.Post(url).body(new StringEntity(testPath)).connectTimeout(1000).execute();
    assertThat(response.returnContent().asString(), is(testPath));
  }

}
