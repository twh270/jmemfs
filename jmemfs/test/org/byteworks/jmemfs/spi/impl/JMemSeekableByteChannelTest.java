package org.byteworks.jmemfs.spi.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.byteworks.jmemfs.spi.JMemFileSystem;
import org.byteworks.jmemfs.spi.JMemFileSystemProvider;
import org.junit.Test;

public class JMemSeekableByteChannelTest {
  private static final byte BYTES[] = { 1, 3, 5, 7, 9 };

  @Test
  public void shouldBeOpen() {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.getTheFileSystem();
    final JMemFileInode inode = new JMemFileInode(null, "file", fs);
    final JMemSeekableByteChannel channel = new JMemSeekableByteChannel(inode);
    assertTrue(channel.isOpen());
  }

  @Test
  public void shouldClose() throws IOException {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.getTheFileSystem();
    final JMemFileInode inode = new JMemFileInode(null, "file", fs);
    final JMemSeekableByteChannel channel = new JMemSeekableByteChannel(inode);
    channel.close();
    assertFalse(channel.isOpen());
  }

  @Test
  public void shouldSetPosition() throws IOException {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.getTheFileSystem();
    final JMemFileInode inode = new JMemFileInode(null, "file", fs);
    final JMemSeekableByteChannel channel = new JMemSeekableByteChannel(inode);
    assertEquals(0, channel.position());
    channel.position(2400);
    assertEquals(2400, channel.position());
  }

  @Test
  public void shouldWriteAndRead() throws IOException {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.getTheFileSystem();
    final JMemFileInode inode = new JMemFileInode(null, "file", fs);
    final JMemSeekableByteChannel channel = new JMemSeekableByteChannel(inode);
    channel.write(ByteBuffer.wrap(BYTES));
    assertEquals(5, channel.position());
    channel.position(0);
    final byte[] buf = new byte[BYTES.length];
    final ByteBuffer dest = ByteBuffer.wrap(buf);
    channel.read(dest);
    assertArrayEquals(BYTES, buf);
  }
}
