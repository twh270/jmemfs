package org.byteworks.jmemfs.spi;

import static org.byteworks.jmemfs.spi.JMemConstants.SCHEME;
import static org.byteworks.jmemfs.spi.JMemConstants.SEPARATOR;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Set;

public class JMemFileSystemProvider extends FileSystemProvider {
  final JMemFileSystem theFileSystem;
  final JMemFileStore theFileStore;

  static JMemFileSystemProvider theInstance;

  public JMemFileSystemProvider() {
    this.theFileSystem = new JMemFileSystem(this);
    this.theFileStore = new JMemFileStore(theFileSystem);
    if (theInstance == null) {
      theInstance = this;
    }
  }

  @Override
  public void checkAccess(final Path path, final AccessMode... modes) throws IOException {
  }

  @Override
  public void copy(final Path source, final Path target, final CopyOption... options) throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public void createDirectory(final Path dir, final FileAttribute< ? >... attrs) throws IOException {
    theFileSystem.createDirectory(dir, attrs);
  }

  @Override
  public void delete(final Path path) throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public <V extends FileAttributeView> V getFileAttributeView(final Path path, final Class<V> type, final LinkOption... options) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public FileStore getFileStore(final Path path) throws IOException {
    return theFileStore;
  }

  @Override
  public FileSystem getFileSystem(final URI uri) {
    checkUri(uri);
    if (!uri.isAbsolute())
      throw new IllegalArgumentException("URI must be absolute");
    if (!(SEPARATOR.equals(uri.getPath())))
      throw new IllegalArgumentException("Path for filesystem root must be '/'");
    return theFileSystem;
  }

  @Override
  public Path getPath(final URI uri) {
    checkUri(uri);
    if (!uri.isAbsolute())
      throw new IllegalArgumentException("URI must be absolute");
    return new JMemPath(theFileSystem, uri.getPath());
  }

  @Override
  public String getScheme() {
    return SCHEME;
  }

  @Override
  public boolean isHidden(final Path path) throws IOException {
    return false;
  }

  @Override
  public boolean isSameFile(final Path path, final Path path2) throws IOException {
    return path.toUri().equals(path2.toUri());
  }

  @Override
  public void move(final Path source, final Path target, final CopyOption... options) throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");

  }

  @Override
  public SeekableByteChannel newByteChannel(final Path path, final Set< ? extends OpenOption> options, final FileAttribute< ? >... attrs) throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public DirectoryStream<Path> newDirectoryStream(final Path dir, final Filter< ? super Path> filter) throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public FileSystem newFileSystem(final URI uri, final Map<String, ? > env) throws IOException {
    return new JMemFileSystem(this, env);
  }

  @Override
  public <A extends BasicFileAttributes> A readAttributes(final Path path, final Class<A> type, final LinkOption... options) throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public Map<String, Object> readAttributes(final Path path, final String attributes, final LinkOption... options) throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public void setAttribute(final Path path, final String attribute, final Object value, final LinkOption... options) throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");

  }

  protected void checkUri(final URI path) {
    if (!(SCHEME.equals(path.getScheme())))
      throw new IllegalArgumentException("URI for this filesystem must be " + SCHEME);
  }
}
