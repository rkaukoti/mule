/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.impl;

import org.mule.MuleServer;
import org.mule.RegistryContext;
import org.mule.api.MuleContext;
import org.mule.api.MuleContextBuilder;
import org.mule.api.MuleContextFactory;
import org.mule.api.config.ConfigurationBuilder;
import org.mule.config.ConfigurationException;
import org.mule.impl.config.builders.AutoConfigurationBuilder;
import org.mule.impl.config.builders.DefaultsConfigurationBuilder;
import org.mule.impl.config.builders.SimpleConfigurationBuilder;
import org.mule.umo.lifecycle.InitialisationException;

import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Default implementation that stores MuleContext in {@link MuleServer} static and
 * uses {@link DefaultMuleContextBuilder} to build new {@link MuleContext} instances.
 */
public class DefaultMuleContextFactory implements MuleContextFactory
{

    protected static final Log logger = LogFactory.getLog(DefaultMuleContextBuilder.class);

    /**
     * {@inheritDoc
     */
    public MuleContext createMuleContext() throws InitialisationException, ConfigurationException
    {
        // Create MuleContext using default MuleContextBuilder
        MuleContext muleContext = doCreateMuleContext(null);

        // Configure with defaults needed for a feasible/startable MuleContext
        new DefaultsConfigurationBuilder().configure(muleContext);

        return muleContext;
    }

    /**
     * {@inheritDoc
     */
    public MuleContext createMuleContext(ConfigurationBuilder configurationBuilder)
        throws InitialisationException, ConfigurationException
    {
        // Create MuleContext using default MuleContextBuilder
        MuleContext muleContext = doCreateMuleContext(null);

        // Configure
        configurationBuilder.configure(muleContext);

        return muleContext;
    }

    /**
     * {@inheritDoc
     */
    public MuleContext createMuleContext(List configurationBuilders, MuleContextBuilder muleContextBuilder)
        throws InitialisationException, ConfigurationException
    {
        // Create MuleContext
        MuleContext muleContext = doCreateMuleContext(muleContextBuilder);

        // Configure
        for (int i = 0; i < configurationBuilders.size(); i++)
        {
            ((ConfigurationBuilder) configurationBuilders.get(i)).configure(muleContext);
        }

        return muleContext;
    }

    /**
     * {@inheritDoc
     */
    public MuleContext createMuleContext(MuleContextBuilder muleContextBuilder)
        throws InitialisationException, ConfigurationException
    {
        return doCreateMuleContext(muleContextBuilder);
    }

    /**
     * {@inheritDoc
     */
    public MuleContext createMuleContext(ConfigurationBuilder configurationBuilder,
                                         MuleContextBuilder muleContextBuilder)
        throws InitialisationException, ConfigurationException
    {
        // Create MuleContext
        MuleContext muleContext = doCreateMuleContext(muleContextBuilder);

        // Configure
        configurationBuilder.configure(muleContext);

        return muleContext;
    }

    // Additional Factory methods provided by this implementation.

    /**
     * Creates a new {@link MuleContext} instance from the resource provided.
     * Implementations of {@link MuleContextFactory} can either use a default
     * {@link ConfigurationBuilder} to implement this, or do some auto-detection to
     * determine the {@link ConfigurationBuilder} that should be used.
     * 
     * @param configResources comma seperated list of configuration resources.
     * @return
     * @throws InitialisationException
     * @throws ConfigurationException
     */
    public MuleContext createMuleContext(String resource)
        throws InitialisationException, ConfigurationException
    {
        return createMuleContext(resource, null);
    }

    /**
     * Creates a new {@link MuleContext} instance from the resource provided.
     * Implementations of {@link MuleContextFactory} can either use a default
     * {@link ConfigurationBuilder} to implement this, or do some auto-detection to
     * determine the {@link ConfigurationBuilder} that should be used. Properties if
     * provided are used to replace "property placeholder" value in configuration
     * files.
     * 
     * @param resource
     * @param properties
     * @return
     * @throws InitialisationException
     * @throws ConfigurationException
     */
    public MuleContext createMuleContext(String configResources, Properties properties)
        throws InitialisationException, ConfigurationException
    {
        // Create MuleContext
        MuleContext muleContext = doCreateMuleContext(null);

        // Configure with startup properties
        if (properties != null && !properties.isEmpty())
        {
            new SimpleConfigurationBuilder(properties).configure(muleContext);
        }

        // Automatically resolve Configuration to be used and delegate configuration
        // to it.
        new AutoConfigurationBuilder(configResources).configure(muleContext);

        return muleContext;
    }

    /**
     * Creates a new MuleContext using the given configurationBuilder. Properties if
     * provided are used to replace "property placeholder" value in configuration
     * files.
     * 
     * @param configurationBuilder
     * @param properties
     * @return
     * @throws InitialisationException
     * @throws ConfigurationException
     */
    public MuleContext createMuleContext(ConfigurationBuilder configurationBuilder, Properties properties)
        throws InitialisationException, ConfigurationException
    {
        // Create MuleContext
        MuleContext muleContext = doCreateMuleContext(null);

        // Configure with startup properties
        if (properties != null && !properties.isEmpty())
        {
            new SimpleConfigurationBuilder(properties).configure(muleContext);
        }

        // Configure with cconfigurationBuilder
        configurationBuilder.configure(muleContext);

        return muleContext;
    }

    protected MuleContext doCreateMuleContext(MuleContextBuilder muleContextBuilder)
        throws InitialisationException
    {
        // Create transent registry
        RegistryContext.getOrCreateRegistry();

        // Create muleContext instance and set it in MuleServer
        MuleContext muleContext = buildMuleContext(muleContextBuilder);
        MuleServer.setMuleContext(muleContext);

        // Initialiase MuleContext
        muleContext.initialise();

        return muleContext;
    }

    protected MuleContext buildMuleContext(MuleContextBuilder muleContextBuilder)
    {
        // If no MuleContextBuilder is specfied use DefaultMuleContextBuilder
        if (muleContextBuilder == null)
        {
            muleContextBuilder = new DefaultMuleContextBuilder();
        }
        return muleContextBuilder.buildMuleContext();
    }

}
