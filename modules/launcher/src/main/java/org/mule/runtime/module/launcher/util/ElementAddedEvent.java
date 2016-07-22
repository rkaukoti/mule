/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the terms of
 * the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.runtime.module.launcher.util;

/**
 *
 */
public class ElementAddedEvent extends ElementEvent {

  public ElementAddedEvent(Object source, Object newValue, int index) {
    super(source, null, newValue, index, ElementEvent.ADDED);
  }
}
