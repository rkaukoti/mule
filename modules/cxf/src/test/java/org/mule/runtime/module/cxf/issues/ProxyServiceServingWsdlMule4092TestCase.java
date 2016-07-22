/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.cxf.issues;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Rule;
import org.junit.Test;
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.runtime.core.util.IOUtils;
import org.mule.runtime.core.util.SystemUtils;
import org.mule.tck.junit4.rule.DynamicPort;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ProxyServiceServingWsdlMule4092TestCase extends FunctionalTestCase
{

    @Rule
    public DynamicPort dynamicPort = new DynamicPort("port1");
    private String expectedWsdlFileName;

    @Override
    protected String getConfigFile()
    {
        return "issues/proxy-service-serving-wsdl-mule4092-flow-httpn.xml";
    }

    @Override
    protected void doSetUp() throws Exception
    {
        super.doSetUp();
        XMLUnit.setIgnoreWhitespace(true);
        setupExpectedWsdlFileName();
    }

    /**
     * The WSDL generated by CXF is basically the same but slightly differs in
     * whitespace and element ordering (which does not matter). XMLUnit's javadoc
     * says it can ignore element ordering but obviously that does not work, hence
     * this hack.
     */
    private void setupExpectedWsdlFileName()
    {
        if (SystemUtils.isSunJDK() || SystemUtils.isAppleJDK())
        {
            expectedWsdlFileName = "test.wsdl";
        }
        else if (SystemUtils.isIbmJDK())
        {
            if (SystemUtils.isJavaVersionAtLeast(160))
            {
                expectedWsdlFileName = "test.wsdl.ibmjdk-6";
            }
            else
            {
                expectedWsdlFileName = "test.wsdl.ibmjdk-5";
            }
        }
        else
        {
            fail("Unknown JDK");
        }
    }

    @Test
    public void testProxyServiceWSDL() throws Exception
    {
        String expected = getXML("issues/" + expectedWsdlFileName);

        URL url = new URL("http://localhost:" + dynamicPort.getNumber() + "/services/onlinestore?wsdl");
        String wsdlFromService = IOUtils.toString(url.openStream());

        // The exact string representation may differ, so we'll spot check the WSDL
        // contents
        // assertTrue(compareResults(expected, wsdlFromService));
        Document expectedDom = buildDOM(expected);
        Document actualDom = buildDOM(wsdlFromService);

        // Check that it's WSDL
        Element topElement = expectedDom.getDocumentElement();
        String wsdlNamespace = topElement.getNamespaceURI();
        assertEquals(wsdlNamespace, actualDom.getDocumentElement().getNamespaceURI());
        assertEquals(topElement.getLocalName(), actualDom.getDocumentElement().getLocalName());

        Element expectedService = (Element) expectedDom.getElementsByTagNameNS(wsdlNamespace, "service")
                                                       .item(0);
        Element actualService = (Element) actualDom.getElementsByTagNameNS(wsdlNamespace, "service").item(0);
        assertNotNull(actualService);
        assertEquals(expectedService.getAttribute("name"), actualService.getAttribute("name"));

        Element expectedPort = (Element) expectedDom.getElementsByTagNameNS(wsdlNamespace, "port").item(0);
        Element actualPort = (Element) actualDom.getElementsByTagNameNS(wsdlNamespace, "port").item(0);
        assertNotNull(actualPort);
        assertEquals(expectedPort.getAttribute("name"), actualPort.getAttribute("name"));

        int expectedNumberOfMessages = expectedDom.getElementsByTagNameNS(wsdlNamespace, "message")
                                                  .getLength();
        int actualNumberOfmMessages = actualDom.getElementsByTagNameNS(wsdlNamespace, "message").getLength();
        assertEquals(expectedNumberOfMessages, actualNumberOfmMessages);
    }

    protected String getXML(String requestFile) throws Exception
    {
        String xml = IOUtils.toString(IOUtils.getResourceAsStream(requestFile, this.getClass()), "UTF-8");
        if (xml != null)
        {
            return xml;
        }
        else
        {
            fail("Unable to load test request file");
            return null;
        }
    }

    private Document buildDOM(String xmlString)
            throws ParserConfigurationException, IOException, SAXException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource source = new InputSource(new StringReader(xmlString));
        return builder.parse(source);
    }

}
