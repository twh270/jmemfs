package org.byteworks.jmemfs.spi;

import static org.byteworks.jmemfs.spi.TestCommon.JMEM_ROOT;
import static org.byteworks.jmemfs.spi.TestCommon.JMEM_URI;
import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class JMemPathTest {
  @Test
  public void shouldCreateUriFromName() {
    final Path path = Paths.get(JMEM_URI("/o/aa"));
    final URI uriForm = path.toUri();
    assertEquals("jmemfs:/o/aa", uriForm.toString());
  }

  @Test
  public void shouldGetFileSystemFromPath() {
    final Path path = Paths.get(JMEM_ROOT);
    final FileSystem fs = path.getFileSystem();
    assertEquals(FileSystems.getFileSystem(JMEM_ROOT), fs);
  }

  @Test
  public void shouldGetNameCount() {
    Path path = Paths.get(JMEM_ROOT);
    assertEquals(1, path.getNameCount());
    path = Paths.get(JMEM_URI("/two/path"));
    assertEquals(2, path.getNameCount());
  }

  @Test
  public void shouldGetNameIndexes() {
    final Path path = Paths.get(JMEM_URI("/o/aa/bbb/defgggg"));
    assertEquals("o", path.getName(0).toString());
    assertEquals("aa", path.getName(1).toString());
    assertEquals("bbb", path.getName(2).toString());
    assertEquals("defgggg", path.getName(3).toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowOnInvalidNameIndex() {
    final Path path = Paths.get(JMEM_URI("/some/name"));
    final Path broken = path.getName(2);
  }
}
