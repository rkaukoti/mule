/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.http.internal.listener.async;

import org.mule.runtime.module.http.internal.domain.request.HttpRequestContext;

/**
 * Handler for an incoming http request that allows to send the http response asynchronously.
 */
public interface RequestHandler
{

    /**
     * Called to handle an incoming http request
     *
     * @param requestContext http request content
     * @param responseCallback callback to call when the response content is ready.
     */
    void handleRequest(HttpRequestContext requestContext, HttpResponseReadyCallback responseCallback);

}
