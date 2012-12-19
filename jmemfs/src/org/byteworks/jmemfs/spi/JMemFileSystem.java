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
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
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
import org.byteworks.jmemfs.spi.impl.JMemFileAttributes;
import org.byteworks.jmemfs.spi.impl.JMemInode;

public class JMemFileSystem extends FileSystem {
  private final JMemFileSystemProvider provider;
  private final Map<String, ? > env;
  private JMemDirectoryInode root;
  private String defaultDir;

  public JMemFileSystem(final JMemFileSystemProvider jMemFileSystemProvider) {
    this.provider = jMemFileSystemProvider;
    this.env = new HashMap<String, String>();
    initialize();
  }

  public JMemFileSystem(final JMemFileSystemProvider jMemFileSystemProvider, final Map<String, ? > env2) {
    this.provider = jMemFileSystemProvider;
    this.env = env2;
    initialize();
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

  private SeekableByteChannel createFile(final Path path, final Set< ? extends OpenOption> options, final FileAttribute< ? >[] attrs) throws NoSuchFileException,
      FileAlreadyExistsException {
    final JMemInode parent = assertParentInode(path);
    JMemInode fileInode = null;
    final Path fileName = path.getFileName();
    final String name = fileName.toString();
    if (options.contains(CREATE_NEW)) {
      fileInode = parent.createFile(fileName);
    }
    else if (options.contains(CREATE)) {
      try {
        fileInode = parent.createFile(fileName);
      }
      catch (final FileAlreadyExistsException ex) {
        fileInode = parent.getInodeFor(fileName);
      }
    }
    return fileInode.createChannel();
  }

  private void initialize() {
    this.root = new JMemDirectoryInode(null, SEPARATOR);
    this.defaultDir = (String) (env.containsKey("default.dir") ? env.get("default.dir") : "/");
  }

  private SeekableByteChannel openFile(final Path path, final Set< ? extends OpenOption> options, final FileAttribute< ? >[] attrs) throws NoSuchFileException {
    final JMemInode parent = assertParentInode(path);
    final JMemInode fileInode = parent.getInodeFor(path.getFileName());
    if (fileInode == null)
      throw new NoSuchFileException("File does not exist: " + path.toString());
    return fileInode.createChannel();
  }

  JMemInode assertParentInode(final Path path) throws NoSuchFileException {
    final JMemPath parent = JMemPath.asJMemPath(path.toAbsolutePath().getParent());
    final JMemInode parentNode = root.getInodeFor(parent);
    if (parentNode == null)
      throw new NoSuchFileException(parent.toString());
    return parentNode;
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
    assertParentInode(dir).createDirectory(dir.getFileName());
  }

  <A extends BasicFileAttributes> A readAttributes(final Path path, final Class<A> type, final LinkOption... options) throws IOException {
    if (!(type == JMemFileAttributes.class) && !(type == BasicFileAttributes.class))
      throw new UnsupportedOperationException("Unsupported attribute type " + type.getName());
    final JMemInode parent = assertParentInode(path);
    JMemInode inode;
    if (path.getFileName() == null) {
      inode = root;
    }
    else {
      inode = parent.getInodeFor(path.getFileName());
    }
    if (inode == null)
      throw new NoSuchFileException("No such file: " + path.toString());
    return (A) inode.getAttributes();
  }

  JMemInode root() {
    return root;
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
