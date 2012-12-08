package org.byteworks.jmemfs.spi;

import static org.byteworks.jmemfs.spi.TestCommon.JMEM_ROOT;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class JMemFileSystemProviderTest {

  @Test
  public void providerShouldBeInstalled() {
    Assert
        .assertTrue(
            "JMemFileSystemProvider is not installed, check META-INF/services for existence and correct definition of java.nio.file.spi.FileSystemProvider file",
            getProvider() != null);
  }

  @Test
  public void shouldGetJMemFileSystemWithEnvironment() throws IOException {
    final Map<String, String> env = new HashMap<String, String>();
    env.put("key", "value");
    final FileSystem fs = FileSystems.newFileSystem(JMEM_ROOT, env);
    final JMemFileSystem jfs = (JMemFileSystem) fs;
    assertEquals("value", jfs.getEnvironment().get("key"));
  }

  @Test
  public void testCheckAccess() throws IOException {
    final JMemFileSystemProvider p = getProvider();
    p.checkAccess(Paths.get(JMEM_ROOT), null);
  }

  private JMemFileSystemProvider getProvider() {
    final List<FileSystemProvider> providers = FileSystemProvider.installedProviders();
    for (final FileSystemProvider p : providers) {
      if (p.getScheme() == JMemFileSystemProvider.SCHEME)
        return (JMemFileSystemProvider) p;
    }
    return null;
  }
}
