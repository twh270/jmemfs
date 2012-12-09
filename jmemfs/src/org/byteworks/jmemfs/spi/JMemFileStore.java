package org.byteworks.jmemfs.spi;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;

public class JMemFileStore extends FileStore {
  private final JMemFileSystem fileSystem;

  public JMemFileStore(final JMemFileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  @Override
  public Object getAttribute(final String attribute) throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public <V extends FileStoreAttributeView> V getFileStoreAttributeView(final Class<V> type) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public long getTotalSpace() throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public long getUnallocatedSpace() throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public long getUsableSpace() throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public boolean isReadOnly() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public String name() {
    return "";
  }

  @Override
  public boolean supportsFileAttributeView(final Class< ? extends FileAttributeView> type) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public boolean supportsFileAttributeView(final String name) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public String type() {
    return "jmemfs";
  }
}
