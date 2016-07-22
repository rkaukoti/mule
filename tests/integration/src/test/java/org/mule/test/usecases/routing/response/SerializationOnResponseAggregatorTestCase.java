/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.test.usecases.routing.response;

import org.junit.Rule;
import org.junit.Test;
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.MuleMessage;
import org.mule.runtime.core.api.client.MuleClient;
import org.mule.runtime.core.api.config.MuleProperties;
import org.mule.runtime.core.api.serialization.ObjectSerializer;
import org.mule.runtime.core.api.store.ObjectStoreException;
import org.mule.runtime.core.util.store.SimpleMemoryObjectStore;
import org.mule.tck.junit4.rule.DynamicPort;

import java.io.Serializable;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mule.runtime.module.http.api.HttpConstants.Methods.POST;
import static org.mule.runtime.module.http.api.client.HttpRequestOptionsBuilder.newOptions;

public class SerializationOnResponseAggregatorTestCase extends FunctionalTestCase {

  @Rule
  public DynamicPort dynamicPort = new DynamicPort("port1");

  @Override
  protected String getConfigFile() {
    return "org/mule/test/usecases/routing/response/serialization-on-response-router-config.xml";
  }

  @Test
  public void testSyncResponse() throws Exception {
    muleContext.getRegistry().registerObject(MuleProperties.OBJECT_STORE_DEFAULT_IN_MEMORY_NAME,
        new TestObjectStore(muleContext));
    MuleClient client = muleContext.getClient();
    MuleMessage message = client.send("http://localhost:" + dynamicPort.getNumber(), getTestMuleMessage("request"),
        newOptions().method(POST.name()).build());
    assertNotNull(message);
    assertThat(new String(getPayloadAsBytes(message)), is("request processed"));
  }

  private static class TestObjectStore extends SimpleMemoryObjectStore<Serializable> {

    private ObjectSerializer serializer;

    private TestObjectStore(MuleContext muleContext) {
      serializer = muleContext.getObjectSerializer();
    }

    @Override
    protected void doStore(Serializable key, Serializable value) throws ObjectStoreException {
      byte[] serialized = serializer.serialize(value);
      super.doStore(key, serialized);
    }

    @Override
    protected Serializable doRetrieve(Serializable key) {
      Serializable serialized = super.doRetrieve(key);
      return serializer.deserialize((byte[]) serialized);
    }
  }
}
