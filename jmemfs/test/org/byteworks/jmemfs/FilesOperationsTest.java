package org.byteworks.jmemfs;

import static org.byteworks.jmemfs.spi.TestCommon.JMEM_URI;
import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class FilesOperationsTest {
  private static final String TEST_STRING = "This is a string";

  @Test
  public void shouldCopyFromPathToOutputStream() throws IOException {
    final Path path = Paths.get(JMEM_URI("/input.txt"));
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    Files.copy(path, bos);
  }

  @Test
  public void shouldCreateDirectory() throws IOException {
    Path path = Paths.get(JMEM_URI("/temp"));
    Files.createDirectory(path);
    path = Paths.get(JMEM_URI("/temp/working"));
    Files.createDirectory(path);
  }

  @Test
  public void shouldCreateFile() throws IOException {
    final Path path = Paths.get(JMEM_URI("/input.txt"));
    Files.createFile(path);
  }

  @Test
  public void shouldReadFile() throws IOException {
    final byte[] outputBytes = TEST_STRING.getBytes();
    Files.write(Paths.get(JMEM_URI("/output.txt")), outputBytes);
    final byte[] inputBytes = Files.readAllBytes(Paths.get(JMEM_URI("/output.txt")));
    assertArrayEquals(outputBytes, inputBytes);
  }

  @Test
  public void shouldWriteFile() throws IOException {
    final byte[] outputBytes = TEST_STRING.getBytes();
    Files.write(Paths.get(JMEM_URI("/output.txt")), outputBytes);
  }
}
