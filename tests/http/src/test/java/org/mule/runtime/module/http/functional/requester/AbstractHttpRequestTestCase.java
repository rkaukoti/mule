/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.runtime.module.http.functional.requester;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

import org.apache.commons.collections.EnumerationUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.mule.runtime.core.util.CaseInsensitiveMapWrapper;
import org.mule.runtime.core.util.FileUtils;
import org.mule.runtime.core.util.IOUtils;
import org.mule.runtime.module.http.functional.AbstractHttpTestCase;
import org.mule.tck.junit4.rule.DynamicPort;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AbstractHttpRequestTestCase extends AbstractHttpTestCase {

  public static final String DEFAULT_RESPONSE = "<h1>Response</h1>";
  @Rule
  public DynamicPort httpPort = new DynamicPort("httpPort");
  @Rule
  public DynamicPort httpsPort = new DynamicPort("httpsPort");
  protected Server server;

  protected String method;
  protected String uri;
  protected Multimap<String, String> headers =
      Multimaps.newMultimap(new CaseInsensitiveMapWrapper<>(HashMap.class), Sets::newHashSet);

  protected String body;

  @Before
  public void startServer() throws Exception {
    server = createServer();
    server.setHandler(createHandler(server));
    server.start();
  }

  @After
  public void stopServer() throws Exception {
    server.stop();
  }

  protected Server createServer() {
    Server server = new Server(httpPort.getNumber());
    if (enableHttps()) {
      enableHttpsServer(server);
    }
    return server;
  }

  protected boolean enableHttps() {
    return false;
  }

  private void enableHttpsServer(Server server) {
    SslContextFactory sslContextFactory = new SslContextFactory();

    try {
      sslContextFactory.setKeyStorePath(FileUtils.getResourcePath("tls/serverKeystore", getClass()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    sslContextFactory.setKeyStorePassword("mulepassword");
    sslContextFactory.setKeyManagerPassword("mulepassword");

    ServerConnector connector = new ServerConnector(server, sslContextFactory);
    connector.setPort(httpsPort.getNumber());
    server.addConnector(connector);
  }

  protected AbstractHandler createHandler(Server server) {
    return new TestHandler();
  }

  protected void handleRequest(Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
    extractBaseRequestParts(baseRequest);
    writeResponse(response);
  }

  protected void extractBaseRequestParts(Request baseRequest) throws IOException {
    method = baseRequest.getMethod();
    uri = baseRequest.getUri().getCompletePath();

    extractHeadersFromBaseRequest(baseRequest);

    body = IOUtils.toString(baseRequest.getInputStream());
  }

  protected void extractHeadersFromBaseRequest(Request baseRequest) {
    for (String headerName : (List<String>) EnumerationUtils.toList(baseRequest.getHeaderNames())) {
      Enumeration<String> headerValues = baseRequest.getHeaders(headerName);

      while (headerValues.hasMoreElements()) {
        headers.put(headerName, headerValues.nextElement());
      }
    }
  }

  protected void writeResponse(HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);
    response.getWriter().print(DEFAULT_RESPONSE);
  }

  public String getFirstReceivedHeader(String headerName) {
    return headers.get(headerName).iterator().next();
  }

  private class TestHandler extends AbstractHandler {

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

      handleRequest(baseRequest, request, response);

      baseRequest.setHandled(true);
    }
  }
}
