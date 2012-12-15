package org.byteworks.jmemfs.spi.impl;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileAlreadyExistsException;

public abstract class JMemInode {
  private final String name;
  private final JMemInode parent;

  public JMemInode(final JMemInode parent, final String name) {
    this.parent = parent;
    this.name = name;
  }

  public abstract SeekableByteChannel createChannel();

  public abstract JMemInode createDirectory(String name) throws IOException;

  public abstract JMemInode createFile(String name) throws FileAlreadyExistsException;

  public abstract JMemInode getInodeFor(final String part);

  public abstract JMemInode getInodeFor(final String[] pathElements) throws IOException;

  public String getName() {
    return name;
  }

  public JMemInode getParent() {
    return parent;
  }
}
