/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.runtime.container.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mule.runtime.core.config.bootstrap.ClassPathRegistryBootstrapDiscoverer.BOOTSTRAP_PROPERTIES;
import static org.mule.runtime.module.artifact.classloader.ClassLoaderLookupStrategy.CHILD_FIRST;
import static org.mule.runtime.module.artifact.classloader.ClassLoaderLookupStrategy.PARENT_ONLY;
import org.mule.runtime.module.artifact.classloader.ArtifactClassLoader;
import org.mule.runtime.module.artifact.classloader.ClassLoaderLookupPolicy;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class ContainerClassLoaderFactoryTestCase
{

    @Test
    public void createsClassLoaderLookupPolicy() throws Exception
    {
        final ContainerClassLoaderFactory factory = new ContainerClassLoaderFactory();
        final ModuleDiscoverer moduleDiscoverer = mock(ModuleDiscoverer.class);
        final List<MuleModule> modules = new ArrayList<>();
        modules.add(new TestModuleBuilder("module1").exportingPackages("org.foo1", "org.foo1.bar").build());
        modules.add(new TestModuleBuilder("module2").exportingPackages("org.foo2").build());
        when(moduleDiscoverer.discover()).thenReturn(modules);
        factory.setModuleDiscoverer(moduleDiscoverer);

        final ArtifactClassLoader containerClassLoader = factory.createContainerClassLoader(this.getClass().getClassLoader());

        final ClassLoaderLookupPolicy classLoaderLookupPolicy = containerClassLoader.getClassLoaderLookupPolicy();
        assertThat(classLoaderLookupPolicy.getLookupStrategy("org.foo1.Foo"), is(PARENT_ONLY));
        assertThat(classLoaderLookupPolicy.getLookupStrategy("org.foo1.bar.Bar"), is(PARENT_ONLY));
        assertThat(classLoaderLookupPolicy.getLookupStrategy("org.foo2.Fo"), is(PARENT_ONLY));
        assertThat(classLoaderLookupPolicy.getLookupStrategy("org.foo2.bar.Bar"), is(CHILD_FIRST));
    }

    @Test
    public void getResourcesFromParent() throws Exception
    {
        final ContainerClassLoaderFactory factory = createClassLoaderExportingBootstrapProperties();

        final ArtifactClassLoader containerClassLoader = factory.createContainerClassLoader(this.getClass().getClassLoader());

        final Enumeration<URL> resources = containerClassLoader.getClassLoader().getResources(BOOTSTRAP_PROPERTIES);
        assertThat(resources.hasMoreElements(), is(true));

        Set<String> items = new HashSet<>();
        int size = 0;
        while (resources.hasMoreElements())
        {
            final String url = resources.nextElement().toString();
            items.add(url);
            size++;
        }

        assertThat(size, equalTo(items.size()));
    }

    @Test
    public void doesNotFindAnyResource() throws Exception
    {
        final ContainerClassLoaderFactory factory = createClassLoaderExportingBootstrapProperties();

        final ArtifactClassLoader containerClassLoader = factory.createContainerClassLoader(this.getClass().getClassLoader());

        final URL resource = containerClassLoader.findResource(BOOTSTRAP_PROPERTIES);
        assertThat(resource, is(nullValue()));
    }

    @Test
    public void doesNotFindAnyResources() throws Exception
    {
        final ContainerClassLoaderFactory factory = createClassLoaderExportingBootstrapProperties();

        final ArtifactClassLoader containerClassLoader = factory.createContainerClassLoader(this.getClass().getClassLoader());

        final Enumeration<URL> resources = containerClassLoader.findResources(BOOTSTRAP_PROPERTIES);
        assertThat(resources.hasMoreElements(), is(false));
    }

    private ContainerClassLoaderFactory createClassLoaderExportingBootstrapProperties()
    {
        final ContainerClassLoaderFactory factory = new ContainerClassLoaderFactory();
        final ModuleDiscoverer moduleDiscoverer = mock(ModuleDiscoverer.class);
        final List<MuleModule> modules = new ArrayList<>();
        modules.add(new TestModuleBuilder("module1").exportingPaths("META-INF/services/org/mule/runtime/core/config").build());
        when(moduleDiscoverer.discover()).thenReturn(modules);
        factory.setModuleDiscoverer(moduleDiscoverer);
        return factory;
    }
}