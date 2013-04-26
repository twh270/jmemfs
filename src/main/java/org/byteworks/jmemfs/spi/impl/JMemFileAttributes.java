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

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public class JMemFileAttributes implements BasicFileAttributes {
  public enum FileType {
    FILE, DIRECTORY, LINK, OTHER
  };

  FileTime modified;
  FileTime accessed;
  FileTime created;
  long size;
  FileType type;

  public JMemFileAttributes(final FileType fileType, final long now) {
    this.modified = this.accessed = this.created = FileTime.fromMillis(now);
    this.type = fileType;
    this.size = 0;
  }

  @Override
  public FileTime creationTime() {
    return created;
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof JMemFileAttributes))
      return false;
    final JMemFileAttributes o = (JMemFileAttributes) obj;
    return (modified.equals(o.modified) && accessed.equals(o.accessed) && created.equals(o.created) && size == o.size && type == o.type);
  }

  @Override
  public Object fileKey() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isDirectory() {
    return type == FileType.DIRECTORY;
  }

  @Override
  public boolean isOther() {
    return type == FileType.OTHER;
  }

  @Override
  public boolean isRegularFile() {
    return type == FileType.FILE;
  }

  @Override
  public boolean isSymbolicLink() {
    return type == FileType.LINK;
  }

  @Override
  public FileTime lastAccessTime() {
    return accessed;
  }

  @Override
  public FileTime lastModifiedTime() {
    return modified;
  }

  @Override
  public long size() {
    return size;
  }

  public void updateATime(final long time) {
    this.accessed = FileTime.fromMillis(time);
  }

  public void updateMTime(final long time) {
    this.modified = FileTime.fromMillis(time);
  }

  public void updateSize(final long newSize) {
    this.size = newSize;
  }

  void updateCTime(final long time) {
    this.created = FileTime.fromMillis(time);
  }

}
