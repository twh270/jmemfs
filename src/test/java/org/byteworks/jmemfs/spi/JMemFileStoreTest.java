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
package org.byteworks.jmemfs.spi;

import static org.byteworks.jmemfs.spi.TestCommon.JMEM_ROOT;

import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;

import junit.framework.Assert;

import org.junit.Test;

public class JMemFileStoreTest {

  @Test
  public void shouldHaveNoName() throws IOException {
    final FileSystemProvider p = (new JMemFileSystemProvider());
    Assert.assertEquals("", p.getFileStore(Paths.get(JMEM_ROOT)).name());
  }

  @Test
  public void shouldHaveType() throws IOException {
    final FileSystemProvider p = (new JMemFileSystemProvider());
    Assert.assertEquals("jmemfs", p.getFileStore(Paths.get(JMEM_ROOT)).type());
  }
}
