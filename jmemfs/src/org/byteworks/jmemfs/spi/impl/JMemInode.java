package org.byteworks.jmemfs.spi.impl;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;

import org.byteworks.jmemfs.spi.JMemFileSystem;

public abstract class JMemInode {
  static final class SystemTimeProvider implements JMemTimeProvider {
    public static final SystemTimeProvider instance = new SystemTimeProvider();

    @Override
    public long currentTimeMillis() {
      return System.currentTimeMillis();
    }
  }
  private final String name;
  private final JMemDirectoryInode parent;
  private final JMemFileAttributes attributes;

  private final JMemTimeProvider timeProvider;

  public JMemInode(final JMemDirectoryInode parent, final String name, final JMemFileAttributes.FileType fileType,
      final JMemFileSystem fileSystem) {
    this.parent = parent;
    this.name = name;
    if (fileSystem.getEnvironment().containsKey("timeProvider")) {
      this.timeProvider = (JMemTimeProvider) fileSystem.getEnvironment().get("timeProvider");
    }
    else {
      this.timeProvider = SystemTimeProvider.instance;
    }
    this.attributes = new JMemFileAttributes(fileType, currentTime());
  }

  public JMemInode(final JMemDirectoryInode parent, final String name, final JMemFileAttributes.FileType fileType, final long now,
      final JMemFileSystem fileSystem) {
    this.parent = parent;
    this.name = name;
    if (fileSystem.getEnvironment().containsKey("timeProvider")) {
      this.timeProvider = (JMemTimeProvider) fileSystem.getEnvironment().get("timeProvider");
    }
    else {
      this.timeProvider = SystemTimeProvider.instance;
    }
    this.attributes = new JMemFileAttributes(fileType, now);
  }

  public abstract void copyTo(JMemInode target, boolean replace, boolean copyAttr) throws IOException;

  public abstract SeekableByteChannel createChannel();

  public abstract JMemInode createDirectory(Path name, JMemFileSystem fileSystem) throws IOException;

  public abstract JMemInode createFile(Path name, JMemFileSystem fileSystem) throws FileAlreadyExistsException;

  @Override
  public boolean equals(final Object other) {
    if (!(other instanceof JMemInode))
      return false;
    final JMemInode o = (JMemInode) other;
    return (name.equals(o.name) && attributes.equals(o.attributes) && (parent == null && o.parent == null || parent.equals(o.parent)));
  }

  public JMemFileAttributes getAttributes() {
    return attributes;
  }

  public abstract JMemInode getInodeFor(final Path path);

  public String getName() {
    updateATime();
    return name;
  }

  public JMemDirectoryInode getParent() {
    return parent;
  }

  public abstract void unlink() throws IOException;

  public void updateAttributes(final JMemFileAttributes attributes2) {
    getAttributes().updateMTime(attributes2.lastModifiedTime().toMillis());
    getAttributes().updateATime(attributes2.lastAccessTime().toMillis());
    getAttributes().updateCTime(attributes2.creationTime().toMillis());
    getAttributes().updateSize(attributes2.size());
  }

  long currentTime() {
    return timeProvider.currentTimeMillis();
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
