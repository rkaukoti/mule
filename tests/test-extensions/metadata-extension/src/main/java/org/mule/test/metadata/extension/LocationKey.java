/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the terms of
 * the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.test.metadata.extension;

import org.mule.runtime.extension.api.annotation.metadata.MetadataKeyPart;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

import static org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals;

public class LocationKey {
  @MetadataKeyPart(order = 1)
  private String continent;

  @MetadataKeyPart(order = 2)
  private String country;

  @DisplayName("State | City")
  @MetadataKeyPart(order = 3)
  private String city;


  public String getContinent() {
    return continent;
  }

  public void setContinent(String continent) {
    this.continent = continent;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  @Override
  public String toString() {
    return String.format("%s|%s|%s", continent, country, city);
  }

  @Override
  public boolean equals(Object obj) {
    return reflectionEquals(this, obj);
  }
}
