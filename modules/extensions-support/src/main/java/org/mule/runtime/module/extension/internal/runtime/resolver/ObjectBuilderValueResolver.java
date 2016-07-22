/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the terms of
 * the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.runtime.resolver;

import org.mule.runtime.core.api.MuleEvent;
import org.mule.runtime.core.api.MuleException;
import org.mule.runtime.core.api.lifecycle.Lifecycle;
import org.mule.runtime.module.extension.internal.runtime.ObjectBuilder;

import static org.mule.runtime.core.util.Preconditions.checkArgument;

/**
 * A {@link ValueResolver} which wraps an {@link ObjectBuilder} and calls {@link ObjectBuilder#build(MuleEvent)} on each
 * {@link #resolve(MuleEvent)}.
 * <p/>
 * It implements {@link Lifecycle} and propagates all lifecycle events to the underlying {@code builder}
 *
 * @param <T> the generic type for the instances built.
 * @since 3.7.0
 */
public class ObjectBuilderValueResolver<T> implements ValueResolver<T> {

  private final ObjectBuilder<T> builder;

  public ObjectBuilderValueResolver(ObjectBuilder<T> builder) {
    checkArgument(builder != null, "builder cannot be null");
    this.builder = builder;
  }

  /**
   * Delegates to {@code builder}
   *
   * @param event a {@link MuleEvent}
   * @return the {@code builder}'s output object
   */
  @Override
  public T resolve(MuleEvent event) throws MuleException {
    return builder.build(event);
  }

  /**
   * @return {@code true} if {@code builder} is dynamic
   */
  @Override
  public boolean isDynamic() {
    return builder.isDynamic();
  }
}
