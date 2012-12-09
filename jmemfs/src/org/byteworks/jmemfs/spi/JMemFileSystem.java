package org.byteworks.jmemfs.spi;

import static org.byteworks.jmemfs.spi.JMemConstants.SEPARATOR;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.byteworks.jmemfs.spi.impl.JMemDirectoryInode;

public class JMemFileSystem extends FileSystem {
  private final JMemFileSystemProvider provider;
  private final Map<String, ? > env;
  JMemDirectoryInode root;

  public JMemFileSystem(final JMemFileSystemProvider jMemFileSystemProvider) {
    this.provider = jMemFileSystemProvider;
    this.env = new HashMap<String, String>();
    this.root = new JMemDirectoryInode(SEPARATOR);
  }

  public JMemFileSystem(final JMemFileSystemProvider jMemFileSystemProvider, final Map<String, ? > env2) {
    this.provider = jMemFileSystemProvider;
    this.env = env2;
  }

  @Override
  public void close() throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  public Map<String, ? > getEnvironment() {
    return env;
  }

  @Override
  public Iterable<FileStore> getFileStores() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public Path getPath(final String first, final String... more) {
    if (more == null)
      return new JMemPath(provider.getTheFileSystem(), first);
    final StringBuilder sb = new StringBuilder(first);
    for (final String segment : more) {
      sb.append("/").append(segment);
    }
    return new JMemPath(provider.getTheFileSystem(), sb.toString());
  }

  @Override
  public PathMatcher getPathMatcher(final String syntaxAndPattern) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public Iterable<Path> getRootDirectories() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public String getSeparator() {
    return SEPARATOR;
  }

  @Override
  public UserPrincipalLookupService getUserPrincipalLookupService() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public boolean isOpen() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public boolean isReadOnly() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public WatchService newWatchService() throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public FileSystemProvider provider() {
    return provider;
  }

  @Override
  public Set<String> supportedFileAttributeViews() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  void createDirectory(final Path dir, final FileAttribute< ? >[] attrs) {
    final Path parent = dir.getParent();
  }

}
