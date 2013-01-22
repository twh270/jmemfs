package org.byteworks.jmemfs.spi;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.byteworks.jmemfs.spi.JMemConstants.SEPARATOR;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.CopyOption;
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
    this.env = new HashMap<String, Object>();
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
    if (options.contains(CREATE_NEW)) {
      fileInode = parent.createFile(fileName, this);
    }
    else if (options.contains(CREATE)) {
      try {
        fileInode = parent.createFile(fileName, this);
      }
      catch (final FileAlreadyExistsException ex) {
        fileInode = parent.getInodeFor(fileName);
      }
    }
    return fileInode.createChannel();
  }

  private void initialize() {
    this.root = new JMemDirectoryInode(null, SEPARATOR, this);
    this.defaultDir = (String) (env.containsKey("default.dir") ? env.get("default.dir") : "/");
  }

  JMemInode assertInode(final Path path) throws NoSuchFileException {
    final JMemInode parent = assertParentInode(path);
    final JMemInode inode = parent == null ? null : parent.getInodeFor(path.getFileName());
    if (inode == null)
      throw new NoSuchFileException(path.toString());
    return inode;
  }

  JMemInode assertParentInode(final Path path) throws NoSuchFileException {
    final JMemPath parent = (JMemPath) path.toAbsolutePath().getParent();
    final JMemInode parentNode = root.getInodeFor(parent);
    if (parentNode == null)
      throw new NoSuchFileException(String.valueOf(parent));
    return parentNode;
  }

  void copy(final Path source, final Path target, final CopyOption... options) throws IOException {
    boolean replace = false;
    boolean copyAttr = false;
    for (final CopyOption option : options) {
      if (ATOMIC_MOVE == option)
        throw new UnsupportedOperationException("Cannot atomically copy files");
      else if (COPY_ATTRIBUTES == option) {
        copyAttr = true;
      }
      else if (REPLACE_EXISTING == option) {
        replace = true;
      }
    }

    final JMemInode sourceInode = assertInode(source);

    final JMemInode targetParent = assertParentInode(target);
    JMemInode targetInode = targetParent.getInodeFor(target.getFileName());

    final boolean targetExists = targetInode != null;
    final boolean isSourceDir = sourceInode.getAttributes().isDirectory();
    final boolean isTargetDir = targetExists ? targetInode.getAttributes().isDirectory() : isSourceDir;
    if (replace && isSourceDir && !isTargetDir)
      throw new IOException("Source path " + source.toString() + " is a directory but target " + target.toString() + " is a file");
    else if (!replace && targetExists)
      throw new FileAlreadyExistsException("Target path already exists: " + target.toString());
    else if (!targetExists && isSourceDir) {
      targetInode = targetParent.createDirectory(target.getFileName(), this);
    }
    else if (!targetExists && !isSourceDir) {
      targetInode = targetParent.createFile(target.getFileName(), this);
    }
    sourceInode.copyTo(targetInode, replace, copyAttr);
  }

  SeekableByteChannel createChannel(final Path path, final Set< ? extends OpenOption> options, final FileAttribute< ? >[] attrs) throws IOException {
    if (options.contains(CREATE) || options.contains(CREATE_NEW))
      return createFile(path, options, attrs);
    else if (options.contains(READ) || options.contains(WRITE) || options.contains(APPEND) || options.contains(TRUNCATE_EXISTING)
        || options.isEmpty())
      return openFile(path, options, attrs);
    throw new IllegalArgumentException("Cannot create a byte channel for the specified open options " + createString(options));
  }

  void createDirectory(final Path dir, final FileAttribute< ? >[] attrs) throws IOException {
    assertParentInode(dir).createDirectory(dir.getFileName(), this);
  }

  void delete(final Path path) throws IOException {
    final JMemInode inode = assertInode(path);
    inode.unlink();
  }

  SeekableByteChannel openFile(final Path path, final Set< ? extends OpenOption> options, final FileAttribute< ? >[] attrs) throws IOException {
    final JMemInode parent = assertParentInode(path);
    final JMemInode fileInode = parent.getInodeFor(path.getFileName());
    if (fileInode == null)
      throw new NoSuchFileException("File does not exist: " + path.toString());
    final SeekableByteChannel channel = fileInode.createChannel();
    if (options.contains(TRUNCATE_EXISTING)) {
      channel.truncate(0);
    }
    if (options.contains(APPEND)) {
      channel.position(channel.size());
    }
    return channel;
  }

  <A extends BasicFileAttributes> A readAttributes(final Path path, final Class<A> type, final LinkOption... options) throws IOException {
    assertValidAttributeClass(type);
    final JMemInode parent = assertParentInode(path);
    JMemInode inode;
    inode = parent.getInodeFor(path.getFileName());
    if (inode == null)
      throw new NoSuchFileException("No such file: " + path.toString());
    return (A) inode.getAttributes();
  }

  private <A extends BasicFileAttributes> void assertValidAttributeClass(final Class<A> type) {
    if (!(type == JMemFileAttributes.class) && !(type == BasicFileAttributes.class))
      throw new UnsupportedOperationException("Unsupported attribute type " + type.getName());
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
