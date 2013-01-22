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
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

public class JMemPathTest {
  @Test
  public void shouldBeAbsolute() {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.theFileSystem;
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
    JMemFileSystemProvider p = new JMemFileSystemProvider();
    JMemFileSystem fs = p.theFileSystem;
    Path path = new JMemPath(fs, "/absolute/path");
    path = path.toAbsolutePath();
    assertEquals("/absolute/path", path.toString());

    final Map<String, Object> env = new HashMap<String, Object>();
    env.put("default.dir", "/root");
    p = new JMemFileSystemProvider(env);
    fs = p.theFileSystem;
    path = new JMemPath(fs, "relative/path");
    path = path.toAbsolutePath();
    assertEquals("/root/relative/path", path.toString());

    path = new JMemPath(fs, "/absolute/path");
    path = path.toAbsolutePath();
    assertEquals("/absolute/path", path.toString());
  }

  @Test
  public void shouldGetFileName() {
    Path path = Paths.get(JMEM_URI("/some/name"));
    assertEquals("name", path.getFileName().toString());

    path = Paths.get(JMEM_URI("/root"));
    assertEquals("root", path.getFileName().toString());

    path = Paths.get(JMEM_URI("/"));
    assertEquals(null, path.getFileName());

    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.theFileSystem;
    path = new JMemPath(fs, "");
    assertEquals("", path.getFileName().toString());
  }

  @Test
  public void shouldGetFileSystemFromPath() {
    final Path path = Paths.get(JMEM_ROOT);
    final FileSystem fs = path.getFileSystem();
    assertEquals(FileSystems.getFileSystem(JMEM_ROOT), fs);
  }

  @Test
  public void shouldGetIterator() {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.theFileSystem;
    Path path = new JMemPath(fs, "relative/path/here");
    String[] parts = new String[] { "relative", "path", "here" };
    Iterator<Path> i = path.iterator();
    for (final String part : parts) {
      assertEquals(part, i.next().toString());
    }

    path = new JMemPath(fs, "/absolute/path/name/here");
    parts = new String[] { "absolute", "path", "name", "here" };
    i = path.iterator();
    for (final String part : parts) {
      assertEquals(part, i.next().toString());
    }

    path = new JMemPath(fs, "/");
    i = path.iterator();
    assertFalse(i.hasNext());

    path = new JMemPath(fs, "");
    i = path.iterator();
    assertTrue(i.hasNext());
    assertEquals("", i.next().toString());
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
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.theFileSystem;
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
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.theFileSystem;
    Path path = Paths.get(JMEM_URI("/o/aa/bbb/defgggg"));
    Path parent = path.getParent();
    assertEquals("/o/aa/bbb", parent.toString());

    path = new JMemPath(fs, "relative/path/here");
    parent = path.getParent();
    assertEquals("relative/path", parent.toString());
  }

  @Test
  public void shouldGetRoot() {
    final Path path = Paths.get(JMEM_URI("/some/name")).getRoot();
    assertEquals("/", path.toString());
  }

  @Test
  public void shouldGetSubPath() {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.theFileSystem;
    final Path path = new JMemPath(fs, "/absolute/path/with/subdirectory");
    Path path2 = path.subpath(1, 3);
    assertEquals("path/with", path2.toString());
    path2 = path.subpath(0, 1);
    assertEquals("absolute", path2.toString());
    path2 = path.subpath(0, 3);
    assertEquals("absolute/path/with", path2.toString());
  }

  @Test
  public void shouldNormalizePath() {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.theFileSystem;
    Path path = new JMemPath(fs, "///way//too////many/slashes/in//this//path/");
    assertEquals("/way/too/many/slashes/in/this/path", path.toString());
    path = new JMemPath(fs, "relative/path/trailing/slash/");
    assertEquals("relative/path/trailing/slash", path.toString());
    path = new JMemPath(fs, "relative/path");
    assertEquals("relative/path", path.toString());
    path = new JMemPath(fs, "/absolute/path");
    assertEquals("/absolute/path", path.toString());

  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldNotRelativizeIncompatiblePaths() {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.theFileSystem;
    final Path path1 = new JMemPath(fs, "/absolute/path");
    final Path path2 = new JMemPath(fs, "relative/path/with/subdirectory");
    /*final Path path3 =*/path1.relativize(path2);
  }

  @Test
  public void shouldRelativizeAbsolutePaths() {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.theFileSystem;
    final Path path1 = new JMemPath(fs, "/absolute/path");
    final Path path2 = new JMemPath(fs, "/absolute/path/with/subdirectory");
    Path path3 = path1.relativize(path2);
    assertEquals("with/subdirectory", path3.toString());
    path3 = path2.relativize(path1);
    assertEquals("../..", path3.toString());
  }

  @Test
  public void shouldRelativizeRelativePaths() {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.theFileSystem;
    final Path path1 = new JMemPath(fs, "relative/path");
    final Path path2 = new JMemPath(fs, "relative/path/with/subdirectory");
    Path path3 = path1.relativize(path2);
    assertEquals("with/subdirectory", path3.toString());
    path3 = path2.relativize(path1);
    assertEquals("../..", path3.toString());
  }

  @Test
  public void shouldRelativizeUniquePaths() {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.theFileSystem;
    Path path1 = new JMemPath(fs, "/absolute/path");
    Path path2 = new JMemPath(fs, "/relative/path/with/subdirectory");
    Path path3 = path1.relativize(path2);
    assertEquals("../../relative/path/with/subdirectory", path3.toString());
    path1 = new JMemPath(fs, "absolute/path");
    path2 = new JMemPath(fs, "relative/path/with/subdirectory");
    path3 = path1.relativize(path2);
    assertEquals("../../relative/path/with/subdirectory", path3.toString());
  }

  @Test
  public void shouldResolve() {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.theFileSystem;
    Path path1 = new JMemPath(fs, "/foo");
    Path path2 = new JMemPath(fs, "/bar");
    assertEquals("/bar", path1.resolve(path2).toString());
    path2 = new JMemPath(fs, "bar");
    assertEquals("/foo/bar", path1.resolve(path2).toString());
    path1 = new JMemPath(fs, "foo");
    path2 = new JMemPath(fs, "/bar");
    assertEquals("/bar", path1.resolve(path2).toString());
    path2 = new JMemPath(fs, "bar");
    assertEquals("foo/bar", path1.resolve(path2).toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowOnInvalidNameIndex() {
    final Path path = Paths.get(JMEM_URI("/some/name"));
    path.getName(2);
  }

  @Test
  public void testConvertToString() {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.theFileSystem;
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
