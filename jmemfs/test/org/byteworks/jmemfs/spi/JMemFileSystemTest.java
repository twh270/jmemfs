package org.byteworks.jmemfs.spi;

import static org.byteworks.jmemfs.spi.TestCommon.BAD_URI;
import static org.byteworks.jmemfs.spi.TestCommon.JMEM_ROOT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class JMemFileSystemTest {

  @Test
  public void shouldCreatePathFromSegments() {
    final FileSystem fs = new JMemFileSystem(new JMemFileSystemProvider());
    Path path = fs.getPath("/first", "second", "third");
    assertEquals("/first/second/third", path.toString());

    path = fs.getPath("/some//", "/weird", "segments", "/here");
    assertEquals("/some/weird/segments/here", path.toString());
  }

  @Test
  public void shouldGetFileSystemProviderFromFS() {
    final FileSystem fs = new JMemFileSystem(new JMemFileSystemProvider());
    assertTrue(fs.provider() instanceof JMemFileSystemProvider);
  }

  @Test
  public void shouldGetJMemFileSystemFromURI() {
    final FileSystem fs = new JMemFileSystem(new JMemFileSystemProvider());
    assertTrue(fs != null);
    assertTrue(fs instanceof JMemFileSystem);
  }

  @Test
  public void shouldGetRootPath() {
    final Path path = Paths.get(JMEM_ROOT);
    assertEquals("/", path.toString());
    assertNotNull(path);
  }

  @Test
  public void shouldGetSeparator() {
    assertEquals("/", new JMemFileSystem(new JMemFileSystemProvider()).getSeparator());
  }

  @Test(expected = FileSystemNotFoundException.class)
  public void shouldNotGetPathWithWrongScheme() {
    Paths.get(BAD_URI);
    fail("Should have thrown exception here");
  }

  @Test
  public void shouldSetDefaultDir() {
    final Map<String, String> env = new HashMap<String, String>();
    env.put("default.dir", "/root");
    final JMemFileSystem fs = new JMemFileSystem(new JMemFileSystemProvider(), env);
    assertEquals("/root", fs.getEnvironment().get("default.dir"));
    assertEquals("/root", fs.defaultDir());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIfURIPathNotRoot() throws URISyntaxException {
    FileSystems.getFileSystem(new URI("jmemfs:/not_root"));
  }

}
