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
import static org.byteworks.jmemfs.spi.TestCommon.JMEM_URI;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.byteworks.jmemfs.spi.impl.JMemFileAttributes;
import org.junit.Test;

public class JMemFileSystemProviderTest {

  @Test
  public void pathShouldNotBeHidden() throws IOException {
    final FileSystemProvider p = new JMemFileSystemProvider();
    assertFalse(p.isHidden(Paths.get(JMEM_ROOT)));
  }

  @Test
  public void providerShouldBeInstalled() {
    assertNotNull(
        "JMemFileSystemProvider is not installed, check META-INF/services for existence and correct definition of java.nio.file.spi.FileSystemProvider file",
        getProvider());
  }

  @Test
  public void shouldAllowCheckAccessOnFile() throws IOException {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.theFileSystem;
    fs.createDirectory(new JMemPath(fs, "/path"), null);
    p.checkAccess(new JMemPath(fs, "/path"));
  }

  @Test
  public void shouldAllowCheckAccessToRoot() throws IOException {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.theFileSystem;
    p.checkAccess(new JMemPath(fs, "/"));
  }

  @Test
  public void shouldCreateDirectory() throws IOException {
    final FileSystemProvider p = new JMemFileSystemProvider();
    p.createDirectory(Paths.get(JMEM_URI("/temp")));
    p.createDirectory(Paths.get(JMEM_URI("/temp/working")));
  }

  @Test
  public void shouldGetAttributesForRoot() throws IOException {
    final FileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileAttributes attributes = p.readAttributes(Paths.get(JMEM_ROOT), JMemFileAttributes.class);
    assertNotNull(attributes);
  }

  @Test
  public void shouldGetFileStore() throws IOException {
    final FileSystemProvider p = new JMemFileSystemProvider();
    final FileStore fs = p.getFileStore(Paths.get(JMEM_ROOT));
    assertNotNull(fs);
  }

  @Test
  public void shouldGetJMemFileSystemWithEnvironment() throws IOException {
    final Map<String, String> env = new HashMap<String, String>();
    env.put("key", "value");
    final FileSystem fs = FileSystems.newFileSystem(JMEM_ROOT, env);
    final JMemFileSystem jfs = (JMemFileSystem) fs;
    assertEquals("value", jfs.getEnvironment().get("key"));
  }

  @Test(expected = NoSuchFileException.class)
  public void shouldThrowOnCheckAccessWithNonExistentFile() throws IOException {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.theFileSystem;
    p.checkAccess(new JMemPath(fs, "/does/not/exist"));
  }

  @Test
  public void testIsSameFile() throws IOException {
    final FileSystemProvider p = new JMemFileSystemProvider();
    Path path1 = Paths.get(JMEM_URI("/some/path"));
    final Path path2 = Paths.get(JMEM_URI("/some/path"));
    assertTrue(p.isSameFile(path1, path2));

    path1 = Paths.get(JMEM_URI("/another/path"));
    assertFalse(p.isSameFile(path1, path2));
  }

  private JMemFileSystemProvider getProvider() {
    final List<FileSystemProvider> providers = FileSystemProvider.installedProviders();
    for (final FileSystemProvider p : providers) {
      if (p.getScheme() == JMemConstants.SCHEME)
        return (JMemFileSystemProvider) p;
    }
    return null;
  }
}
