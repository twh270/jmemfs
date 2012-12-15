package org.byteworks.jmemfs.spi;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.byteworks.jmemfs.spi.JMemConstants.SEPARATOR;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
    return Arrays.asList((FileStore) provider.getTheFileStore());
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
    return Arrays.asList(new Path[] { new JMemPath(this, root.getName()) });
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

  private JMemInode assertParentInode(final Path path) throws NoSuchFileException {
    final JMemPath parent = JMemPath.asJMemPath(path.toAbsolutePath().getParent());
    final JMemInode parentNode = root.getInodeFor(parent.getPathElements());
    if (parentNode == null)
      throw new NoSuchFileException(parent.toString());
    return parentNode;
  }

  private SeekableByteChannel createFile(final Path path, final Set< ? extends OpenOption> options, final FileAttribute< ? >[] attrs) throws NoSuchFileException,
      FileAlreadyExistsException {
    final JMemInode parent = assertParentInode(path);
    JMemInode fileInode = null;
    final String name = path.getFileName().toString();
    if (options.contains(CREATE_NEW)) {
      fileInode = parent.createFile(name);
    }
    else if (options.contains(CREATE)) {
      try {
        fileInode = parent.createFile(name);
      }
      catch (final FileAlreadyExistsException ex) {
        fileInode = parent.getInodeFor(name);
      }
    }
    return fileInode.createChannel();
  }

  private SeekableByteChannel openFile(final Path path, final Set< ? extends OpenOption> options, final FileAttribute< ? >[] attrs) throws NoSuchFileException {
    final JMemInode parent = assertParentInode(path);
    final JMemInode fileInode = parent.getInodeFor(path.getFileName().toString());
    if (fileInode == null)
      throw new NoSuchFileException("File does not exist: " + path.toString());
    return fileInode.createChannel();
  }

  SeekableByteChannel createChannel(final Path path, final Set< ? extends OpenOption> options, final FileAttribute< ? >[] attrs) throws NoSuchFileException,
      FileAlreadyExistsException {
    if (options.contains(CREATE) || options.contains(CREATE_NEW))
      return createFile(path, options, attrs);
    else if (options.contains(READ) || options.contains(WRITE) || options.isEmpty())
      return openFile(path, options, attrs);
    throw new IllegalArgumentException("Cannot create a byte channel for the specified open options " + createString(options));
  }

  void createDirectory(final Path dir, final FileAttribute< ? >[] attrs) throws IOException {
    assertParentInode(dir).createDirectory(dir.getFileName().toString());
  }

  private static final <T> String createString(final Collection<T> c) {
    final Iterator<T> i = c.iterator();
    final StringBuilder sb = new StringBuilder();
    while (i.hasNext()) {
      sb.append(String.valueOf(i.next())).append(',');
    }
    if (sb.length() > 0) {
      sb.setLength(sb.length() - 1);
    }
    return sb.toString();
  }
}
