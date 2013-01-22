package org.byteworks.jmemfs.spi;

import static junit.framework.Assert.assertEquals;
import static org.byteworks.jmemfs.spi.TestCommon.BAD_URI;
import static org.byteworks.jmemfs.spi.TestCommon.JMEM_ROOT;
import static org.byteworks.jmemfs.spi.TestCommon.JMEM_URI;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileStore;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.byteworks.jmemfs.spi.impl.JMemInode;
import org.junit.Test;

public class JMemFileSystemTest {

  @Test
  public void shouldCreateDirectory() throws IOException {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.theFileSystem;
    fs.createDirectory(Paths.get(JMEM_URI("/temp")), null);
    fs.createDirectory(Paths.get(JMEM_URI("/temp/working")), null);
  }

  @Test
  public void shouldCreatePathFromSegments() {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.theFileSystem;
    Path path = fs.getPath("/first", "second", "third");
    assertEquals("/first/second/third", path.toString());

    path = fs.getPath("/some//", "/weird", "segments", "/here");
    assertEquals("/some/weird/segments/here", path.toString());
  }

  @Test
  public void shouldGetDotAndDotDot() throws IOException {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.theFileSystem;
    fs.createDirectory(new JMemPath(fs, "dir1"), null);
    final JMemPath dir2Path = new JMemPath(fs, "/dir1/dir2");
    fs.createDirectory(dir2Path, null);
    final JMemPath pathToGrandChild = (JMemPath) Paths.get(TestCommon.JMEM_URI("/dir1/dir2"));
    final JMemInode dir2Inode = fs.root().getInodeFor(dir2Path);
    JMemInode test = dir2Inode.getInodeFor(new JMemPath(fs, "."));
    assertEquals(dir2Inode, test);
    final JMemInode dir1Inode = dir2Inode.getParent();
    test = dir2Inode.getInodeFor(new JMemPath(fs, ".."));
    assertEquals(dir1Inode, test);
  }

  @Test
  public void shouldGetFileStores() {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.theFileSystem;
    int count = 0;
    for (final FileStore f : fs.getFileStores()) {
      count++;
      assertEquals("jmemfs", f.type());
    }
    assertEquals(1, count);
  }

  @Test
  public void shouldGetFileSystemProviderFromFS() {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.theFileSystem;
    assertTrue(fs.provider() instanceof JMemFileSystemProvider);
  }

  @Test
  public void shouldGetJMemFileSystemFromURI() {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.theFileSystem;
    assertTrue(fs != null);
    assertTrue(fs instanceof JMemFileSystem);
  }

  @Test(expected = NoSuchFileException.class)
  public void shouldGetNullForNonexistentParent() throws NoSuchFileException {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.theFileSystem;
    fs.assertParentInode(fs.getPath("/this/path/does/not/exist"));
  }

  @Test
  public void shouldGetRootDirectories() {
    final JMemFileSystemProvider provider = new JMemFileSystemProvider();
    final JMemFileSystem fs = provider.theFileSystem;
    int count = 0;
    for (final Path p : fs.getRootDirectories()) {
      count++;
      assertEquals("/", p.toString());
    }
    assertEquals(1, count);
  }

  @Test
  public void shouldGetRootPath() {
    final Path path = Paths.get(JMEM_ROOT);
    assertEquals("/", path.toString());
    assertNotNull(path);
  }

  @Test
  public void shouldGetSeparator() {
    final JMemFileSystemProvider p = new JMemFileSystemProvider();
    final JMemFileSystem fs = p.theFileSystem;
    assertEquals("/", fs.getSeparator());
  }

  @Test(expected = FileSystemNotFoundException.class)
  public void shouldNotGetPathWithWrongScheme() {
    Paths.get(BAD_URI);
    fail("Should have thrown exception here");
  }

  @Test
  public void shouldSetDefaultDir() {
    final Map<String, Object> env = new HashMap<String, Object>();
    env.put("default.dir", "/root");
    final JMemFileSystemProvider p = new JMemFileSystemProvider(env);
    final JMemFileSystem fs = p.theFileSystem;
    assertEquals("/root", fs.getEnvironment().get("default.dir"));
    assertEquals("/root", fs.defaultDir());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIfURIPathNotRoot() throws URISyntaxException {
    FileSystems.getFileSystem(new URI("jmemfs:/not_root"));
  }

}
