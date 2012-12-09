package org.byteworks.jmemfs.spi;

import static org.byteworks.jmemfs.spi.TestCommon.BAD_URI;
import static org.byteworks.jmemfs.spi.TestCommon.JMEM_ROOT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class JMemFileSystemTest {

  @Test
  public void shouldGetFileSystemProviderFromFS() {
    final FileSystem fs = getJMemFS();
    assertTrue(fs.provider() instanceof JMemFileSystemProvider);
  }

  @Test
  public void shouldGetJMemFileSystemFromURI() {
    final FileSystem fs = getJMemFS();
    assertTrue(fs != null);
    assertTrue(fs instanceof JMemFileSystem);
  }

  @Test
  public void shouldGetPath() throws URISyntaxException {
    final Path path = Paths.get(JMEM_ROOT);
    assertTrue(path != null);
  }

  @Test
  public void shouldGetSeparator() {
    assertEquals("/", getJMemFS().getSeparator());
  }

  @Test(expected = FileSystemNotFoundException.class)
  public void shouldNotGetPathWithWrongScheme() throws URISyntaxException {
    final Path path = Paths.get(BAD_URI);
    fail("Should have thrown exception here");
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIfURIPathNotRoot() throws URISyntaxException {
    FileSystems.getFileSystem(new URI("jmemfs:/not_root"));
  }

  private FileSystem getJMemFS() {
    FileSystem fs = null;
    fs = FileSystems.getFileSystem(JMEM_ROOT);
    return fs;
  }

}
