package org.byteworks.jmemfs.spi.impl;

import static junit.framework.Assert.assertNotNull;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileAlreadyExistsException;

import org.junit.Test;

public class JMemFileInodeTest {

  @Test
  public void shouldCreateChannel() {
    final JMemFileInode inode = new JMemFileInode(null, "input.txt");
    final SeekableByteChannel channel = inode.createChannel();
    assertNotNull(channel);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldNotAllowCreateDirectory() throws IOException {
    final JMemFileInode inode = new JMemFileInode(null, "input.txt");
    inode.createDirectory("illegal");
  }

  @Test(expected = IllegalStateException.class)
  public void shouldNotAllowCreateFile() throws FileAlreadyExistsException {
    final JMemFileInode inode = new JMemFileInode(null, "input.txt");
    inode.createFile("illegal");
  }
}
