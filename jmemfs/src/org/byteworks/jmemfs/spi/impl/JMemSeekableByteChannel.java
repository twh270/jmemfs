package org.byteworks.jmemfs.spi.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

public class JMemSeekableByteChannel implements SeekableByteChannel {

  @Override
  public void close() throws IOException {
  }

  @Override
  public boolean isOpen() {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public long position() throws IOException {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public SeekableByteChannel position(final long newPosition) throws IOException {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public int read(final ByteBuffer dst) throws IOException {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public long size() throws IOException {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public SeekableByteChannel truncate(final long size) throws IOException {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public int write(final ByteBuffer src) throws IOException {
    throw new UnsupportedOperationException("not implemented");
  }

}
