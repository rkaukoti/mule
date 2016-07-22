/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the terms of
 * the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.extension.validation;

import org.junit.Test;
import org.mule.extension.validation.api.ValidationResult;
import org.mule.extension.validation.api.Validator;
import org.mule.runtime.core.api.MuleEvent;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mule.extension.validation.internal.ImmutableValidationResult.ok;

public class CustomValidatorInstanceReusedTestCase extends ValidationTestCase {

  private static Set<Validator> executedValidators;

  @Override
  protected void doSetUp() throws Exception {
    super.doSetUp();
    executedValidators = new HashSet<>();
  }

  @Override
  protected String getConfigFile() {
    return "custom-validator.xml";
  }

  @Test
  public void byClassInstanceReused() throws Exception {
    runFlow("byClassInstanceReused");
    runFlow("byClassInstanceReused");
    assertThat(executedValidators, hasSize(1));
  }

  public static class TestValidator implements Validator {

    @Override
    public ValidationResult validate(MuleEvent event) {
      executedValidators.add(this);
      return ok();
    }
  }

}
