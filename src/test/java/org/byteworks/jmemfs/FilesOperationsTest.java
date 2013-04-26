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
package org.byteworks.jmemfs;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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

/*
 * DONE InputStream newInputStream(Path, OpenOption...) throws IOException;
 * DONE OutputStream newOutputStream(Path, OpenOption...) throws IOException; 
 * java.nio.channels.SeekableByteChannel newByteChannel(Path, Set<? extends OpenOption>, attribute.FileAttribute<?>...) throws IOException; 
 * java.nio.channels.SeekableByteChannel newByteChannel(Path, OpenOption...) throws IOException; 
 * DirectoryStream<Path> newDirectoryStream(Path) throws IOException; 
 * DirectoryStream<Path> newDirectoryStream(Path, String) throws IOException;
 * DirectoryStream<Path> newDirectoryStream(Path, DirectoryStream$Filter<? super Path>) throws IOException; 
 * Path createFile(Path, attribute.FileAttribute<?>...) throws IOException; 
 * Path createDirectory(Path, attribute.FileAttribute<?>...) throws IOException;
 * Path createDirectories(Path, attribute.FileAttribute<?>...) throws IOException; 
 * Path createTempFile(Path, String, String, attribute.FileAttribute<?>...) throws IOException; 
 * Path createTempFile(String, String, attribute.FileAttribute<?>...) throws IOException; 
 * Path createTempDirectory(Path, String, attribute.FileAttribute<?>...) throws IOException; 
 * Path createTempDirectory(String, attribute.FileAttribute<?>...) throws IOException; 
 * Path createSymbolicLink(Path, Path, attribute.FileAttribute<?>...) throws IOException; 
 * Path createLink(Path, Path) throws IOException; 
 * void delete(Path) throws IOException; 
 * boolean deleteIfExists(Path) throws IOException; 
 * Path copy(Path, Path, CopyOption...) throws IOException; 
 * Path move(Path, Path, CopyOption...) throws IOException; 
 * Path readSymbolicLink(Path) throws IOException; 
 * FileStore getFileStore(Path) throws IOException; 
 * boolean isSameFile(Path, Path) throws IOException; 
 * boolean isHidden(Path) throws IOException; String probeContentType(Path) throws IOException; 
 * <V extends attribute.FileAttributeView> V getFileAttributeView(Path, Class<V>, LinkOption...); 
 * <A extends attribute.BasicFileAttributes> A readAttributes(Path, Class<A>, LinkOption...) throws IOException; 
 * Path setAttribute(Path, String, Object, LinkOption...) throws IOException;
 * Object getAttribute(Path, String, LinkOption...) throws IOException;
 * Map<String, Object> readAttributes(Path, String, LinkOption...) throws IOException; 
 * Set<attribute.PosixFilePermission> getPosixFilePermissions(Path, LinkOption...) throws IOException; 
 * Path setPosixFilePermissions(Path, Set<attribute.PosixFilePermission>) throws IOException; 
 * attribute.UserPrincipal getOwner(Path, LinkOption...) throws IOException; 
 * Path setOwner(Path, attribute.UserPrincipal) throws IOException; 
 * boolean isSymbolicLink(Path); boolean isDirectory(Path, LinkOption...); 
 * boolean isRegularFile(Path, LinkOption...);
 * attribute.FileTime getLastModifiedTime(Path, LinkOption...) throws IOException; 
 * Path setLastModifiedTime(Path, attribute.FileTime) throws IOException; 
 * long size(Path) throws IOException; 
 * boolean exists(Path, LinkOption...); 
 * boolean notExists(Path, LinkOption...); 
 * boolean isReadable(Path); 
 * boolean isWritable(Path); 
 * boolean isExecutable(Path);
 * Path walkFileTree(Path, Set<FileVisitOption>, int, FileVisitor<? super Path>) throws IOException; 
 * Path walkFileTree(Path, FileVisitor<? super Path>) throws IOException; 
 * BufferedReader newBufferedReader(Path, java.nio.charset.Charset) throws IOException; 
 * BufferedWriter newBufferedWriter(Path, java.nio.charset.Charset, OpenOption...) throws IOException; 
 * long copy(InputStream, Path, CopyOption...) throws IOException; 
 * long copy(Path, OutputStream) throws IOException; 
 * byte[] readAllBytes(Path) throws IOException; 
 * List<String> readAllLines(Path, java.nio.charset.Charset) throws IOException; 
 * Path write(Path, byte[], OpenOption...) throws IOException; 
 * Path write(Path, Iterable<? extends CharSequence>, java.nio.charset.Charset, OpenOption...) throws IOException;
 */

public class FilesOperationsTest {
  private static final String TEST_STRING = "This is a string";
  private static final String TEST_STRING2 = "This is also a string";
  private static final byte[] BYTES = TEST_STRING.getBytes();
  private static final byte[] BYTES2 = TEST_STRING2.getBytes();

  private JMemFileSystemProvider provider;
  private JMemFileSystem fileSystem;

  @Before
  public void setUp() {
    provider = new JMemFileSystemProvider();
    fileSystem = provider.getTheFileSystem();
  }

  @Test
  public void shouldAppendExistingFileWithWrite() throws IOException {
    Files.write(new JMemPath(fileSystem, "/output.txt"), BYTES);
    Files.write(new JMemPath(fileSystem, "/output.txt"), BYTES, APPEND);
    final byte[] readBytes = Files.readAllBytes(new JMemPath(fileSystem, "/output.txt"));
    assertEquals("This is a stringThis is a string", new String(readBytes));
  }

  @Test
  public void shouldCopyFromInputStreamToPath() throws IOException {
    final InputStream is = new ByteArrayInputStream(BYTES);
    final Path path = new JMemPath(fileSystem, "/output.txt");
    Files.copy(is, path);
    final byte[] readBytes = Files.readAllBytes(path);
    assertArrayEquals(BYTES, readBytes);
  }

  @Test
  public void shouldCopyFromPathToOutputStream() throws IOException {
    final Path path = new JMemPath(fileSystem, "/output.txt");
    Files.write(path, BYTES);
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    Files.copy(path, bos);
    bos.close();
    assertEquals(BYTES.length, bos.toByteArray().length);
  }

  @Test
  public void shouldCopyFromSourceToTargetPath() throws IOException {
    final Path source = new JMemPath(fileSystem, "/source.txt");
    final Path target = new JMemPath(fileSystem, "/target.txt");
    Files.write(source, BYTES);
    Files.copy(source, target);
    byte[] readBytes = Files.readAllBytes(target);
    assertArrayEquals(BYTES, readBytes);
    Files.copy(new ByteArrayInputStream(BYTES2), source, REPLACE_EXISTING);
    Files.copy(source, target, REPLACE_EXISTING);
    readBytes = Files.readAllBytes(target);
    assertArrayEquals(BYTES2, readBytes);
  }

  @Test
  public void shouldCopyWithReplaceInputStreamToPath() throws IOException {
    InputStream is = new ByteArrayInputStream(BYTES);
    final Path path = new JMemPath(fileSystem, "/output.txt");
    Files.copy(is, path);
    is = new ByteArrayInputStream(BYTES2);
    Files.copy(is, path, REPLACE_EXISTING);
    final byte[] readBytes = Files.readAllBytes(path);
    assertArrayEquals(BYTES2, readBytes);
  }

  @Test
  public void shouldCreateDirectories() throws IOException {
    final Path path = new JMemPath(fileSystem, "/temp/working/path");
    Files.createDirectories(path);
  }

  @Test
  public void shouldCreateDirectory() throws IOException {
    Path path = new JMemPath(fileSystem, "/temp");
    Files.createDirectory(path);
    path = new JMemPath(fileSystem, "/temp/working");
    Files.createDirectory(path);
  }

  @Test
  public void shouldCreateFile() throws IOException {
    final Path path = new JMemPath(fileSystem, "/input.txt");
    Files.createFile(path);
  }

  @Test
  public void shouldCreateTempDirectory() throws IOException {
    final Path dir = Files.createTempDirectory(new JMemPath(fileSystem, "/"), "temp");
    assertTrue(Files.exists(dir));
  }

  @Test
  public void shouldCreateTempFile() throws IOException {
    final Path file = Files.createTempFile(new JMemPath(fileSystem, "/"), "temp", ".txt");
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
    FileTime t = (FileTime) Files.getAttribute(path, "lastModifiedTime");
    assertEquals(now, t.toMillis());
    t = (FileTime) Files.getAttribute(path, "lastAccessTime");
    assertEquals(now, t.toMillis());
    t = (FileTime) Files.getAttribute(path, "creationTime");
    assertEquals(now, t.toMillis());
    final long size = (long) Files.getAttribute(path, "size");
    assertEquals(BYTES.length, size);
    boolean isRegularFile = (boolean) Files.getAttribute(path, "isRegularFile");
    assertTrue(isRegularFile);
    boolean isDirectory = (boolean) Files.getAttribute(path, "isDirectory");
    assertFalse(isDirectory);
    final Path tempPath = new JMemPath(fs, "/temp");
    Files.createDirectory(tempPath);
    isRegularFile = (boolean) Files.getAttribute(tempPath, "isRegularFile");
    assertFalse(isRegularFile);
    isDirectory = (boolean) Files.getAttribute(tempPath, "isDirectory");
    assertTrue(isDirectory);
    // TODO symbolic links and fileKey not supported yet
  }

  @Test
  public void shouldGetSeekableByteChannelForAllowedOpenOptions() throws IOException {
    final JMemPath path = new JMemPath(fileSystem, "/output.txt");
    Files.write(path, BYTES);
    SeekableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.APPEND);
    channel.close();
    channel = Files.newByteChannel(path, StandardOpenOption.READ);
    channel.close();
    channel = Files.newByteChannel(path, StandardOpenOption.WRITE);
    channel.close();
    channel = Files.newByteChannel(path, StandardOpenOption.WRITE, StandardOpenOption.READ);
    channel.close();
  }

  @Test(expected = IOException.class)
  public void shouldNotAppendNewFileWithWrite() throws IOException {
    Files.write(new JMemPath(fileSystem, "/output.txt"), BYTES, APPEND);
    final byte[] readBytes = Files.readAllBytes(new JMemPath(fileSystem, "/output.txt"));
    assertArrayEquals(BYTES, readBytes);
  }

  @Test
  public void shouldNotGetSeekableByteChannelForInvalidOpenCombinations() throws IOException {
    final JMemPath path = new JMemPath(fileSystem, "/output.txt");
    Files.write(path, BYTES);
    try {
      final SeekableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.APPEND, StandardOpenOption.READ);
      fail("Expected IOException");
    }
    catch (final IllegalArgumentException e) {
      // expected
    }
    try {
      final SeekableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.APPEND);
      fail("Expected IOException");
    }
    catch (final IllegalArgumentException e) {
      // expected
    }
  }

  @Test(expected = IOException.class)
  public void shouldNotReadNonexistentFile() throws IOException {
    /*final byte[] inputBytes = */Files.readAllBytes(new JMemPath(fileSystem, "/output.txt"));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldOnlyAllowReadOnNewInputStream() throws IOException {
    final JMemPath path = new JMemPath(fileSystem, "/output.txt");
    Files.write(path, BYTES);
    final InputStream is = Files.newInputStream(path, StandardOpenOption.READ, StandardOpenOption.CREATE_NEW);
    is.close();
  }

  @Test
  public void shouldOpenExistingWithCreateOptionWithoutTruncating() throws IOException {
    final JMemPath path = new JMemPath(fileSystem, "/output.txt");
    Files.write(path, BYTES);
    SeekableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.CREATE);
    channel.close();
    channel = Files.newByteChannel(path, StandardOpenOption.READ);
    assertEquals(BYTES.length, channel.size());
  }

  @Test
  public void shouldOpenNewInputStream() throws IOException {
    final JMemPath path = new JMemPath(fileSystem, "/output.txt");
    Files.write(path, BYTES);
    final InputStream is = Files.newInputStream(path);
    final byte[] bytes = new byte[BYTES.length];
    assertEquals(BYTES.length, is.read(bytes));
    assertArrayEquals(BYTES, bytes);
  }

  @Test
  public void shouldOpenNewOutputStream() throws IOException {
    final JMemPath path = new JMemPath(fileSystem, "/output.txt");
    final OutputStream os = Files.newOutputStream(path);
    os.write(BYTES);
    os.close();
    Files.delete(path);
  }

  @Test
  public void shouldReadFile() throws IOException {
    Files.write(new JMemPath(fileSystem, "/output.txt"), BYTES);
    final byte[] inputBytes = Files.readAllBytes(new JMemPath(fileSystem, "/output.txt"));
    assertArrayEquals(BYTES, inputBytes);
  }

  @Test
  public void shouldTruncateExistingFileWithWrite() throws IOException {
    Files.write(new JMemPath(fileSystem, "/output.txt"), BYTES);
    Files.write(new JMemPath(fileSystem, "/output.txt"), BYTES, TRUNCATE_EXISTING);
    final byte[] readBytes = Files.readAllBytes(new JMemPath(fileSystem, "/output.txt"));
    assertArrayEquals(BYTES, readBytes);
  }

  @Test
  public void shouldWriteFile() throws IOException {
    Files.write(new JMemPath(fileSystem, "/output.txt"), BYTES);
  }
}
