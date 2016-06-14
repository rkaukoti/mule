/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.db.api.config;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Parameter;
import org.mule.runtime.extension.api.annotation.param.Optional;

import java.util.concurrent.TimeUnit;

/**
 * @since 4.0
 */
@Alias("pooling-profile")
public class DbPoolingProfile
{

    /**
     * Maximum number of connections a pool maintains at any given time
     */
    @Parameter
    @Optional
    private Integer maxPoolSize;

    /**
     * Minimum number of connections a pool maintains at any given time
     */
    @Parameter
    @Optional
    private Integer minPoolSize;

    /**
     * Determines how many connections at a time to try to acquire when the pool is exhausted
     */
    @Parameter
    @Optional
    private Integer acquireIncrement;

    /**
     * Determines how many statements are cached per pooled connection. Defaults to 0, meaning statement caching is disabled
     */
    @Parameter
    @Optional
    private Integer preparedStatementCacheSize;

    /**
     * The number of milliseconds a client trying to obtain a connection waits for
     * it to be checked-in or acquired when the pool is exhausted. Zero (default) means wait indefinitely
     */
    @Parameter
    @Optional(defaultValue = "0")
    private int maxWait = 0;

    /**
     * A {@link TimeUnit} which qualifies the {@link #maxWait}.
     */
    @Parameter
    @Optional(defaultValue = "SECONDS")
    private TimeUnit maxWaitUnit;
}
