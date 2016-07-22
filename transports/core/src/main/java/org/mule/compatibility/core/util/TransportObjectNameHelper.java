/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.compatibility.core.util;

import org.mule.compatibility.core.api.endpoint.EndpointURI;
import org.mule.compatibility.core.api.transport.Connector;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.util.ObjectNameHelper;
import org.mule.runtime.core.util.StringUtils;

import static org.mule.compatibility.core.registry.MuleRegistryTransportHelper.lookupConnector;

/**
 * Generates consistent objects names for Mule components
 */
// @ThreadSafe
public final class TransportObjectNameHelper extends ObjectNameHelper {

  public static final String SEPARATOR = ".";
  // public static final char HASH = '#';
  public static final String CONNECTOR_PREFIX = "connector";
  public static final String ENDPOINT_PREFIX = "endpoint";
  public static final String DEFAULT = "mule.default";

  public TransportObjectNameHelper(MuleContext muleContext) {
    super(muleContext);
  }

  /**
   * @deprecated Transport infrastructure is deprecated.
   */
  @Deprecated
  public static String getEndpointNameFor(EndpointURI endpointUri) {
    String address = endpointUri.getAddress();
    if (StringUtils.isBlank(address)) {
      // for some endpoints in TCK like test://xxx
      address = endpointUri.toString();
    }
    // Make sure we include the endpoint scheme in the name
    address = (address.indexOf(":/") > -1 ? address : endpointUri.getScheme() + SEPARATOR + address);
    return ENDPOINT_PREFIX + SEPARATOR + replaceObjectNameChars(address);
  }

  /**
   * @deprecated Transport infrastructure is deprecated.
   */
  @Deprecated
  public static boolean isDefaultAutoGeneratedConnector(Connector connector) {
    return connector.getName().startsWith(CONNECTOR_PREFIX + SEPARATOR + connector.getProtocol() + SEPARATOR + DEFAULT);
  }

  /**
   * @deprecated Transport infrastructure is deprecated.
   */
  @Deprecated
  public static String replaceObjectNameChars(String name) {
    String value = name.replaceAll("//", SEPARATOR);
    value = value.replaceAll("\\p{Punct}", SEPARATOR);
    value = value.replaceAll("\\" + SEPARATOR + "{2,}", SEPARATOR);
    if (value.endsWith(SEPARATOR)) {
      value = value.substring(0, value.length() - 1);
    }
    return value;
  }

  /**
   * @deprecated Transport infrastructure is deprecated.
   */
  @Deprecated
  public String getEndpointName(final EndpointURI endpointUri) {
    String name = getEndpointNameFor(endpointUri);

    return ensureUniqueEndpoint(name);
  }

  /**
   * @deprecated Transport infrastructure is deprecated.
   */
  @Deprecated
  protected String ensureUniqueEndpoint(String name) {
    int i = 0;
    String tempName = name;
    // Check that the generated name does not conflict with an existing global
    // endpoint.
    // We can't check local edpoints right now but the chances of conflict are
    // very small and will be
    // reported during JMX object registration
    while (getMuleContext().getRegistry().lookupObject(tempName) != null) {
      i++;
      tempName = name + SEPARATOR + i;
    }
    return tempName;
  }

  /**
   * @deprecated Transport infrastructure is deprecated.
   */
  @Deprecated
  protected String ensureUniqueConnector(String name) {
    int i = 0;
    String tempName = name;
    // Check that the generated name does not conflict with an existing global
    // endpoint.
    // We can't check local edpoints right now but the chances of conflict are
    // very small and will be
    // reported during JMX object registration
    try {
      while (lookupConnector(getMuleContext().getRegistry(), tempName) != null) {
        i++;
        tempName = name + SEPARATOR + i;
      }
    } catch (Exception e) {
      // ignore
    }
    return tempName;
  }

  /**
   * @deprecated Transport infrastructure is deprecated.
   */
  @Deprecated
  public String getConnectorName(Connector connector) {
    if (connector.getName() != null && connector.getName().indexOf('#') == -1) {
      String name = replaceObjectNameChars(connector.getName());
      return ensureUniqueConnector(name);
    } else {
      String name = CONNECTOR_PREFIX + SEPARATOR + connector.getProtocol() + SEPARATOR + DEFAULT;
      return ensureUniqueConnector(name);
    }
  }
}
