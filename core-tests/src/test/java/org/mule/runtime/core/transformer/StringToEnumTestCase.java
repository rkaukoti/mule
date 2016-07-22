/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.runtime.core.transformer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mule.runtime.core.api.transformer.TransformerException;
import org.mule.runtime.core.transformer.simple.StringToEnum;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@SmallTest
public class StringToEnumTestCase extends AbstractMuleTestCase {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  private StringToEnum transformer = new StringToEnum(TestEnum.class);

  @Test
  public void transform() throws Exception {
    for (TestEnum value : TestEnum.values()) {
      assertThat(transformer.transform(value.name()), is(value));
    }
  }

  @Test
  public void illegalValue() throws Exception {
    expectedException.expect(TransformerException.class);
    expectedException.expectCause(instanceOf(IllegalArgumentException.class));

    transformer.transform("NOT ENUM VALUE");
  }

  @Test
  public void nullClass() {
    expectedException.expect(IllegalArgumentException.class);
    new StringToEnum(null);
  }

  enum TestEnum {
    A, B
  }
}
