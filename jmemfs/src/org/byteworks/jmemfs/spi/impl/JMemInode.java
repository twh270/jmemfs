package org.byteworks.jmemfs.spi.impl;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;

public abstract class JMemInode {
  private final String name;
  private final JMemInode parent;
  private final JMemFileAttributes attributes;

  public JMemInode(final JMemInode parent, final String name, final JMemFileAttributes.FileType fileType) {
    this.parent = parent;
    this.name = name;
    this.attributes = new JMemFileAttributes(fileType, currentTime());
  }

  public JMemInode(final JMemInode parent, final String name, final JMemFileAttributes.FileType fileType, final long now) {
    this.parent = parent;
    this.name = name;
    this.attributes = new JMemFileAttributes(fileType, now);
  }

  public abstract SeekableByteChannel createChannel();

  public abstract JMemInode createDirectory(Path name) throws IOException;

  public abstract JMemInode createFile(Path name) throws FileAlreadyExistsException;

  public JMemFileAttributes getAttributes() {
    return attributes;
  }

  public abstract JMemInode getInodeFor(final Path path);

  public String getName() {
    updateATime();
    return name;
  }

  public JMemInode getParent() {
    return parent;
  }

  long currentTime() {
    return System.currentTimeMillis();
  }

  void updateATime() {
    getAttributes().updateATime(currentTime());
  }

  void updateCTime() {
    getAttributes().updateCTime(currentTime());
  }

  void updateMTime() {
    getAttributes().updateMTime(currentTime());
  }

  void updateSize(final long newSize) {
    getAttributes().updateSize(newSize);
  }

}
