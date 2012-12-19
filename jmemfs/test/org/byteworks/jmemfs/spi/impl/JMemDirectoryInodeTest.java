package org.byteworks.jmemfs.spi.impl;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import org.byteworks.jmemfs.spi.JMemFileSystem;
import org.byteworks.jmemfs.spi.JMemFileSystemProvider;
import org.byteworks.jmemfs.spi.JMemPath;
import org.junit.Test;

public class JMemDirectoryInodeTest {
  @Test
  public void shouldCreateDirectory() throws IOException {
    final JMemDirectoryInode inode = new JMemDirectoryInode(null, "root");
    final JMemFileSystem fs = new JMemFileSystem(new JMemFileSystemProvider());
    final JMemInode subDirectory = inode.createDirectory(new JMemPath(fs, "subdirectory"));
  }

  @Test
  public void shouldCreateFile() throws FileAlreadyExistsException {
    final JMemDirectoryInode inode = new JMemDirectoryInode(null, "root");
    final JMemFileSystem fs = new JMemFileSystem(new JMemFileSystemProvider());
    final JMemInode fileNode = inode.createFile(new JMemPath(fs, "file.txt"));
  }

  @Test
  public void shouldGetChild() throws IOException {
    final JMemDirectoryInode inode = new JMemDirectoryInode(null, "root");
    final JMemFileSystem fs = new JMemFileSystem(new JMemFileSystemProvider());
    final JMemInode subDirectory = inode.createDirectory(new JMemPath(fs, "subdirectory"));
    assertEquals(subDirectory, inode.getInodeFor("subdirectory"));
  }

  @Test
  public void shouldGetGrandChild() throws IOException {
    final JMemDirectoryInode inode = new JMemDirectoryInode(null, "root");
    final JMemFileSystem fs = new JMemFileSystem(new JMemFileSystemProvider());
    final JMemInode child = inode.createDirectory(new JMemPath(fs, "dir1"));
    final JMemInode grandChild = child.createDirectory(new JMemPath(fs, "dir2"));
    assertEquals(child, inode.getInodeFor("dir1"));
    assertEquals(grandChild, child.getInodeFor(new JMemPath(fs, "dir2")));
  }

  @Test
  public void shouldGetGrandChildFromRoot() throws IOException {
    final JMemDirectoryInode inode = new JMemDirectoryInode(null, "root");
    final JMemFileSystem fs = new JMemFileSystem(new JMemFileSystemProvider());
    final JMemInode child = inode.createDirectory(new JMemPath(fs, "dir1"));
    final JMemInode grandChild = child.createDirectory(new JMemPath(fs, "dir2"));
    assertEquals(grandChild, inode.getInodeFor(new JMemPath(null, "/dir1/dir2/")));
    assertEquals(grandChild, inode.getInodeFor(new JMemPath(null, "/dir1/dir2")));
  }

  @Test
  public void shouldGetParent() throws IOException {
    final JMemDirectoryInode inode = new JMemDirectoryInode(null, "root");
    final JMemFileSystem fs = new JMemFileSystem(new JMemFileSystemProvider());
    final JMemInode subDirectory = inode.createDirectory(new JMemPath(fs, "subdirectory"));
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
    final JMemFileSystem fs = new JMemFileSystem(new JMemFileSystemProvider());
    final JMemInode fileNode = inode.createFile(new JMemPath(fs, "file.txt"));
    inode.createFile(new JMemPath(fs, "file.txt"));
  }
}
