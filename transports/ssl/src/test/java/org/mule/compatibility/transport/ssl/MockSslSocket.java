/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.compatibility.transport.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

/**
 * {@link SSLSocket} subclass that can be used to mock SSL related tests
 */
public class MockSslSocket extends SSLSocket
{

    private InputStream inputStream;

    public void addHandshakeCompletedListener(HandshakeCompletedListener listener)
    {
        // not needed
    }

    public boolean getEnableSessionCreation()
    {
        return false;
    }

    public void setEnableSessionCreation(boolean flag)
    {
        // not needed
    }

    public String[] getEnabledCipherSuites()
    {
        return null;
    }

    public void setEnabledCipherSuites(String[] suites)
    {
        // not needed
    }

    public String[] getEnabledProtocols()
    {
        return null;
    }

    public void setEnabledProtocols(String[] protocols)
    {
        // not needed
    }

    public boolean getNeedClientAuth()
    {
        return false;
    }

    public void setNeedClientAuth(boolean need)
    {
        // not needed
    }

    public SSLSession getSession()
    {
        return null;
    }

    public String[] getSupportedCipherSuites()
    {
        return null;
    }

    public String[] getSupportedProtocols()
    {
        return null;
    }

    public boolean getUseClientMode()
    {
        return false;
    }

    public void setUseClientMode(boolean mode)
    {
        // not needed
    }

    public boolean getWantClientAuth()
    {
        return false;
    }

    public void setWantClientAuth(boolean want)
    {
        // not needed
    }

    public void removeHandshakeCompletedListener(HandshakeCompletedListener listener)
    {
        // not needed
    }

    public void startHandshake() throws IOException
    {
        // not needed
    }

    @Override
    public InputStream getInputStream() throws IOException
    {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }

    @Override
    public OutputStream getOutputStream() throws IOException
    {
        return null;
    }

    @Override
    public SocketAddress getRemoteSocketAddress()
    {
        return new InetSocketAddress("localhost", 12345);
    }
}


