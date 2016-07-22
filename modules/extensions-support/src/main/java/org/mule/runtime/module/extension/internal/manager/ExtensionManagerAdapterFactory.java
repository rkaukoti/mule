/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.manager;

import org.mule.runtime.core.api.MuleContext;

/**
 * Factory to create instances of {@link ExtensionManagerAdapter}
 *
 * @since 4.0
 */
public interface ExtensionManagerAdapterFactory {

  /**
   * Creates a new {@link ExtensionManagerAdapter}
   *
   * @param muleContext the owning {@link MuleContext}
   * @return a non {@code null} {@link ExtensionManagerAdapter}
   */
  ExtensionManagerAdapter createExtensionManager(MuleContext muleContext);
}
