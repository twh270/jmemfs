package org.byteworks.jmemfs.spi.impl;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileAlreadyExistsException;

public class JMemFileInode extends JMemInode {
  public JMemFileInode(final JMemInode parent, final String name) {
    super(parent, name);
  }

  @Override
  public SeekableByteChannel createChannel() {
    return new JMemSeekableByteChannel();
  }

  @Override
  public JMemInode createDirectory(final String name) throws IOException {
    throw new IllegalStateException("A file inode cannot create a directory");
  }

  @Override
  public JMemInode createFile(final String name) throws FileAlreadyExistsException {
    throw new IllegalStateException("A file inode cannot create a file");
  }

  @Override
  public JMemInode getInodeFor(final String part) {
    throw new IllegalStateException("A file inode cannot get a child inode");
  }

  @Override
  public JMemInode getInodeFor(final String[] pathElements) throws IOException {
    throw new IllegalStateException("A file inode cannot get a child inode");
  }

}
