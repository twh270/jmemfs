package org.byteworks.jmemfs.spi;

import static org.byteworks.jmemfs.spi.JMemConstants.SEPARATOR;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.NoSuchFileException;
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
import org.byteworks.jmemfs.spi.impl.JMemInode;

public class JMemFileSystem extends FileSystem {
  private final JMemFileSystemProvider provider;
  private final Map<String, ? > env;
  private JMemDirectoryInode root;
  private String defaultDir;

  public JMemFileSystem(final JMemFileSystemProvider jMemFileSystemProvider) {
    this.provider = jMemFileSystemProvider;
    this.env = new HashMap<String, String>();
    this.root = new JMemDirectoryInode(null, SEPARATOR);
  }

  public JMemFileSystem(final JMemFileSystemProvider jMemFileSystemProvider, final Map<String, ? > env2) {
    this.provider = jMemFileSystemProvider;
    this.env = env2;
    this.defaultDir = (String) (env.containsKey("default.dir") ? env.get("default.dir") : "/");
  }

  @Override
  public void close() throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  public String defaultDir() {
    return defaultDir;
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

  JMemInode checkParentsExist(final Path child) throws NoSuchFileException {
    final Path parent = child.toAbsolutePath().getParent();
    final int count = parent.getNameCount();
    JMemInode curr = root;
    for (int i = 0; i < count; i++) {
      curr = curr.getInodeForName(parent.getName(i).toString());
      if (curr == null)
        throw new NoSuchFileException(parent.toString());
    }
    return curr;
  }

  void createDirectory(final Path dir, final FileAttribute< ? >[] attrs) throws IOException {
    final JMemInode parentNode = checkParentsExist(dir);
    parentNode.createDirectory(dir.getFileName().toString());
  }

}
