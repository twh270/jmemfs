package org.byteworks.jmemfs.spi.impl;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public class JMemFileAttributes implements BasicFileAttributes {
  public enum FileType {
    FILE, DIRECTORY, LINK, OTHER
  };

  FileTime modified;
  FileTime accessed;
  FileTime created;
  long size;
  FileType type;

  public JMemFileAttributes(final FileType fileType, final long now) {
    this.modified = this.accessed = this.created = FileTime.fromMillis(now);
    this.type = fileType;
    this.size = 0;
  }

  @Override
  public FileTime creationTime() {
    return created;
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof JMemFileAttributes))
      return false;
    final JMemFileAttributes o = (JMemFileAttributes) obj;
    return (modified.equals(o.modified) && accessed.equals(o.accessed) && created.equals(o.created) && size == o.size && type == o.type);
  }

  @Override
  public Object fileKey() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isDirectory() {
    return type == FileType.DIRECTORY;
  }

  @Override
  public boolean isOther() {
    return type == FileType.OTHER;
  }

  @Override
  public boolean isRegularFile() {
    return type == FileType.FILE;
  }

  @Override
  public boolean isSymbolicLink() {
    return type == FileType.LINK;
  }

  @Override
  public FileTime lastAccessTime() {
    return accessed;
  }

  @Override
  public FileTime lastModifiedTime() {
    return modified;
  }

  @Override
  public long size() {
    return size;
  }

  public void updateATime(final long time) {
    this.accessed = FileTime.fromMillis(time);
  }

  public void updateMTime(final long time) {
    this.modified = FileTime.fromMillis(time);
  }

  public void updateSize(final long newSize) {
    this.size = newSize;
  }

  void updateCTime(final long time) {
    this.created = FileTime.fromMillis(time);
  }

}
