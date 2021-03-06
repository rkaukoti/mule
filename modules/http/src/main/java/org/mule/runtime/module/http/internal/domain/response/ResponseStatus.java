/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.http.internal.domain.response;

public class ResponseStatus
{
    private int statusCode;
    private String reasonPhrase;

    public ResponseStatus()
    {
        this.statusCode = 200;
        this.reasonPhrase = "";
    }

    public ResponseStatus(final int statusCode, final String reasonPhrase)
    {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public int getStatusCode()
    {
        return statusCode;
    }

    public String getReasonPhrase()
    {
        return reasonPhrase;
    }

    public void setStatusCode(int statusCode)
    {
        this.statusCode = statusCode;
    }

    public void setReasonPhrase(String reasonPhrase)
    {
        this.reasonPhrase = reasonPhrase;
    }

}
