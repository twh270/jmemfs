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
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.getTheFileSystem();
    final JMemDirectoryInode inode = new JMemDirectoryInode(null, "root", fs);
    final JMemInode subDirectory = inode.createDirectory(new JMemPath(fs, "subdirectory"), fs);
  }

  @Test
  public void shouldCreateFile() throws FileAlreadyExistsException {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.getTheFileSystem();
    final JMemDirectoryInode inode = new JMemDirectoryInode(null, "root", fs);
    final JMemInode fileNode = inode.createFile(new JMemPath(fs, "file.txt"), fs);
  }

  @Test
  public void shouldGetChild() throws IOException {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.getTheFileSystem();
    final JMemDirectoryInode inode = new JMemDirectoryInode(null, "root", fs);
    final JMemInode subDirectory = inode.createDirectory(new JMemPath(fs, "subdirectory"), fs);
    assertEquals(subDirectory, inode.getInodeFor("subdirectory"));
  }

  @Test
  public void shouldGetGrandChild() throws IOException {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.getTheFileSystem();
    final JMemDirectoryInode inode = new JMemDirectoryInode(null, "root", fs);
    final JMemInode child = inode.createDirectory(new JMemPath(fs, "dir1"), fs);
    final JMemInode grandChild = child.createDirectory(new JMemPath(fs, "dir2"), fs);
    assertEquals(child, inode.getInodeFor("dir1"));
    assertEquals(grandChild, child.getInodeFor(new JMemPath(fs, "dir2")));
  }

  @Test
  public void shouldGetGrandChildFromRoot() throws IOException {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.getTheFileSystem();
    final JMemDirectoryInode inode = new JMemDirectoryInode(null, "root", fs);
    final JMemInode child = inode.createDirectory(new JMemPath(fs, "dir1"), fs);
    final JMemInode grandChild = child.createDirectory(new JMemPath(fs, "dir2"), fs);
    assertEquals(grandChild, inode.getInodeFor(new JMemPath(null, "/dir1/dir2/")));
    assertEquals(grandChild, inode.getInodeFor(new JMemPath(null, "/dir1/dir2")));
  }

  @Test
  public void shouldGetParent() throws IOException {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.getTheFileSystem();
    final JMemDirectoryInode inode = new JMemDirectoryInode(null, "root", fs);
    final JMemInode subDirectory = inode.createDirectory(new JMemPath(fs, "subdirectory"), fs);
    assertEquals(inode, subDirectory.getParent());
  }

  @Test(expected = IllegalStateException.class)
  public void shouldNotAllowCreateChannel() {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.getTheFileSystem();
    final JMemDirectoryInode inode = new JMemDirectoryInode(null, "root", fs);
    inode.createChannel();
  }

  @Test
  public void shouldSetName() {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.getTheFileSystem();
    final JMemDirectoryInode inode = new JMemDirectoryInode(null, "root", fs);
    assertEquals("root", inode.getName());
  }

  @Test(expected = FileAlreadyExistsException.class)
  public void shouldThrowIfFileExists() throws FileAlreadyExistsException {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.getTheFileSystem();
    final JMemDirectoryInode inode = new JMemDirectoryInode(null, "root", fs);
    final JMemInode fileNode = inode.createFile(new JMemPath(fs, "file.txt"), fs);
    inode.createFile(new JMemPath(fs, "file.txt"), fs);
  }
}
