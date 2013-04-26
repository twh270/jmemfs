//   Copyright 2013 Thomas Wheeler 
// 
//   Licensed under the Apache License, Version 2.0 (the "License"); 
//   you may not use this file except in compliance with the License. 
//   You may obtain a copy of the License at 
// 
//     http://www.apache.org/licenses/LICENSE-2.0 
// 
//   Unless required by applicable law or agreed to in writing, software 
//   distributed under the License is distributed on an "AS IS" BASIS, 
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
//   See the License for the specific language governing permissions and 
//   limitations under the License. 
//
//   Copyright 2013 Thomas Wheeler 
// 
//   Licensed under the Apache License, Version 2.0 (the "License"); 
//   you may not use this file except in compliance with the License. 
//   You may obtain a copy of the License at 
// 
//     http://www.apache.org/licenses/LICENSE-2.0 
// 
//   Unless required by applicable law or agreed to in writing, software 
//   distributed under the License is distributed on an "AS IS" BASIS, 
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
//   See the License for the specific language governing permissions and 
//   limitations under the License. 
//
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
    fileInode.truncate((int) size);
    return this;
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
