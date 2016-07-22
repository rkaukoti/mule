/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the terms of
 * the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.compatibility.core.component;

import org.mule.compatibility.core.api.component.JavaWithBindingsComponent;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.MuleException;
import org.mule.runtime.core.api.construct.FlowConstruct;
import org.mule.runtime.core.api.lifecycle.InitialisationException;
import org.mule.runtime.core.api.model.EntryPointResolverSet;

/**
 * @deprecated Transport infrastructure is deprecated.
 */
@Deprecated
public class NullLifecycleAdapterWithBindings extends DefaultComponentLifecycleAdapterWithBindings {

  public NullLifecycleAdapterWithBindings(Object componentObject, JavaWithBindingsComponent component, FlowConstruct flowConstruct,
      EntryPointResolverSet entryPointResolver, MuleContext muleContext) throws MuleException {
    super(componentObject, component, flowConstruct, entryPointResolver, muleContext);
  }

  @Override
  public void start() throws MuleException {
    // no-op
  }

  @Override
  public void stop() throws MuleException {
    // no-op
  }

  @Override
  public void dispose() {
    // no-op
  }

  @Override
  public boolean isStarted() {
    return true;
  }

  @Override
  public boolean isDisposed() {
    return false;
  }

  @Override
  public void initialise() throws InitialisationException {
    // no-op
  }

}
