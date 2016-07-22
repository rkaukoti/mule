/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.api.schedule;


import org.junit.Test;
import org.mule.runtime.core.util.Predicate;
import org.mule.tck.junit4.AbstractMuleTestCase;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class SchedulersTest extends AbstractMuleTestCase
{

    @Test
    public void validatePollSchedulersPredicates()
    {
        Predicate<String> foo = Schedulers.flowConstructPollingSchedulers("foo");
        assertTrue(foo.evaluate("polling://foo/1234"));
    }

    @Test
    public void failingValidatePollSchedulersPredicates()
    {
        Predicate<String> foo = Schedulers.flowConstructPollingSchedulers("foo");
        assertFalse(foo.evaluate("polling://fooFails/1234"));
    }

    @Test
    public void anyPollSchedulerIsAccepted()
    {
        Predicate<String> foo = Schedulers.allPollSchedulers();
        assertTrue(foo.evaluate("polling://fooFails/1234"));
    }

    @Test
    public void aNonPollSchedulerIsNotAccepted()
    {
        Predicate<String> foo = Schedulers.allPollSchedulers();
        assertFalse(foo.evaluate("fooFails/1234"));
    }
}
