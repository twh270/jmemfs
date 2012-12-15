package org.byteworks.jmemfs.spi.impl;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashMap;
import java.util.Map;

public class JMemDirectoryInode extends JMemInode {
  private final Map<String, JMemInode> entries = new HashMap<String, JMemInode>();

  public JMemDirectoryInode(final JMemInode parent, final String name) {
    super(parent, name, JMemFileAttributes.FileType.DIRECTORY);
  }

  public JMemDirectoryInode(final JMemInode parent, final String name, final long now) {
    super(parent, name, JMemFileAttributes.FileType.DIRECTORY, now);
  }

  @Override
  public SeekableByteChannel createChannel() {
    throw new IllegalStateException("A directory inode cannot create a channel");
  }

  @Override
  public JMemInode createDirectory(final String name) throws IOException {
    updateATime();
    if (entries.containsKey(name))
      throw new FileAlreadyExistsException(name);
    final JMemInode node = new JMemDirectoryInode(this, name);
    updateMTime();
    entries.put(name, node);
    return node;
  }

  @Override
  public JMemInode createFile(final String name) throws FileAlreadyExistsException {
    updateATime();
    synchronized (entries) {
      if (getInodeFor(name) != null)
        throw new FileAlreadyExistsException(name);
      final JMemInode fileInode = new JMemFileInode(this, name);
      entries.put(name, fileInode);
      updateMTime();
      return fileInode;
    }
  }

  @Override
  public JMemInode getInodeFor(final String name) {
    updateATime();
    if (".".equals(name))
      return this;
    else if ("..".equals(name))
      return getParent();
    else
      return entries.get(name);
  }

  @Override
  public JMemInode getInodeFor(final String[] pathElements) {
    JMemInode curr = this;
    for (final String element : pathElements) {
      curr = curr.getInodeFor(element);
      if (curr == null)
        return null;
    }
    return curr;
  }
}
