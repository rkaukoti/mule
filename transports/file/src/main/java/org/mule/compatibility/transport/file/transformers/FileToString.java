/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.compatibility.transport.file.transformers;

import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.core.api.transformer.TransformerException;

import java.nio.charset.Charset;

/**
 * <code>FileToString</code> reads file contents into a string.
 */
public class FileToString extends FileToByteArray {

  public FileToString() {
    setReturnDataType(DataType.STRING);
  }

  /**
   * Simple implementation which relies on {@link FileToByteArray} to get a <code>byte[]</code> from the file beeing parsed and
   * then transform it to a String with the correct encoding. If the encoding isn't supported simply throw an exception, good
   * tranformation or no transformation at all. NOTE: if a <code>byte[]</code> is passed in as a source object this transformer
   * accepts it and tries the usual transformation.
   */
  @Override
  public Object doTransform(Object src, Charset encoding) throws TransformerException {
    byte[] bytes = (byte[]) super.doTransform(src, encoding);
    return new String(bytes, encoding);
  }

}
