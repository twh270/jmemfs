package org.byteworks.jmemfs.spi.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.byteworks.jmemfs.spi.JMemFileSystem;
import org.byteworks.jmemfs.spi.JMemFileSystemProvider;
import org.junit.Test;

public class JMemFileAttributesTest {
  @Test
  public void shouldGetCorrectAttributesForDirectory() {
    final long now = System.currentTimeMillis();
    final JMemTimeProvider timeProvider = new JMemTimeProvider() {
      @Override
      public long currentTimeMillis() {
        return now;
      }
    };
    final Map<String, Object> env = new HashMap<String, Object>();
    env.put("timeProvider", timeProvider);
    final JMemFileSystemProvider p = new JMemFileSystemProvider(env);
    final JMemFileSystem fs = p.getTheFileSystem();
    final JMemDirectoryInode root = new JMemDirectoryInode(null, "root", fs);
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
    final JMemTimeProvider timeProvider = new JMemTimeProvider() {
      @Override
      public long currentTimeMillis() {
        return now;
      }
    };
    final Map<String, Object> env = new HashMap<String, Object>();
    env.put("timeProvider", timeProvider);
    final JMemFileSystemProvider p = new JMemFileSystemProvider(env);
    final JMemFileSystem fs = p.getTheFileSystem();
    final JMemFileInode root = new JMemFileInode(null, "root", fs);
    final JMemFileAttributes attributes = root.getAttributes();
    assertEquals(now, attributes.creationTime().toMillis());
    assertEquals(now, attributes.lastAccessTime().toMillis());
    assertEquals(now, attributes.lastModifiedTime().toMillis());
    assertEquals(0, attributes.size());
    assertTrue(attributes.isRegularFile());
  }
}
