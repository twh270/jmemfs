package org.byteworks.jmemfs.spi.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileAlreadyExistsException;

public class JMemFileInode extends JMemInode {
  private ByteBuffer storage;

  public JMemFileInode(final JMemInode parent, final String name) {
    super(parent, name);
    this.storage = ByteBuffer.allocate(0);
  }

  @Override
  public SeekableByteChannel createChannel() {
    return new JMemSeekableByteChannel(this);
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

  public int readBytes(final int position, final ByteBuffer dst) {
    final int bytesToRead = storage.capacity() - position;
    storage.position(position);
    if (bytesToRead > 0) {
      dst.put(storage);
    }
    return bytesToRead;
  }

  public int writeBytes(final int position, final ByteBuffer src) {
    final int bytesToWrite = src.remaining();
    if (position + bytesToWrite > storage.capacity()) {
      allocateStorage(position + bytesToWrite);
    }
    storage.position(position);
    storage.put(src);
    return bytesToWrite;
  }

  private void allocateStorage(final int capacity) {
    final ByteBuffer newStorage = ByteBuffer.allocate(capacity);
    newStorage.put(storage);
    storage = newStorage;
  }
}
