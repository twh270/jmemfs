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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.byteworks.jmemfs.spi.JMemFileSystem;
import org.byteworks.jmemfs.spi.JMemFileSystemProvider;
import org.junit.Test;

public class JMemFileAttributesTest {
  @Test
  public void shouldGetCorrectAttributesForDirectory() {
    final long now = System.currentTimeMillis();
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
    final JMemDirectoryInode root = new JMemDirectoryInode(null, "root", fs);
    final JMemFileAttributes attributes = root.getAttributes();
    assertEquals(now, attributes.creationTime().toMillis());
    assertEquals(now, attributes.lastAccessTime().toMillis());
    assertEquals(now, attributes.lastModifiedTime().toMillis());
    assertEquals(0, attributes.size());
    assertTrue(attributes.isDirectory());
  }

  @Test
  public void shouldGetCorrectAttributesForFile() {
    final long now = System.currentTimeMillis();
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
    final JMemFileInode root = new JMemFileInode(null, "root", fs);
    final JMemFileAttributes attributes = root.getAttributes();
    assertEquals(now, attributes.creationTime().toMillis());
    assertEquals(now, attributes.lastAccessTime().toMillis());
    assertEquals(now, attributes.lastModifiedTime().toMillis());
    assertEquals(0, attributes.size());
    assertTrue(attributes.isRegularFile());
  }
}
