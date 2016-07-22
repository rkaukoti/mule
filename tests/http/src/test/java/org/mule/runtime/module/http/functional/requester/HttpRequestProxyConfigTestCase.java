/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.http.functional.requester;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mule.extension.http.api.request.proxy.NtlmProxyConfig;
import org.mule.extension.http.api.request.proxy.ProxyConfig;
import org.mule.extension.http.internal.request.validator.HttpRequesterProvider;
import org.mule.runtime.core.api.MessagingException;
import org.mule.runtime.core.internal.connection.ConnectionProviderWrapper;
import org.mule.runtime.core.util.concurrent.Latch;
import org.mule.runtime.extension.api.runtime.ConfigurationInstance;
import org.mule.runtime.module.http.functional.AbstractHttpTestCase;
import org.mule.tck.junit4.rule.DynamicPort;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Collection;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mule.runtime.module.extension.internal.util.ExtensionsTestUtils.getConfigurationInstanceFromRegistry;

@RunWith(Parameterized.class)
public class HttpRequestProxyConfigTestCase extends AbstractHttpTestCase
{

    private static final String PROXY_HOST = "localhost";
    private static final String PROXY_USERNAME = "theUsername";
    private static final String PROXY_PASSWORD = "thePassword";
    private static final String PROXY_NTLM_DOMAIN = "theNtlmDomain";

    @Rule
    public DynamicPort proxyPort = new DynamicPort("proxyPort");

    @Rule
    public DynamicPort httpPort = new DynamicPort("httpPort");
    @Parameter(0)
    public String flowName;
    @Parameter(1)
    public ProxyType proxyType;
    private Thread mockProxyAcceptor;
    private Latch latch = new Latch();
    private Latch proxyReadyLatch = new Latch();

    @Parameters(name = "{0}")
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList(new Object[][] {
                {"RefAnonymousProxy", ProxyType.ANONYMOUS},
                {"InnerAnonymousProxy", ProxyType.ANONYMOUS},
                {"RefUserPassProxy", ProxyType.USER_PASS},
                {"InnerUserPassProxy", ProxyType.USER_PASS},
                {"RefNtlmProxy", ProxyType.NTLM},
                {"InnerNtlmProxy", ProxyType.NTLM}});
    }

    @Override
    protected String getConfigFile()
    {
        return "http-request-proxy-config.xml";
    }

    @Before
    public void startMockProxy() throws IOException, InterruptedException
    {
        mockProxyAcceptor = new MockProxy();
        mockProxyAcceptor.start();

        // Give time to the proxy thread to start up completely
        proxyReadyLatch.await();
        Thread.yield();
    }

    @After
    public void stopMockProxy() throws Exception
    {
        mockProxyAcceptor.join();
    }

    @Test
    public void testProxy() throws Exception
    {
        checkProxyConfig();
        ensureRequestGoesThroughProxy(flowName);
    }

    private void checkProxyConfig() throws Exception
    {
        ConfigurationInstance config = getConfigurationInstanceFromRegistry("config" + flowName, getTestEvent(TEST_PAYLOAD));
        ConnectionProviderWrapper providerWrapper = (ConnectionProviderWrapper) config.getConnectionProvider().get();
        HttpRequesterProvider provider = (HttpRequesterProvider) providerWrapper.getDelegate();
        ProxyConfig proxyConfig = provider.getProxyConfig();

        assertThat(proxyConfig.getHost(), is(PROXY_HOST));
        assertThat(proxyConfig.getPort(), is(Integer.valueOf(proxyPort.getValue())));

        if (proxyType == ProxyType.USER_PASS || proxyType == ProxyType.NTLM)
        {
            assertThat(proxyConfig.getUsername(), is(PROXY_USERNAME));
            assertThat(proxyConfig.getPassword(), is(PROXY_PASSWORD));
            if (proxyType == ProxyType.NTLM)
            {
                assertThat(proxyConfig, is(instanceOf(NtlmProxyConfig.class)));
                assertThat(((NtlmProxyConfig) proxyConfig).getNtlmDomain(), is(PROXY_NTLM_DOMAIN));
            }
        }
    }

    private void ensureRequestGoesThroughProxy(String flowName) throws Exception
    {
        MessagingException e = flowRunner(flowName).withPayload(TEST_MESSAGE).runExpectingException();
        // Request should go through the proxy.
        assertThat(e.getCauseException(), is(instanceOf(IOException.class)));
        assertThat(e.getCauseException().getMessage(), is("Remotely closed"));
        latch.await(1, SECONDS);
    }

    private enum ProxyType
    {
        ANONYMOUS,
        USER_PASS,
        NTLM
    }

    private class MockProxy extends Thread
    {

        @Override
        public void run()
        {
            ServerSocket serverSocket = null;
            try
            {
                serverSocket = new ServerSocket(Integer.parseInt(proxyPort.getValue()));
                proxyReadyLatch.countDown();
                serverSocket.accept().close();
                latch.release();
            }
            catch (IOException e)
            { /* Ignore */ }
            finally
            {
                if (serverSocket != null)
                {
                    try
                    {
                        serverSocket.close();
                    }
                    catch (IOException e)
                    { /* Ignore */ }
                }
            }
        }
    }
}
