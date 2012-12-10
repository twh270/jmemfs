package org.byteworks.jmemfs.spi.impl;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashMap;
import java.util.Map;

public class JMemDirectoryInode extends JMemInode {
  private final Map<String, JMemInode> entries = new HashMap<String, JMemInode>();

  public JMemDirectoryInode(final JMemInode parent, final String name) {
    super(parent, name);
  }

  @Override
  public JMemInode createDirectory(final String name) throws IOException {
    if (entries.containsKey(name))
      throw new FileAlreadyExistsException(name);
    final JMemInode node = new JMemDirectoryInode(this, name);
    entries.put(name, node);
    return node;
  }

  @Override
  public JMemInode getInodeForName(final String name) {
    if (".".equals(name))
      return this;
    else if ("..".equals(name))
      return getParent();
    else
      return entries.get(name);
  }
}
