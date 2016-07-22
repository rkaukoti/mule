/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the terms of
 * the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.runtime.module.http.functional;

public class HttpListenerTlsRestrictedProtocolsAndCiphersTestCase extends AbstractServerTlsRestrictedProtocolsAndCiphersTestCase {
  @Override
  protected String getConfigFile() {
    return "http-listener-restricted-protocols-ciphers-config.xml";
  }
}
