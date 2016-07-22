/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.cxf.jaxws;

import org.apache.hello_world_soap_http.GreeterImpl;
import org.mule.tck.probe.Probe;

public class GreeterNotNull implements Probe
{
    private GreeterImpl impl;

    public GreeterNotNull(GreeterImpl impl)
    {
        this.impl = impl;
    }

    public boolean isSatisfied()
    {
        return impl != null;
    }

    public String describeFailure()
    {
        return "Expected Greeter implementation but found null";
    }

}
