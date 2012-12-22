package org.byteworks.jmemfs.spi.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SeekableByteChannel;

public class JMemSeekableByteChannel implements SeekableByteChannel {
  private boolean open = true;
  private int position;
  private final JMemFileInode fileInode;

  public JMemSeekableByteChannel(final JMemFileInode fileInode) {
    this.fileInode = fileInode;
  }

  @Override
  public void close() throws IOException {
    open = false;
  }

  @Override
  public boolean isOpen() {
    return open;
  }

  @Override
  public long position() throws IOException {
    if (!open)
      throw new ClosedChannelException();
    return position;
  }

  @Override
  public SeekableByteChannel position(final long newPosition) throws IOException {
    if (!open)
      throw new ClosedChannelException();
    if (newPosition > Integer.MAX_VALUE)
      throw new IllegalArgumentException("Position cannot be greater than " + Integer.MAX_VALUE);
    position = (int) newPosition;
    return this;
  }

  @Override
  public int read(final ByteBuffer dst) throws IOException {
    if (!open)
      throw new ClosedChannelException();
    final int bytes = fileInode.readBytes(position, dst);
    position += bytes;
    return bytes;
  }

  @Override
  public long size() throws IOException {
    if (!open)
      throw new ClosedChannelException();
    return fileInode.getAttributes().size();
  }

  @Override
  public SeekableByteChannel truncate(final long size) throws IOException {
    if (!open)
      throw new ClosedChannelException();
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public int write(final ByteBuffer src) throws IOException {
    if (!open)
      throw new ClosedChannelException();
    final int bytes = fileInode.writeBytes(position, src);
    position += bytes;
    return bytes;
  }

}
