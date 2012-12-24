package org.byteworks.jmemfs.spi.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;

public class JMemFileInode extends JMemInode {
  private ByteBuffer storage;

  public JMemFileInode(final JMemDirectoryInode parent, final String name) {
    super(parent, name, JMemFileAttributes.FileType.FILE);
    this.storage = ByteBuffer.allocate(0);
  }

  public JMemFileInode(final JMemDirectoryInode parent, final String name, final long now) {
    super(parent, name, JMemFileAttributes.FileType.FILE, now);
    this.storage = ByteBuffer.allocate(0);
  }

  @Override
  public void copyTo(final JMemInode target, final boolean replace, final boolean copyAttr) throws IOException {
    final JMemFileInode targetFileInode = (JMemFileInode) target;
    updateATime();
    updateMTime();
    if (replace) {
      targetFileInode.truncate(0);
    }
    storage.position(0);
    targetFileInode.writeBytes(0, storage);
    if (copyAttr) {
      target.updateAttributes(getAttributes());
    }
  }

  @Override
  public SeekableByteChannel createChannel() {
    updateATime();
    return new JMemSeekableByteChannel(this);
  }

  @Override
  public JMemInode createDirectory(final Path name) throws IOException {
    throw new IllegalStateException("A file inode cannot create a directory");
  }

  @Override
  public JMemInode createFile(final Path name) throws FileAlreadyExistsException {
    throw new IllegalStateException("A file inode cannot create a file");
  }

  @Override
  public JMemInode getInodeFor(final Path path) {
    throw new IllegalStateException("A file inode cannot get a child inode");
  }

  public int readBytes(final int position, final ByteBuffer dst) {
    updateATime();
    final long size = getAttributes().size();
    final long bytesToRead = size - position;
    storage.position(position);
    storage.limit((int) size);
    if (bytesToRead > 0) {
      dst.put(storage);
    }
    return (int) bytesToRead;
  }

  public void truncate(final int size) {
    if (size > storage.capacity()) {
      allocateStorage(size + storage.capacity());
    }
    storage.limit(size);
    if (storage.position() > size) {
      storage.position(size);
    }
  }

  @Override
  public void unlink() throws IOException {
    getParent().unlink(this.getName());
  }

  public int writeBytes(final int position, final ByteBuffer src) {
    final int bytesToWrite = src.remaining();
    if (position + bytesToWrite > storage.capacity()) {
      allocateStorage(position + bytesToWrite);
    }
    storage.limit(storage.capacity());
    storage.position(position);
    storage.put(src);
    final int newPosition = storage.position();
    final long size = getAttributes().size();
    if (size < newPosition) {
      updateSize(newPosition);
    }
    updateATime();
    updateMTime();
    return bytesToWrite;
  }

  private void allocateStorage(final int requiredCapacity) {
    final int allocatedCapacity = requiredCapacity + (storage.capacity() / 10);
    final ByteBuffer newStorage = ByteBuffer.allocate(allocatedCapacity);
    final int pos = storage.position();
    storage.position(0);
    newStorage.put(storage);
    storage = newStorage;
    storage.position(pos);
  }
}
