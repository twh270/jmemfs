package org.byteworks.jmemfs.spi.impl;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
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
  public JMemInode createDirectory(final Path name) throws IOException {
    updateATime();
    if (entries.containsKey(name.getFileName().toString()))
      throw new FileAlreadyExistsException(name.toString());
    final JMemInode node = new JMemDirectoryInode(this, name.getFileName().toString());
    updateMTime();
    entries.put(name.getFileName().toString(), node);
    return node;
  }

  @Override
  public JMemInode createFile(final Path name) throws FileAlreadyExistsException {
    updateATime();
    synchronized (entries) {
      if (getInodeFor(name.getFileName()) != null)
        throw new FileAlreadyExistsException(name.toString());
      final JMemInode fileInode = new JMemFileInode(this, name.getFileName().toString());
      entries.put(name.getFileName().toString(), fileInode);
      updateMTime();
      return fileInode;
    }
  }

  @Override
  public JMemInode getInodeFor(final Path path) {
    JMemInode curr = this;
    if (path.getNameCount() == 1)
      return getInodeFor(path.getFileName().toString());
    for (int i = 0; i < path.getNameCount() && curr != null; i++) {
      curr = curr.getInodeFor(path.getName(i));
    }
    return curr;
  }

  public JMemInode getInodeFor(final String name) {
    updateATime();
    if (".".equals(name))
      return this;
    else if ("..".equals(name))
      return getParent();
    else
      return entries.get(name);
  }

}
