package org.byteworks.jmemfs.spi.impl;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import org.junit.Test;

public class JMemDirectoryInodeTest {
  @Test
  public void shouldCreateDirectory() throws IOException {
    final JMemDirectoryInode inode = new JMemDirectoryInode(null, "root");
    final JMemInode subDirectory = inode.createDirectory("subdirectory");
  }

  @Test
  public void shouldCreateFile() throws FileAlreadyExistsException {
    final JMemDirectoryInode inode = new JMemDirectoryInode(null, "root");
    final JMemInode fileNode = inode.createFile("file.txt");
  }

  @Test
  public void shouldGetChild() throws IOException {
    final JMemDirectoryInode inode = new JMemDirectoryInode(null, "root");
    final JMemInode subDirectory = inode.createDirectory("subdirectory");
    assertEquals(subDirectory, inode.getInodeFor("subdirectory"));
  }

  @Test
  public void shouldGetGrandChild() throws IOException {
    final JMemDirectoryInode inode = new JMemDirectoryInode(null, "root");
    final JMemInode child = inode.createDirectory("dir1");
    final JMemInode grandChild = child.createDirectory("dir2");
    assertEquals(child, inode.getInodeFor("dir1"));
    assertEquals(grandChild, child.getInodeFor("dir2"));
    assertEquals(grandChild, inode.getInodeFor(new String[] { "dir1", "dir2" }));
  }

  @Test
  public void shouldGetParent() throws IOException {
    final JMemDirectoryInode inode = new JMemDirectoryInode(null, "root");
    final JMemInode subDirectory = inode.createDirectory("subdirectory");
    assertEquals(inode, subDirectory.getParent());
  }

  @Test(expected = IllegalStateException.class)
  public void shouldNotAllowCreateChannel() {
    final JMemDirectoryInode inode = new JMemDirectoryInode(null, "root");
    inode.createChannel();
  }

  @Test
  public void shouldSetName() {
    final JMemDirectoryInode inode = new JMemDirectoryInode(null, "root");
    assertEquals("root", inode.getName());
  }

  @Test(expected = FileAlreadyExistsException.class)
  public void shouldThrowIfFileExists() throws FileAlreadyExistsException {
    final JMemDirectoryInode inode = new JMemDirectoryInode(null, "root");
    final JMemInode fileNode = inode.createFile("file.txt");
    inode.createFile("file.txt");
  }
}
