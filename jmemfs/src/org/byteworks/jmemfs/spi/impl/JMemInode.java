package org.byteworks.jmemfs.spi.impl;

import java.io.IOException;

public abstract class JMemInode {
  private final String name;
  private final JMemInode parent;

  public JMemInode(final JMemInode parent, final String name) {
    this.parent = parent;
    this.name = name;
  }

  public abstract JMemInode createDirectory(String name) throws IOException;

  public abstract JMemInode getInodeForName(final String part);

  public String getName() {
    return name;
  }

  public JMemInode getParent() {
    return parent;
  }
}
