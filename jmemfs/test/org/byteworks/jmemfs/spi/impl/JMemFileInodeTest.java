package org.byteworks.jmemfs.spi.impl;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileAlreadyExistsException;

import org.byteworks.jmemfs.spi.JMemFileSystem;
import org.byteworks.jmemfs.spi.JMemFileSystemProvider;
import org.byteworks.jmemfs.spi.JMemPath;
import org.junit.Test;

public class JMemFileInodeTest {
  private static byte[] BYTES = { 0, 1, 2, 3, 4, 5 };

  @Test
  public void shouldCreateChannel() {
    final JMemFileInode inode = new JMemFileInode(null, "input.txt");
    final SeekableByteChannel channel = inode.createChannel();
    assertNotNull(channel);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldNotAllowCreateDirectory() throws IOException {
    final JMemFileInode inode = new JMemFileInode(null, "input.txt");
    final JMemFileSystem fs = new JMemFileSystem(new JMemFileSystemProvider());
    inode.createDirectory(new JMemPath(fs, "illegal"));
  }

  @Test(expected = IllegalStateException.class)
  public void shouldNotAllowCreateFile() throws FileAlreadyExistsException {
    final JMemFileInode inode = new JMemFileInode(null, "input.txt");
    final JMemFileSystem fs = new JMemFileSystem(new JMemFileSystemProvider());
    inode.createFile(new JMemPath(fs, "illegal"));
  }

  @Test
  public void shouldUpdateAttributesOnWrite() {
    final long now = System.currentTimeMillis();
    final long later = now + 1;
    final JMemFileInode inode = new JMemFileInode(null, "input.txt", now) {
      @Override
      long currentTime() {
        return later;
      }
    };
    final ByteBuffer src = ByteBuffer.wrap(BYTES);
    inode.writeBytes(0, src);
    assertEquals(later, inode.getAttributes().lastAccessTime().toMillis());
    assertEquals(later, inode.getAttributes().lastModifiedTime().toMillis());
    assertEquals(BYTES.length, inode.getAttributes().size());
  }

  @Test
  public void shouldWrite() {
    final JMemFileInode inode = new JMemFileInode(null, "input.txt");
    final ByteBuffer src = ByteBuffer.wrap(BYTES);
    assertEquals(BYTES.length, inode.writeBytes(0, src));
  }
}
