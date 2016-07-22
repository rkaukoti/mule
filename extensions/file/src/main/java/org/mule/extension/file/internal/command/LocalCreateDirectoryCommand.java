/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published under the
 * terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.mule.extension.file.internal.command;

import org.mule.extension.file.internal.LocalFileSystem;
import org.mule.runtime.module.extension.file.api.FileConnectorConfig;
import org.mule.runtime.module.extension.file.api.command.CreateDirectoryCommand;

import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.String.format;

/**
 * A {@link LocalFileCommand} which implements the {@link CreateDirectoryCommand} contract
 *
 * @since 4.0
 */
public final class LocalCreateDirectoryCommand extends LocalFileCommand implements CreateDirectoryCommand {

  /**
   * {@inheritDoc}
   */
  public LocalCreateDirectoryCommand(LocalFileSystem fileSystem) {
    super(fileSystem);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createDirectory(FileConnectorConfig config, String directoryPath) {
    Path target = resolvePath(config, directoryPath);

    if (Files.exists(target)) {
      throw new IllegalArgumentException(format("Directory '%s' already exists", target));
    }

    mkdirs(config, target);
  }
}
