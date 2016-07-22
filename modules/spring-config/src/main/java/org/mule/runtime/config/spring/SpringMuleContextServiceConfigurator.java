/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.config.spring;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.mule.runtime.config.spring.factories.ConstantFactoryBean;
import org.mule.runtime.config.spring.factories.ExtensionManagerFactoryBean;
import org.mule.runtime.config.spring.factories.TransactionManagerFactoryBean;
import org.mule.runtime.config.spring.processors.MuleObjectNameProcessor;
import org.mule.runtime.config.spring.processors.ParentContextPropertyPlaceholderProcessor;
import org.mule.runtime.config.spring.processors.PropertyPlaceholderProcessor;
import org.mule.runtime.core.DynamicDataTypeConversionResolver;
import org.mule.runtime.core.api.CustomService;
import org.mule.runtime.core.api.CustomizationService;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.MuleRuntimeException;
import org.mule.runtime.core.api.context.notification.ConnectionNotificationListener;
import org.mule.runtime.core.api.context.notification.CustomNotificationListener;
import org.mule.runtime.core.api.context.notification.ExceptionNotificationListener;
import org.mule.runtime.core.api.context.notification.ManagementNotificationListener;
import org.mule.runtime.core.api.context.notification.MuleContextNotificationListener;
import org.mule.runtime.core.api.context.notification.RegistryNotificationListener;
import org.mule.runtime.core.api.context.notification.SecurityNotificationListener;
import org.mule.runtime.core.api.context.notification.TransactionNotificationListener;
import org.mule.runtime.core.api.lifecycle.InitialisationException;
import org.mule.runtime.core.config.ChainedThreadingProfile;
import org.mule.runtime.core.config.bootstrap.ArtifactType;
import org.mule.runtime.core.config.factories.HostNameFactory;
import org.mule.runtime.core.connector.MuleConnectorOperationLocator;
import org.mule.runtime.core.context.notification.ConnectionNotification;
import org.mule.runtime.core.context.notification.CustomNotification;
import org.mule.runtime.core.context.notification.ExceptionNotification;
import org.mule.runtime.core.context.notification.ManagementNotification;
import org.mule.runtime.core.context.notification.MessageProcessingFlowTraceManager;
import org.mule.runtime.core.context.notification.MuleContextNotification;
import org.mule.runtime.core.context.notification.RegistryNotification;
import org.mule.runtime.core.context.notification.SecurityNotification;
import org.mule.runtime.core.context.notification.TransactionNotification;
import org.mule.runtime.core.el.mvel.MVELExpressionLanguage;
import org.mule.runtime.core.el.mvel.MVELExpressionLanguageWrapper;
import org.mule.runtime.core.exception.MessagingExceptionLocationProvider;
import org.mule.runtime.core.execution.MuleMessageProcessingManager;
import org.mule.runtime.core.expression.DefaultExpressionManager;
import org.mule.runtime.core.internal.connection.DefaultConnectionManager;
import org.mule.runtime.core.internal.metadata.MuleMetadataManager;
import org.mule.runtime.core.management.stats.DefaultProcessingTimeWatcher;
import org.mule.runtime.core.retry.policies.NoRetryPolicyTemplate;
import org.mule.runtime.core.security.MuleSecurityManager;
import org.mule.runtime.core.time.TimeSupplier;
import org.mule.runtime.core.util.DefaultStreamCloserService;
import org.mule.runtime.core.util.lock.MuleLockFactory;
import org.mule.runtime.core.util.lock.SingleServerLockProvider;
import org.mule.runtime.core.util.queue.DelegateQueueManager;
import org.mule.runtime.core.util.store.DefaultObjectStoreFactoryBean;
import org.mule.runtime.core.util.store.MuleObjectStoreManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.String.format;
import static org.mule.runtime.core.api.config.MuleProperties.DEFAULT_LOCAL_TRANSIENT_USER_OBJECT_STORE_NAME;
import static org.mule.runtime.core.api.config.MuleProperties.DEFAULT_LOCAL_USER_OBJECT_STORE_NAME;
import static org.mule.runtime.core.api.config.MuleProperties.DEFAULT_USER_OBJECT_STORE_NAME;
import static org.mule.runtime.core.api.config.MuleProperties.DEFAULT_USER_TRANSIENT_OBJECT_STORE_NAME;
import static org.mule.runtime.core.api.config.MuleProperties.LOCAL_OBJECT_STORE_MANAGER;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_CONNECTION_MANAGER;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_CONNECTOR_MESSAGE_PROCESSOR_LOCATOR;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_CONVERTER_RESOLVER;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_DEFAULT_MESSAGE_DISPATCHER_THREADING_PROFILE;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_DEFAULT_MESSAGE_PROCESSING_MANAGER;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_DEFAULT_MESSAGE_RECEIVER_THREADING_PROFILE;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_DEFAULT_MESSAGE_REQUESTER_THREADING_PROFILE;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_DEFAULT_RETRY_POLICY_TEMPLATE;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_DEFAULT_SERVICE_THREADING_PROFILE;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_DEFAULT_THREADING_PROFILE;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_EXCEPTION_LOCATION_PROVIDER;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_EXPRESSION_LANGUAGE;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_EXTENSION_MANAGER;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_LOCAL_QUEUE_MANAGER;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_LOCAL_STORE_IN_MEMORY;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_LOCAL_STORE_PERSISTENT;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_LOCK_FACTORY;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_LOCK_PROVIDER;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_MESSAGE_PROCESSING_FLOW_TRACE_MANAGER;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_METADATA_MANAGER;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_MULE_CONFIGURATION;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_MULE_STREAM_CLOSER_SERVICE;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_NOTIFICATION_MANAGER;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_OBJECT_NAME_PROCESSOR;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_PROCESSING_TIME_WATCHER;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_QUEUE_MANAGER;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_SECURITY_MANAGER;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_SERIALIZER;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_STORE_DEFAULT_IN_MEMORY_NAME;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_STORE_DEFAULT_PERSISTENT_NAME;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_STORE_MANAGER;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_TIME_SUPPLIER;
import static org.mule.runtime.core.api.config.MuleProperties.OBJECT_TRANSACTION_MANAGER;
import static org.mule.runtime.core.api.config.MuleProperties.QUEUE_STORE_DEFAULT_IN_MEMORY_NAME;
import static org.mule.runtime.core.api.config.MuleProperties.QUEUE_STORE_DEFAULT_PERSISTENT_NAME;
import static org.mule.runtime.core.config.bootstrap.ArtifactType.APP;
import static org.mule.runtime.core.util.ClassUtils.loadClass;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

/**
 * This class configured all the services available in a {@code MuleContext}.
 * <p>
 * There's a predefined set of services plus a configurable set of services provided by {@code MuleContext#getCustomizationService}.
 * <p>
 * This class takes cares of registering bean definitions for each of the provided services
 * so dependency injection can be propertly done through the use of {@link javax.inject.Inject}.
 *
 * @since 4.0
 */
class SpringMuleContextServiceConfigurator
{

    private static final String ENDPOINT_FACTORY_IMPL_CLASS_NAME = "org.mule.compatibility.core.endpoint.DefaultEndpointFactory";
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringMuleContextServiceConfigurator.class);
    private static final ImmutableSet<String> APPLICATION_ONLY_SERVICES = ImmutableSet.<String>builder()
            .add(OBJECT_SECURITY_MANAGER)
            .add(OBJECT_DEFAULT_MESSAGE_PROCESSING_MANAGER)
            .add(OBJECT_MULE_STREAM_CLOSER_SERVICE)
            .add(OBJECT_CONVERTER_RESOLVER)
            .add(OBJECT_LOCK_FACTORY)
            .add(OBJECT_LOCK_PROVIDER)
            .add(OBJECT_PROCESSING_TIME_WATCHER)
            .add(OBJECT_CONNECTOR_MESSAGE_PROCESSOR_LOCATOR)
            .add(OBJECT_EXCEPTION_LOCATION_PROVIDER)
            .add(OBJECT_MESSAGE_PROCESSING_FLOW_TRACE_MANAGER)
            .build();
    private static final ImmutableMap<String, String> OBJECT_STORE_NAME_TO_LOCAL_OBJECT_STORE_NAME = ImmutableMap.<String, String>builder()
            .put(OBJECT_STORE_DEFAULT_IN_MEMORY_NAME, OBJECT_LOCAL_STORE_IN_MEMORY)
            .put(OBJECT_STORE_DEFAULT_PERSISTENT_NAME, OBJECT_LOCAL_STORE_PERSISTENT)
            .put(DEFAULT_USER_OBJECT_STORE_NAME, DEFAULT_LOCAL_USER_OBJECT_STORE_NAME)
            .put(DEFAULT_USER_TRANSIENT_OBJECT_STORE_NAME, DEFAULT_LOCAL_TRANSIENT_USER_OBJECT_STORE_NAME)
            .build();
    private final MuleContext muleContext;
    private final ArtifactType artifactType;
    private final OptionalObjectsController optionalObjectsController;
    private final CustomizationService customizationService;
    private final BeanDefinitionRegistry beanDefinitionRegistry;
    //Do not use static field. BeanDefinitions are reused and produce weird behaviour
    private final ImmutableMap<String, BeanDefinition> defaultContextServices = ImmutableMap.<String, BeanDefinition>builder()
            .put(OBJECT_TRANSACTION_MANAGER, getBeanDefinition(TransactionManagerFactoryBean.class))
            .put(OBJECT_DEFAULT_RETRY_POLICY_TEMPLATE, getBeanDefinition(NoRetryPolicyTemplate.class))
            .put(OBJECT_EXPRESSION_LANGUAGE, getBeanDefinition(MVELExpressionLanguageWrapper.class))
            .put(OBJECT_EXTENSION_MANAGER, getBeanDefinition(ExtensionManagerFactoryBean.class))
            .put(OBJECT_TIME_SUPPLIER, getBeanDefinition(TimeSupplier.class))
            .put(OBJECT_CONNECTION_MANAGER, getBeanDefinition(DefaultConnectionManager.class))
            .put(OBJECT_METADATA_MANAGER, getBeanDefinition(MuleMetadataManager.class))
            .put(OBJECT_OBJECT_NAME_PROCESSOR, getBeanDefinition(MuleObjectNameProcessor.class))
            .put(OBJECT_SERIALIZER, getBeanDefinitionBuilder(DefaultObjectSerializerFactoryBean.class)
                    .addDependsOn(OBJECT_MULE_CONFIGURATION)
                    .getBeanDefinition())
            .put(OBJECT_NOTIFICATION_MANAGER, createNotificationManagerBeanDefinition())
            .put(OBJECT_STORE_DEFAULT_IN_MEMORY_NAME,
                    getBeanDefinitionBuilder(ConstantFactoryBean.class).addConstructorArgReference(OBJECT_LOCAL_STORE_IN_MEMORY)
                                                                       .getBeanDefinition())
            .put(OBJECT_LOCAL_STORE_IN_MEMORY, getBeanDefinition(DefaultObjectStoreFactoryBean.class, "createDefaultInMemoryObjectStore"))
            .put(OBJECT_STORE_DEFAULT_PERSISTENT_NAME,
                    getBeanDefinitionBuilder(ConstantFactoryBean.class).addConstructorArgReference(OBJECT_LOCAL_STORE_PERSISTENT)
                                                                       .getBeanDefinition())
            .put(OBJECT_LOCAL_STORE_PERSISTENT,
                    getBeanDefinition(DefaultObjectStoreFactoryBean.class, "createDefaultPersistentObjectStore"))
            .put(DEFAULT_USER_OBJECT_STORE_NAME,
                    getBeanDefinitionBuilder(ConstantFactoryBean.class).addConstructorArgReference(DEFAULT_LOCAL_USER_OBJECT_STORE_NAME)
                                                                       .getBeanDefinition())
            .put(DEFAULT_LOCAL_USER_OBJECT_STORE_NAME,
                    getBeanDefinition(DefaultObjectStoreFactoryBean.class, "createDefaultUserObjectStore"))
            .put(DEFAULT_USER_TRANSIENT_OBJECT_STORE_NAME, getBeanDefinitionBuilder(ConstantFactoryBean.class).addConstructorArgReference(
                    DEFAULT_LOCAL_TRANSIENT_USER_OBJECT_STORE_NAME).getBeanDefinition())
            .put(DEFAULT_LOCAL_TRANSIENT_USER_OBJECT_STORE_NAME,
                    getBeanDefinition(DefaultObjectStoreFactoryBean.class, "createDefaultUserTransientObjectStore"))
            .put(OBJECT_STORE_MANAGER, getBeanDefinition(MuleObjectStoreManager.class))
            .put(QUEUE_STORE_DEFAULT_PERSISTENT_NAME,
                    getBeanDefinition(DefaultObjectStoreFactoryBean.class, "createDefaultPersistentQueueStore"))
            .put(QUEUE_STORE_DEFAULT_IN_MEMORY_NAME,
                    getBeanDefinition(DefaultObjectStoreFactoryBean.class, "createDefaultInMemoryQueueStore"))
            .put(OBJECT_QUEUE_MANAGER,
                    getBeanDefinitionBuilder(ConstantFactoryBean.class).addConstructorArgReference(OBJECT_LOCAL_QUEUE_MANAGER)
                                                                       .getBeanDefinition())
            .put(OBJECT_LOCAL_QUEUE_MANAGER, getBeanDefinition(DelegateQueueManager.class))
            .put(OBJECT_DEFAULT_THREADING_PROFILE, getBeanDefinition(ChainedThreadingProfile.class))
            .put(OBJECT_DEFAULT_SERVICE_THREADING_PROFILE, getBeanDefinition(ChainedThreadingProfile.class))
            .put(OBJECT_DEFAULT_MESSAGE_DISPATCHER_THREADING_PROFILE,
                    getBeanDefinitionBuilder(ChainedThreadingProfile.class).addConstructorArgReference(OBJECT_DEFAULT_THREADING_PROFILE)
                                                                           .getBeanDefinition())
            .put(OBJECT_DEFAULT_MESSAGE_REQUESTER_THREADING_PROFILE,
                    getBeanDefinitionBuilder(ChainedThreadingProfile.class).addConstructorArgReference(OBJECT_DEFAULT_THREADING_PROFILE)
                                                                           .getBeanDefinition())
            .put(OBJECT_DEFAULT_MESSAGE_RECEIVER_THREADING_PROFILE,
                    getBeanDefinitionBuilder(ChainedThreadingProfile.class).addConstructorArgReference(OBJECT_DEFAULT_THREADING_PROFILE)
                                                                           .getBeanDefinition())
            .put("_muleParentContextPropertyPlaceholderProcessor", getBeanDefinition(ParentContextPropertyPlaceholderProcessor.class))
            .put("_mulePropertyPlaceholderProcessor", createMulePropertyPlaceholderBeanDefinition())
            .put(OBJECT_SECURITY_MANAGER, getBeanDefinition(MuleSecurityManager.class))
            .put(OBJECT_DEFAULT_MESSAGE_PROCESSING_MANAGER, getBeanDefinition(MuleMessageProcessingManager.class))
            .put(OBJECT_MULE_STREAM_CLOSER_SERVICE, getBeanDefinition(DefaultStreamCloserService.class))
            .put(OBJECT_CONVERTER_RESOLVER, getBeanDefinition(DynamicDataTypeConversionResolver.class))
            .put(OBJECT_LOCK_FACTORY, getBeanDefinition(MuleLockFactory.class))
            .put(OBJECT_LOCK_PROVIDER, getBeanDefinition(SingleServerLockProvider.class))
            .put(OBJECT_PROCESSING_TIME_WATCHER, getBeanDefinition(DefaultProcessingTimeWatcher.class))
            .put(OBJECT_CONNECTOR_MESSAGE_PROCESSOR_LOCATOR, getBeanDefinition(MuleConnectorOperationLocator.class))
            .put(OBJECT_EXCEPTION_LOCATION_PROVIDER, getBeanDefinition(MessagingExceptionLocationProvider.class))
            .put(OBJECT_MESSAGE_PROCESSING_FLOW_TRACE_MANAGER, getBeanDefinition(MessageProcessingFlowTraceManager.class))
            .build();

    public SpringMuleContextServiceConfigurator(MuleContext muleContext, ArtifactType artifactType,
                                                OptionalObjectsController optionalObjectsController,
                                                BeanDefinitionRegistry beanDefinitionRegistry)
    {
        this.muleContext = muleContext;
        this.customizationService = muleContext.getCustomizationService();
        this.artifactType = artifactType;
        this.optionalObjectsController = optionalObjectsController;
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }

    private static BeanDefinition createMulePropertyPlaceholderBeanDefinition()
    {
        HashMap<Object, Object> factories = new HashMap<>();
        factories.put("hostname", new HostNameFactory());
        BeanDefinitionBuilder mulePropertyPlaceholderProcessor = getBeanDefinitionBuilder(PropertyPlaceholderProcessor.class);
        return mulePropertyPlaceholderProcessor
                .addPropertyValue("factories", factories)
                .addPropertyValue("ignoreUnresolvablePlaceholders", true)
                .getBeanDefinition();
    }

    private static BeanDefinition createNotificationManagerBeanDefinition()
    {
        List<NotificationConfig> defaultNotifications = new ArrayList<>();
        defaultNotifications.add(new NotificationConfig(MuleContextNotificationListener.class, MuleContextNotification.class));
        defaultNotifications.add(new NotificationConfig(SecurityNotificationListener.class, SecurityNotification.class));
        defaultNotifications.add(new NotificationConfig(ManagementNotificationListener.class, ManagementNotification.class));
        defaultNotifications.add(new NotificationConfig(ConnectionNotificationListener.class, ConnectionNotification.class));
        defaultNotifications.add(new NotificationConfig(RegistryNotificationListener.class, RegistryNotification.class));
        defaultNotifications.add(new NotificationConfig(CustomNotificationListener.class, CustomNotification.class));
        defaultNotifications.add(new NotificationConfig(ExceptionNotificationListener.class, ExceptionNotification.class));
        defaultNotifications.add(new NotificationConfig(TransactionNotificationListener.class, TransactionNotification.class));
        return getBeanDefinitionBuilder(ServerNotificationManagerConfigurator.class)
                .addPropertyValue("enabledNotifications", defaultNotifications)
                .getBeanDefinition();
    }

    private static BeanDefinition getBeanDefinition(Class<?> beanType)
    {
        return getBeanDefinitionBuilder(beanType).getBeanDefinition();
    }

    private static BeanDefinition getConstantObjectBeanDefinition(Object impl)
    {
        return getBeanDefinitionBuilder(ConstantFactoryBean.class).addConstructorArgValue(impl).getBeanDefinition();
    }

    private static BeanDefinitionBuilder getBeanDefinitionBuilder(Class<?> beanType)
    {
        return genericBeanDefinition(beanType);
    }

    private static BeanDefinition getBeanDefinition(Class<?> beanType, String factoryMethodName)
    {
        return getBeanDefinitionBuilder(beanType)
                .setFactoryMethod(factoryMethodName)
                .getBeanDefinition();
    }

    void createArtifactServices()
    {
        initialiseExpressionManager();
        defaultContextServices.entrySet()
                              .stream()
                              .filter(service -> !APPLICATION_ONLY_SERVICES.contains(service.getKey()) || artifactType.equals(APP))
                              .forEach(service ->
                              {
                                  registerBeanDefinition(service.getKey(), service.getValue());
                              });
        createBootstrapBeanDefinitions();
        createLocalObjectStoreBeanDefinitions();
        createQueueStoreBeanDefinitions();
        createQueueManagerBeanDefinitions();
        createEndpointFactory();
    }

    private void initialiseExpressionManager()
    {
        try
        {
            //TODO MULE-9638 - DB parsers use the expression language before initialisation phase. Remove once it gets migrated to SDK.
            MVELExpressionLanguage expressionLanguage = new MVELExpressionLanguage(muleContext);
            expressionLanguage.initialise();
            ((DefaultExpressionManager) muleContext.getExpressionManager()).setExpressionLanguage(expressionLanguage);
        }
        catch (InitialisationException e)
        {
            throw new MuleRuntimeException(e);
        }
    }

    private void registerBeanDefinition(String serviceId, BeanDefinition defaultBeanDefinition)
    {
        BeanDefinition beanDefinition = defaultBeanDefinition;
        Optional<CustomService> customServiceOptional = customizationService.getCustomizedService(serviceId);
        if (customServiceOptional.isPresent())
        {
            Optional<Class> customServiceClass = customServiceOptional.get().getServiceClass();
            Optional<Object> customServiceImpl = customServiceOptional.get().getServiceImpl();
            if (customServiceClass.isPresent())
            {
                beanDefinition = getBeanDefinitionBuilder(customServiceClass.get()).getBeanDefinition();
            }
            else if (customServiceImpl.isPresent())
            {
                beanDefinition = getConstantObjectBeanDefinition(customServiceImpl.get());
            }
        }
        beanDefinitionRegistry.registerBeanDefinition(serviceId, beanDefinition);
    }

    private void createQueueStoreBeanDefinitions()
    {
        beanDefinitionRegistry.registerAlias(QUEUE_STORE_DEFAULT_PERSISTENT_NAME, "_fileQueueStore");
        beanDefinitionRegistry.registerAlias(QUEUE_STORE_DEFAULT_IN_MEMORY_NAME, "_simpleMemoryQueueStore");
    }

    private void createQueueManagerBeanDefinitions()
    {
        if (customizationService.getCustomizedService(OBJECT_QUEUE_MANAGER).isPresent())
        {
            registerBeanDefinition(OBJECT_LOCAL_QUEUE_MANAGER,
                    getBeanDefinitionBuilder(ConstantFactoryBean.class).addConstructorArgReference(OBJECT_LOCAL_QUEUE_MANAGER)
                                                                       .getBeanDefinition());
        }
        else
        {
            registerBeanDefinition(OBJECT_LOCAL_QUEUE_MANAGER, getBeanDefinition(DelegateQueueManager.class));
        }
    }

    private void createLocalObjectStoreBeanDefinitions()
    {
        AtomicBoolean anyBaseStoreWasRedefined = new AtomicBoolean(false);
        OBJECT_STORE_NAME_TO_LOCAL_OBJECT_STORE_NAME.entrySet().forEach(objectStoreLocal ->
        {
            customizationService.getCustomizedService(objectStoreLocal.getKey()).ifPresent(customService ->
            {
                beanDefinitionRegistry.registerAlias(objectStoreLocal.getKey(), objectStoreLocal.getValue());
                customService.getServiceClass().ifPresent(serviceClass ->
                {
                    anyBaseStoreWasRedefined.set(true);
                    beanDefinitionRegistry.registerBeanDefinition(objectStoreLocal.getValue(),
                            defaultContextServices.get(objectStoreLocal.getKey()));
                });
            });
        });

        if (anyBaseStoreWasRedefined.get())
        {
            beanDefinitionRegistry.registerBeanDefinition(LOCAL_OBJECT_STORE_MANAGER, getBeanDefinitionBuilder(MuleObjectStoreManager.class)
                    .addPropertyValue("basePersistentStoreKey", new RuntimeBeanReference(OBJECT_STORE_DEFAULT_PERSISTENT_NAME))
                    .addPropertyValue("baseTransientStoreKey", new RuntimeBeanReference(OBJECT_STORE_DEFAULT_IN_MEMORY_NAME))
                    .addPropertyValue("basePersistentUserStoreKey", new RuntimeBeanReference(DEFAULT_LOCAL_USER_OBJECT_STORE_NAME))
                    .addPropertyValue("baseTransientUserStoreKey", new RuntimeBeanReference(DEFAULT_LOCAL_TRANSIENT_USER_OBJECT_STORE_NAME))
                    .getBeanDefinition());
        }
        else
        {
            beanDefinitionRegistry.registerAlias(OBJECT_STORE_MANAGER, LOCAL_OBJECT_STORE_MANAGER);
        }
    }

    private void createEndpointFactory()
    {
        try
        {
            Class endpointFactoryClass = loadClass(ENDPOINT_FACTORY_IMPL_CLASS_NAME, Thread.currentThread().getContextClassLoader());
            beanDefinitionRegistry.registerBeanDefinition("_muleEndpointFactory", getBeanDefinition(endpointFactoryClass));
        }
        catch (ClassNotFoundException e)
        {
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug(format("Could not load class endpoint factory implementation %s. Endpoint factory will not be available.",
                        ENDPOINT_FACTORY_IMPL_CLASS_NAME), e);
            }
        }
    }

    private void createBootstrapBeanDefinitions()
    {
        try
        {
            SpringRegistryBootstrap springRegistryBootstrap =
                    new SpringRegistryBootstrap(artifactType, muleContext, optionalObjectsController, beanDefinitionRegistry);
            springRegistryBootstrap.initialise();
        }
        catch (InitialisationException e)
        {
            throw new RuntimeException(e);
        }
    }

}
