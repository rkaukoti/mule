/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.ftp;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mule.extension.FtpTestHarness;
import org.mule.extension.ftp.internal.FtpConnector;
import org.mule.functional.junit4.ExtensionFunctionalTestCase;
import org.mule.runtime.api.message.MuleEvent;
import org.mule.runtime.api.message.MuleMessage;
import org.mule.runtime.module.extension.file.api.FileWriteMode;
import org.mule.runtime.module.extension.file.api.stream.AbstractFileInputStream;

import java.util.Arrays;
import java.util.Collection;

import static org.mule.extension.FtpTestHarness.HELLO_PATH;

@RunWith(Parameterized.class)
public abstract class FtpConnectorTestCase extends ExtensionFunctionalTestCase
{

    @Rule
    public final FtpTestHarness testHarness;
    private final String name;

    public FtpConnectorTestCase(String name, FtpTestHarness testHarness)
    {
        this.name = name;
        this.testHarness = testHarness;
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> data()
    {
        return Arrays.asList(new Object[][] {
                {"ftp", new ClassicFtpTestHarness()},
                {"sftp", new SftpTestHarness()}
        });
    }

    @Override
    protected Class<?>[] getAnnotatedExtensionClasses()
    {
        return new Class<?>[] {FtpConnector.class};
    }

    protected MuleEvent readHelloWorld() throws Exception
    {
        return getPath(HELLO_PATH);
    }

    protected MuleMessage readPath(String path) throws Exception
    {
        return getPath(path).getMessage();
    }

    protected void doWrite(String path, Object content, FileWriteMode mode, boolean createParent) throws Exception
    {
        doWrite("write", path, content, mode, createParent);
    }

    protected void doWrite(String flow, String path, Object content, FileWriteMode mode, boolean createParent) throws Exception
    {
        doWrite(flow, path, content, mode, createParent, null);
    }

    protected void doWrite(String flow, String path, Object content, FileWriteMode mode, boolean createParent, String encoding)
            throws Exception
    {
        flowRunner(flow)
                .withFlowVariable("path", path)
                .withFlowVariable("createParent", createParent)
                .withFlowVariable("mode", mode)
                .withFlowVariable("encoding", encoding)
                .withPayload(content)
                .run();
    }

    private MuleEvent getPath(String path) throws Exception
    {
        return flowRunner("read")
                .withFlowVariable("path", path)
                .run();
    }

    protected String readPathAsString(String path) throws Exception
    {
        return getPayloadAsString(readPath(path));
    }

    protected boolean isLocked(MuleMessage message)
    {
        return ((AbstractFileInputStream) message.getPayload()).isLocked();
    }
}
