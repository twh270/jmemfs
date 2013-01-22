package org.byteworks.jmemfs;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Map;

import org.byteworks.jmemfs.spi.JMemFileSystem;
import org.byteworks.jmemfs.spi.JMemFileSystemProvider;
import org.byteworks.jmemfs.spi.JMemPath;
import org.byteworks.jmemfs.spi.impl.JMemTimeProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FilesOperationsTest {
  private static final String TEST_STRING = "This is a string";
  private static final String TEST_STRING2 = "This is also a string";
  private static final byte[] BYTES = TEST_STRING.getBytes();
  private static final byte[] BYTES2 = TEST_STRING2.getBytes();

  private JMemFileSystemProvider provider;
  private JMemFileSystem fs;

  @Before
  public void setUp() {
    provider = new JMemFileSystemProvider();
    fs = provider.getTheFileSystem();
  }

  @Test
  public void shouldAppendExistingFileWithWrite() throws IOException {
    Files.write(new JMemPath(fs, "/output.txt"), BYTES);
    Files.write(new JMemPath(fs, "/output.txt"), BYTES, APPEND);
    final byte[] readBytes = Files.readAllBytes(new JMemPath(fs, "/output.txt"));
    assertEquals("This is a stringThis is a string", new String(readBytes));
  }

  @Test
  public void shouldCopyFromInputStreamToPath() throws IOException {
    final InputStream is = new ByteArrayInputStream(BYTES);
    final Path path = new JMemPath(fs, "/output.txt");
    Files.copy(is, path);
    final byte[] readBytes = Files.readAllBytes(path);
    assertArrayEquals(BYTES, readBytes);
  }

  @Test
  public void shouldCopyFromPathToOutputStream() throws IOException {
    final Path path = new JMemPath(fs, "/output.txt");
    Files.write(path, BYTES);
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    Files.copy(path, bos);
    bos.close();
    assertEquals(BYTES.length, bos.toByteArray().length);
  }

  @Test
  public void shouldCopyFromSourceToTargetPath() throws IOException {
    final Path source = new JMemPath(fs, "/source.txt");
    final Path target = new JMemPath(fs, "/target.txt");
    Files.write(source, BYTES);
    Files.copy(source, target);
    final byte[] readBytes = Files.readAllBytes(target);
    assertArrayEquals(BYTES, readBytes);
    Files.copy(new ByteArrayInputStream(BYTES2), source, REPLACE_EXISTING);
    Files.copy(source, target, REPLACE_EXISTING);
  }

  @Test
  public void shouldCopyWithReplaceInputStreamToPath() throws IOException {
    InputStream is = new ByteArrayInputStream(BYTES);
    final Path path = new JMemPath(fs, "/output.txt");
    Files.copy(is, path);
    is = new ByteArrayInputStream(BYTES2);
    Files.copy(is, path, REPLACE_EXISTING);
    final byte[] readBytes = Files.readAllBytes(path);
    assertArrayEquals(BYTES2, readBytes);
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

  @Test
  public void shouldCreateTempDirectory() throws IOException {
    final Path dir = Files.createTempDirectory(new JMemPath(fs, "/"), "temp");
    assertTrue(Files.exists(dir));
  }

  @Test
  public void shouldCreateTempFile() throws IOException {
    final Path file = Files.createTempFile(new JMemPath(fs, "/"), "temp", ".txt");
    assertTrue(Files.exists(file));
  }

  @Test
  public void shouldDelete() throws IOException {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.getTheFileSystem();
    Files.createDirectory(new JMemPath(fs, "/dir1"));
    Files.createDirectory(new JMemPath(fs, "/dir1/dir2"));
    Files.write(new JMemPath(fs, "/dir1/dir2/file.txt"), BYTES);
    Files.delete(new JMemPath(fs, "/dir1/dir2/file.txt"));
    Files.delete(new JMemPath(fs, "/dir1/dir2"));
    Files.delete(new JMemPath(fs, "/dir1"));
    try {
      p.checkAccess(new JMemPath(fs, "/dir1"));
      Assert.fail("Expected an exception");
    }
    catch (final IOException e) {
      // expected
    }
  }

  /*
   * 
  *  <tr>
  *     <td> "lastModifiedTime" </td>
  *     <td> {@link FileTime} </td>
  *   </tr>
  *   <tr>
  *     <td> "lastAccessTime" </td>
  *     <td> {@link FileTime} </td>
  *   </tr>
  *   <tr>
  *     <td> "creationTime" </td>
  *     <td> {@link FileTime} </td>
  *   </tr>
  *   <tr>
  *     <td> "size" </td>
  *     <td> {@link Long} </td>
  *   </tr>
  *   <tr>
  *     <td> "isRegularFile" </td>
  *     <td> {@link Boolean} </td>
  *   </tr>
  *   <tr>
  *     <td> "isDirectory" </td>
  *     <td> {@link Boolean} </td>
  *   </tr>
  *   <tr>
  *     <td> "isSymbolicLink" </td>
  *     <td> {@link Boolean} </td>
  *   </tr>
  *   <tr>
  *     <td> "isOther" </td>
  *     <td> {@link Boolean} </td>
  *   </tr>
  *   <tr>
  *     <td> "fileKey" </td>
  *     <td> {@link Object} </td>
  *   </tr>
  * </table>
   */
  @Test
  public void shouldGetAttributes() throws IOException {
    final long now = 10000000L;
    final JMemTimeProvider timeProvider = new JMemTimeProvider() {
      @Override
      public long currentTimeMillis() {
        return now;
      }
    };
    final Map<String, Object> env = new HashMap<String, Object>();
    env.put("timeProvider", timeProvider);
    final JMemFileSystemProvider p = new JMemFileSystemProvider(env);
    final JMemFileSystem fs = p.getTheFileSystem();
    final JMemPath path = new JMemPath(fs, "/output.txt");
    Files.write(path, BYTES);
    final FileTime t = (FileTime) Files.getAttribute(path, "lastModifiedTime");
    assertEquals(now, t.toMillis());
  }

  @Test(expected = IOException.class)
  public void shouldNotAppendNewFileWithWrite() throws IOException {
    Files.write(new JMemPath(fs, "/output.txt"), BYTES, APPEND);
    final byte[] readBytes = Files.readAllBytes(new JMemPath(fs, "/output.txt"));
    assertArrayEquals(BYTES, readBytes);
  }

  @Test(expected = IOException.class)
  public void shouldNotReadNonexistentFile() throws IOException {
    /*final byte[] inputBytes = */Files.readAllBytes(new JMemPath(fs, "/output.txt"));
  }

  @Test
  public void shouldReadFile() throws IOException {
    Files.write(new JMemPath(fs, "/output.txt"), BYTES);
    final byte[] inputBytes = Files.readAllBytes(new JMemPath(fs, "/output.txt"));
    assertArrayEquals(BYTES, inputBytes);
  }

  @Test
  public void shouldTruncateExistingFileWithWrite() throws IOException {
    final byte[] BYTES = TEST_STRING.getBytes();
    Files.write(new JMemPath(fs, "/output.txt"), BYTES);
    Files.write(new JMemPath(fs, "/output.txt"), BYTES, TRUNCATE_EXISTING);
    final byte[] readBytes = Files.readAllBytes(new JMemPath(fs, "/output.txt"));
    assertArrayEquals(BYTES, readBytes);
  }

  @Test
  public void shouldWriteFile() throws IOException {
    Files.write(new JMemPath(fs, "/output.txt"), BYTES);
  }
}
