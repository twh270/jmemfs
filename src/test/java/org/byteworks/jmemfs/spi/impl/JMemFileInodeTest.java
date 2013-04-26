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

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashMap;
import java.util.Map;

import org.byteworks.jmemfs.spi.JMemFileSystem;
import org.byteworks.jmemfs.spi.JMemFileSystemProvider;
import org.byteworks.jmemfs.spi.JMemPath;
import org.junit.Test;

public class JMemFileInodeTest {
  private static byte[] BYTES = { 0, 1, 2, 3, 4, 5 };

  @Test
  public void shouldCreateChannel() {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.getTheFileSystem();
    final JMemFileInode inode = new JMemFileInode(null, "input.txt", fs);
    final SeekableByteChannel channel = inode.createChannel();
    assertNotNull(channel);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldNotAllowCreateDirectory() throws IOException {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.getTheFileSystem();
    final JMemFileInode inode = new JMemFileInode(null, "input.txt", fs);
    inode.createDirectory(new JMemPath(fs, "illegal"), fs);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldNotAllowCreateFile() throws FileAlreadyExistsException {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.getTheFileSystem();
    final JMemFileInode inode = new JMemFileInode(null, "input.txt", fs);
    inode.createFile(new JMemPath(fs, "illegal"), fs);
  }

  @Test
  public void shouldUpdateAttributesOnWrite() {
    final long now = System.currentTimeMillis();
    final long later = now + 1;
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
    final JMemFileInode inode = new JMemFileInode(null, "input.txt", fs) {
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
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.getTheFileSystem();
    final JMemFileInode inode = new JMemFileInode(null, "input.txt", fs);
    final ByteBuffer src = ByteBuffer.wrap(BYTES);
    assertEquals(BYTES.length, inode.writeBytes(0, src));
  }
}
