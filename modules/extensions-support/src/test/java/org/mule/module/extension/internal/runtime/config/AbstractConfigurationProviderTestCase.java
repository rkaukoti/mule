/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.extension.introspection.ConfigurationModel;
import org.mule.extension.introspection.ExtensionModel;
import org.mule.module.extension.internal.runtime.DefaultOperationContext;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;
import org.mule.tck.util.TestTimeSupplier;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@SmallTest
@RunWith(MockitoJUnitRunner.class)
abstract class AbstractConfigurationProviderTestCase<T> extends AbstractMuleTestCase
{

    protected static final String CONFIG_NAME = "config";

    @Mock(answer = RETURNS_DEEP_STUBS)
    protected MuleContext muleContext;

    @Mock
    protected ExtensionModel extensionModel;

    @Mock(answer = RETURNS_DEEP_STUBS)
    protected ConfigurationModel configurationModel;

    @Mock(answer = RETURNS_DEEP_STUBS)
    protected DefaultOperationContext operationContext;

    @Mock
    protected MuleEvent event;

    protected TestTimeSupplier timeSupplier = new TestTimeSupplier(System.currentTimeMillis());
    protected LifecycleAwareConfigurationProvider<T> provider;

    @Before
    public void before() throws Exception
    {
        provider.setMuleContext(muleContext);
        when(muleContext.getInjector().inject(anyObject())).thenAnswer(invocation -> {
            Object target = invocation.getArguments()[0];
            if (target instanceof LifecycleAwareConfigurationInstance)
            {
                ((LifecycleAwareConfigurationInstance) target).setMuleContext(muleContext);
                ((LifecycleAwareConfigurationInstance) target).setTimeSupplier(timeSupplier);
            }

            return target;
        });

    }

    @Test
    public void getName()
    {
        assertThat(provider.getName(), is(CONFIG_NAME));
    }

    @Test
    public void getConfigurationModel()
    {
        assertThat(provider.getModel(), is(CoreMatchers.sameInstance(configurationModel)));
    }

    protected void assertSameInstancesResolved() throws Exception
    {
        final int count = 10;
        Object config = provider.get(event);

        for (int i = 1; i < count; i++)
        {
            assertThat(provider.get(event), is(sameInstance(config)));
        }
    }
}
