/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.enricher;

import static java.lang.String.format;
import static org.mule.runtime.extension.api.connectivity.OperationTransactionalAction.JOIN_IF_POSSIBLE;
import static org.mule.runtime.extension.api.introspection.parameter.ExpressionSupport.NOT_SUPPORTED;
import static org.mule.runtime.module.extension.internal.ExtensionProperties.TRANSACTIONAL_ACTION_PARAMETER_DESCRIPTION;
import static org.mule.runtime.module.extension.internal.ExtensionProperties.TRANSACTIONAL_ACTION_PARAMETER_NAME;
import org.mule.metadata.api.ClassTypeLoader;
import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.extension.api.connectivity.OperationTransactionalAction;
import org.mule.runtime.extension.api.introspection.declaration.DescribingContext;
import org.mule.runtime.extension.api.introspection.declaration.fluent.OperationDeclaration;
import org.mule.runtime.extension.api.introspection.declaration.fluent.ParameterDeclaration;
import org.mule.runtime.extension.api.introspection.declaration.spi.ModelEnricher;
import org.mule.runtime.extension.api.introspection.declaration.type.ExtensionsTypeLoaderFactory;
import org.mule.runtime.extension.api.introspection.operation.OperationModel;
import org.mule.runtime.module.extension.internal.exception.IllegalOperationModelDefinitionException;
import org.mule.runtime.module.extension.internal.model.property.ConnectivityModelProperty;
import org.mule.runtime.module.extension.internal.runtime.connectivity.ConnectionInterceptor;
import org.mule.runtime.module.extension.internal.util.IdempotentDeclarationWalker;

/**
 * Adds a {@link ConnectionInterceptor} to all {@link OperationModel operations} which
 * contain the {@link ConnectivityModelProperty}
 *
 * @since 4.0
 */
public class ConnectionModelEnricher implements ModelEnricher
{

    private final MetadataType transactionalActionType;


    public ConnectionModelEnricher()
    {
        ClassTypeLoader typeLoader = ExtensionsTypeLoaderFactory.getDefault().createTypeLoader();
        transactionalActionType = typeLoader.load(OperationTransactionalAction.class);
    }

    @Override
    public void enrich(DescribingContext describingContext)
    {
        new IdempotentDeclarationWalker()
        {
            @Override
            protected void onOperation(OperationDeclaration declaration)
            {
                declaration.getModelProperty(ConnectivityModelProperty.class)
                        .ifPresent(property ->
                                   {
                                       declaration.addInterceptorFactory(ConnectionInterceptor::new);
                                       if (property.supportsTransactions())
                                       {
                                           addTransactionalActionParameter(describingContext, declaration);
                                       }
                                   });
            }
        }.walk(describingContext.getExtensionDeclarer().getDeclaration());
    }

    private void addTransactionalActionParameter(DescribingContext describingContext, OperationDeclaration declaration)
    {
        declaration.getParameters().stream()
                .filter(parameter -> TRANSACTIONAL_ACTION_PARAMETER_NAME.equals(parameter.getName()))
                .findAny()
                .ifPresent(p ->
                           {
                               throw new IllegalOperationModelDefinitionException(format("Operation '%s' from extension '%s' defines a parameter named '%s' which is a reserved word",
                                                                                         declaration.getName(),
                                                                                         describingContext.getExtensionDeclarer().getDeclaration().getName(),
                                                                                         TRANSACTIONAL_ACTION_PARAMETER_NAME
                               ));
                           });

        ParameterDeclaration transactionParameter = new ParameterDeclaration(TRANSACTIONAL_ACTION_PARAMETER_NAME);
        transactionParameter.setType(transactionalActionType, false);
        transactionParameter.setExpressionSupport(NOT_SUPPORTED);
        transactionParameter.setRequired(false);
        transactionParameter.setDefaultValue(JOIN_IF_POSSIBLE);
        transactionParameter.setDescription(TRANSACTIONAL_ACTION_PARAMETER_DESCRIPTION);

        declaration.addParameter(transactionParameter);
    }
}
