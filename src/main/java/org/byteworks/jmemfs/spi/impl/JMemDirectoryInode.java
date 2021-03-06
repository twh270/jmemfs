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
import java.nio.channels.SeekableByteChannel;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.byteworks.jmemfs.spi.JMemFileSystem;

public class JMemDirectoryInode extends JMemInode {
  private final Map<String, JMemInode> entries = new HashMap<String, JMemInode>();

  public JMemDirectoryInode(final JMemDirectoryInode parent, final String name, final JMemFileSystem fileSystem) {
    super(parent, name, JMemFileAttributes.FileType.DIRECTORY, fileSystem);
  }

  @Override
  public void copyTo(final JMemInode target, final boolean replace, final boolean copyAttr) {
    updateATime();
    updateMTime();
    if (copyAttr) {
      target.updateAttributes(getAttributes());
    }
  }

  @Override
  public SeekableByteChannel createChannel() {
    throw new IllegalStateException("A directory inode cannot create a channel");
  }

  @Override
  public JMemInode createDirectory(final Path name, final JMemFileSystem fileSystem) throws IOException {
    updateATime();
    if (entries.containsKey(name.getFileName().toString()))
      throw new FileAlreadyExistsException(name.toString());
    final JMemInode node = new JMemDirectoryInode(this, name.getFileName().toString(), fileSystem);
    updateMTime();
    entries.put(name.getFileName().toString(), node);
    return node;
  }

  @Override
  public JMemInode createFile(final Path name, final JMemFileSystem fileSystem) throws FileAlreadyExistsException {
    updateATime();
    synchronized (entries) {
      if (getInodeFor(name.getFileName()) != null)
        throw new FileAlreadyExistsException(name.toString());
      final JMemInode fileInode = new JMemFileInode(this, name.getFileName().toString(), fileSystem);
      entries.put(name.getFileName().toString(), fileInode);
      updateMTime();
      return fileInode;
    }
  }

  @Override
  public JMemInode getInodeFor(final Path path) {
    if (path == null)
      return this;
    JMemInode curr = this;
    if (path.getNameCount() == 1)
      return getInodeFor(path.getFileName().toString());
    for (int i = 0; i < path.getNameCount() && curr != null; i++) {
      curr = curr.getInodeFor(path.getName(i));
    }
    return curr;
  }

  public JMemInode getInodeFor(final String name) {
    updateATime();
    if (".".equals(name))
      return this;
    else if ("..".equals(name))
      return getParent();
    else
      return entries.get(name);
  }

  @Override
  public void unlink() throws IOException {
    if (entries.size() > 0)
      throw new DirectoryNotEmptyException("Directory cannot be deleted if it is not empty");
    getParent().unlink(this.getName());
  }

  void unlink(final String name) {
    entries.remove(name);
  }

}
