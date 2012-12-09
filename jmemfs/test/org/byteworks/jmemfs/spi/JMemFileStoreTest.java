package org.byteworks.jmemfs.spi;

import static org.byteworks.jmemfs.spi.TestCommon.JMEM_ROOT;

import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;

import junit.framework.Assert;

import org.junit.Test;

public class JMemFileStoreTest {

  @Test
  public void shouldHaveNoName() throws IOException {
    final FileSystemProvider p = (new JMemFileSystemProvider());
    Assert.assertEquals("", p.getFileStore(Paths.get(JMEM_ROOT)).name());
  }

  @Test
  public void shouldHaveType() throws IOException {
    final FileSystemProvider p = (new JMemFileSystemProvider());
    Assert.assertEquals("jmemfs", p.getFileStore(Paths.get(JMEM_ROOT)).type());
  }

  //  public static void main(final String[] args) {
  //    final URI tmp =
  //        URI.create("jar:file:" + Paths.get("C:\\Users\\Thomas\\Downloads\\openjdk-7u6-fcs-src-b24-28_aug_2012.zip").toUri().getPath());
  //    try {
  //      final FileSystem tmpFS = FileSystems.newFileSystem(tmp, new HashMap<String, String>());
  //    }
  //    catch (final IOException e1) {
  //      System.out.println("Uh oh:" + e1.getMessage());
  //    }
  //
  //    for (final FileStore store : java.nio.file.FileSystems.getDefault().getFileStores()) {
  //      System.out.println("FileStore: " + store.name() + ", " + store.type());
  //    }
  //    final List<FileSystemProvider> list = FileSystemProvider.installedProviders();
  //    for (final FileSystemProvider p : list) {
  //      URI uri = null;
  //      try {
  //        final String scheme = p.getScheme();
  //        uri = scheme == "jar" ? tmp : new URI(scheme + ":/");
  //
  //        final FileSystem fs = p.getFileSystem(uri);
  //        System.out.println("FileSystem FileStores for provider scheme=" + scheme);
  //        for (final FileStore s : fs.getFileStores()) {
  //          System.out.println("  name=" + s.name() + ", type=" + s.type());
  //        }
  //        System.out.println("FileSystem roots for provider scheme=" + scheme);
  //        for (final Path path : fs.getRootDirectories()) {
  //          System.out.println("  root = " + path.toString());
  //        }
  //      }
  //      catch (final URISyntaxException e) {
  //        System.out.println("\tERROR: Bad URI syntax for " + uri.toString());
  //      }
  //      catch (final FileSystemNotFoundException e) {
  //        System.out.println("\tERROR: Filesystem for URI not found: " + uri.toString());
  //      }
  //    }
  //  }
}
