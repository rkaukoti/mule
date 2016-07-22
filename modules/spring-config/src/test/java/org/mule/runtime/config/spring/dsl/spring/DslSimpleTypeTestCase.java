/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the terms of
 * the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.runtime.config.spring.dsl.spring;

import org.junit.Test;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mule.runtime.config.spring.dsl.spring.DslSimpleType.isSimpleType;

@SmallTest
public class DslSimpleTypeTestCase extends AbstractMuleTestCase {

  @Test
  public void simpleTypes() {
    assertThat(isSimpleType(byte.class), is(true));
    assertThat(isSimpleType(Byte.class), is(true));
    assertThat(isSimpleType(short.class), is(true));
    assertThat(isSimpleType(Short.class), is(true));
    assertThat(isSimpleType(int.class), is(true));
    assertThat(isSimpleType(Integer.class), is(true));
    assertThat(isSimpleType(long.class), is(true));
    assertThat(isSimpleType(Long.class), is(true));
    assertThat(isSimpleType(float.class), is(true));
    assertThat(isSimpleType(Float.class), is(true));
    assertThat(isSimpleType(double.class), is(true));
    assertThat(isSimpleType(Double.class), is(true));
    assertThat(isSimpleType(char.class), is(true));
    assertThat(isSimpleType(Character.class), is(true));
    assertThat(isSimpleType(String.class), is(true));
    assertThat(isSimpleType(Object.class), is(false));
    assertThat(isSimpleType(TestEnum.class), is(true));
    assertThat(isSimpleType(AtomicInteger.class), is(false));
  }

  private enum TestEnum {
    TEST
  }

}
