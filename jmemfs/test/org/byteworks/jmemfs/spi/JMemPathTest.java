package org.byteworks.jmemfs.spi;

import static org.byteworks.jmemfs.spi.TestCommon.JMEM_ROOT;
import static org.byteworks.jmemfs.spi.TestCommon.JMEM_URI;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class JMemPathTest {
  @Test
  public void shouldBeAbsolute() {
    final JMemFileSystem fs = new JMemFileSystem(new JMemFileSystemProvider());
    Path path = new JMemPath(fs, "/absolute/path");
    assertTrue(path.isAbsolute());

    path = new JMemPath(fs, "relative/path");
    assertFalse(path.isAbsolute());
  }

  @Test
  public void shouldCreateFileObject() {
    Path path = Paths.get(JMEM_URI("/some/name"));
    final File file = path.toFile();
    assertEquals("\\some\\name", file.toString());
    path = file.toPath();
  }

  @Test
  public void shouldCreateUriFromName() {
    final Path path = Paths.get(JMEM_URI("/o/aa"));
    final URI uriForm = path.toUri();
    assertEquals("jmemfs:/o/aa", uriForm.toString());
  }

  @Test
  public void shouldGetAbsolutePath() {
    JMemFileSystem fs = new JMemFileSystem(new JMemFileSystemProvider());
    Path path = new JMemPath(fs, "/absolute/path");
    path = path.toAbsolutePath();
    assertEquals("/absolute/path", path.toString());

    final Map<String, String> env = new HashMap<String, String>();
    env.put("default.dir", "/root");
    fs = new JMemFileSystem(new JMemFileSystemProvider(), env);
    path = new JMemPath(fs, "relative/path");
    path = path.toAbsolutePath();
    assertEquals("/root/relative/path", path.toString());

    path = new JMemPath(fs, "/absolute/path");
    path = path.toAbsolutePath();
    assertEquals("/absolute/path", path.toString());
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
    assertEquals(0, path.getNameCount());
    path = Paths.get(JMEM_URI("/two/path"));
    assertEquals(2, path.getNameCount());
  }

  @Test
  public void shouldGetNameIndexesAndNames() {
    final JMemFileSystem fs = new JMemFileSystem(new JMemFileSystemProvider());
    Path path = new JMemPath(fs, "/");
    assertEquals(0, path.getNameCount());

    path = new JMemPath(fs, "");
    assertEquals(1, path.getNameCount());
    assertEquals("", path.getName(0).toString());

    path = new JMemPath(fs, "relative/path/here");
    assertEquals("relative", path.getName(0).toString());

    path = new JMemPath(fs, "/o/aa/bbb/defgggg");
    assertEquals("o", path.getName(0).toString());
    assertEquals("aa", path.getName(1).toString());
    assertEquals("bbb", path.getName(2).toString());
    assertEquals("defgggg", path.getName(3).toString());

  }

  @Test
  public void shouldGetParent() {
    final JMemFileSystem fs = new JMemFileSystem(new JMemFileSystemProvider());
    Path path = Paths.get(JMEM_URI("/o/aa/bbb/defgggg"));
    Path parent = path.getParent();
    assertEquals("/o/aa/bbb", parent.toString());

    path = new JMemPath(fs, "relative/path/here");
    parent = path.getParent();
    assertEquals("relative/path", parent.toString());
  }

  @Test
  public void shouldNormalizePath() {
    final JMemFileSystem fs = new JMemFileSystem(new JMemFileSystemProvider());
    final Path path = new JMemPath(fs, "///way//too////many/slashes/in//this//path/");
    assertEquals("/way/too/many/slashes/in/this/path", path.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowOnInvalidNameIndex() {
    final Path path = Paths.get(JMEM_URI("/some/name"));
    path.getName(2);
  }

  @Test
  public void testConvertToString() {
    final JMemFileSystem fs = new JMemFileSystem(new JMemFileSystemProvider());
    Path path = Paths.get(JMEM_ROOT);
    assertEquals("/", path.toString());

    path = new JMemPath(fs, "");
    assertEquals("", path.toString());

    path = new JMemPath(fs, "/");
    assertEquals("/", path.toString());

    path = new JMemPath(fs, "relative/path");
    assertEquals("relative/path", path.toString());

    path = new JMemPath(fs, "/root/path/trailing/slash/");
    assertEquals("/root/path/trailing/slash", path.toString());
  }
}
