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
}
