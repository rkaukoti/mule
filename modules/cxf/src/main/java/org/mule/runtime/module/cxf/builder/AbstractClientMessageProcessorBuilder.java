/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.runtime.module.cxf.builder;

import org.apache.cxf.endpoint.Client;
import org.mule.runtime.module.cxf.support.StreamClosingInterceptor;

/**
 * An abstract builder for non proxy clients.
 */
public abstract class AbstractClientMessageProcessorBuilder extends AbstractOutboundMessageProcessorBuilder {

  protected Class<?> serviceClass;

  @Override
  protected void configureClient(Client client) {
    // EE-1806/MULE-4404
    client.getInInterceptors().add(new StreamClosingInterceptor());
    client.getInFaultInterceptors().add(new StreamClosingInterceptor());
  }

  public Class<?> getServiceClass() {
    return serviceClass;
  }

  public void setServiceClass(Class<?> serviceClass) {
    this.serviceClass = serviceClass;
  }
}
