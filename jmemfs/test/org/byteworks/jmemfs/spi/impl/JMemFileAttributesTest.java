package org.byteworks.jmemfs.spi.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.byteworks.jmemfs.spi.JMemFileSystem;
import org.byteworks.jmemfs.spi.JMemFileSystemProvider;
import org.junit.Test;

public class JMemFileAttributesTest {
  @Test
  public void shouldGetCorrectAttributesForDirectory() {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.getTheFileSystem();
    final long now = System.currentTimeMillis();
    final JMemDirectoryInode root = new JMemDirectoryInode(null, "root", now, fs);
    final JMemFileAttributes attributes = root.getAttributes();
    assertEquals(now, attributes.creationTime().toMillis());
    assertEquals(now, attributes.lastAccessTime().toMillis());
    assertEquals(now, attributes.lastModifiedTime().toMillis());
    assertEquals(0, attributes.size());
    assertTrue(attributes.isDirectory());
  }

  @Test
  public void shouldGetCorrectAttributesForFile() {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.getTheFileSystem();
    final long now = System.currentTimeMillis();
    final JMemFileInode root = new JMemFileInode(null, "root", now, fs);
    final JMemFileAttributes attributes = root.getAttributes();
    assertEquals(now, attributes.creationTime().toMillis());
    assertEquals(now, attributes.lastAccessTime().toMillis());
    assertEquals(now, attributes.lastModifiedTime().toMillis());
    assertEquals(0, attributes.size());
    assertTrue(attributes.isRegularFile());
  }
}
