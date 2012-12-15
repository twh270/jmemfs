package org.byteworks.jmemfs.spi.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.byteworks.jmemfs.spi.impl.JMemDirectoryInode;
import org.byteworks.jmemfs.spi.impl.JMemFileAttributes;
import org.byteworks.jmemfs.spi.impl.JMemFileInode;
import org.junit.Test;

public class JMemFileAttributesTest {
  @Test
  public void shouldGetCorrectAttributesForDirectory() {
    final long now = System.currentTimeMillis();
    final JMemDirectoryInode root = new JMemDirectoryInode(null, "root", now);
    final JMemFileAttributes attributes = root.getAttributes();
    assertEquals(now, attributes.creationTime().toMillis());
    assertEquals(now, attributes.lastAccessTime().toMillis());
    assertEquals(now, attributes.lastModifiedTime().toMillis());
    assertEquals(0, attributes.size());
    assertTrue(attributes.isDirectory());
  }

  @Test
  public void shouldGetCorrectAttributesForFile() {
    final long now = System.currentTimeMillis();
    final JMemFileInode root = new JMemFileInode(null, "root", now);
    final JMemFileAttributes attributes = root.getAttributes();
    assertEquals(now, attributes.creationTime().toMillis());
    assertEquals(now, attributes.lastAccessTime().toMillis());
    assertEquals(now, attributes.lastModifiedTime().toMillis());
    assertEquals(0, attributes.size());
    assertTrue(attributes.isRegularFile());
  }
}
