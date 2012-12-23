package org.byteworks.jmemfs;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.byteworks.jmemfs.spi.JMemFileSystem;
import org.byteworks.jmemfs.spi.JMemFileSystemProvider;
import org.byteworks.jmemfs.spi.JMemPath;
import org.junit.Before;
import org.junit.Test;

public class FilesOperationsTest {
  private static final String TEST_STRING = "This is a string";

  private JMemFileSystemProvider provider;
  private JMemFileSystem fs;

  @Before
  public void setUp() {
    provider = new JMemFileSystemProvider();
    fs = provider.getTheFileSystem();
  }

  @Test
  public void shouldAppendExistingFileWithWrite() throws IOException {
    final byte[] outputBytes = TEST_STRING.getBytes();
    Files.write(new JMemPath(fs, "/output.txt"), outputBytes);
    Files.write(new JMemPath(fs, "/output.txt"), outputBytes, APPEND);
    final byte[] readBytes = Files.readAllBytes(new JMemPath(fs, "/output.txt"));
    assertEquals("This is a stringThis is a string", new String(readBytes));
  }

  @Test
  public void shouldCopyFromPathToOutputStream() throws IOException {
    final byte[] outputBytes = TEST_STRING.getBytes();
    final Path path = new JMemPath(fs, "/output.txt");
    Files.write(path, outputBytes);
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    Files.copy(path, bos);
    bos.close();
    assertEquals(outputBytes.length, bos.toByteArray().length);
  }

  @Test
  public void shouldCreateDirectories() throws IOException {
    final Path path = new JMemPath(fs, "/temp/working/path");
    Files.createDirectories(path);
  }

  @Test
  public void shouldCreateDirectory() throws IOException {
    Path path = new JMemPath(fs, "/temp");
    Files.createDirectory(path);
    path = new JMemPath(fs, "/temp/working");
    Files.createDirectory(path);
  }

  @Test
  public void shouldCreateFile() throws IOException {
    final Path path = new JMemPath(fs, "/input.txt");
    Files.createFile(path);
  }

  @Test(expected = IOException.class)
  public void shouldNotAppendNewFileWithWrite() throws IOException {
    final byte[] outputBytes = TEST_STRING.getBytes();
    Files.write(new JMemPath(fs, "/output.txt"), outputBytes, APPEND);
    final byte[] readBytes = Files.readAllBytes(new JMemPath(fs, "/output.txt"));
    assertArrayEquals(outputBytes, readBytes);
  }

  @Test(expected = IOException.class)
  public void shouldNotReadNonexistentFile() throws IOException {
    /*final byte[] inputBytes = */Files.readAllBytes(new JMemPath(fs, "/output.txt"));
  }

  @Test
  public void shouldReadFile() throws IOException {
    final byte[] outputBytes = TEST_STRING.getBytes();
    Files.write(new JMemPath(fs, "/output.txt"), outputBytes);
    final byte[] inputBytes = Files.readAllBytes(new JMemPath(fs, "/output.txt"));
    assertArrayEquals(outputBytes, inputBytes);
  }

  @Test
  public void shouldTruncateExistingFileWithWrite() throws IOException {
    final byte[] outputBytes = TEST_STRING.getBytes();
    Files.write(new JMemPath(fs, "/output.txt"), outputBytes);
    Files.write(new JMemPath(fs, "/output.txt"), outputBytes, TRUNCATE_EXISTING);
    final byte[] readBytes = Files.readAllBytes(new JMemPath(fs, "/output.txt"));
    assertArrayEquals(outputBytes, readBytes);
  }

  @Test
  public void shouldTruncateNewFileWithWrite() throws IOException {
    final byte[] outputBytes = TEST_STRING.getBytes();
    Files.write(new JMemPath(fs, "/output.txt"), outputBytes, TRUNCATE_EXISTING);
    final byte[] inputBytes = Files.readAllBytes(new JMemPath(fs, "/output.txt"));
    assertArrayEquals(outputBytes, inputBytes);
  }

  @Test
  public void shouldWriteFile() throws IOException {
    final byte[] outputBytes = TEST_STRING.getBytes();
    Files.write(new JMemPath(fs, "/output.txt"), outputBytes);
  }
}
