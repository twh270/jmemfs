package org.byteworks.jmemfs;

import static org.byteworks.jmemfs.spi.TestCommon.JMEM_URI;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class FilesOperationsTest {
  @Test
  public void shouldCopyFromPathToOutputStream() throws IOException {
    final Path path = Paths.get(JMEM_URI("/input.txt"));
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    Files.copy(path, bos);
  }

  @Test
  public void shouldCreateDirectory() throws IOException {
    final Path path = Paths.get(JMEM_URI("/temp"));
    Files.createDirectory(path);
  }

  @Test
  public void shouldCreateFile() throws IOException {
    final Path path = Paths.get(JMEM_URI("/input.txt"));
    Files.createFile(path);
  }
}
