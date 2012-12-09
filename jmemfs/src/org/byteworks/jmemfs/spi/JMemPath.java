package org.byteworks.jmemfs.spi;

import static org.byteworks.jmemfs.spi.JMemConstants.SEPARATOR;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

public class JMemPath implements Path {
  private final String path;
  private final JMemFileSystem fileSystem;
  private int[] nameIndexes = null;

  public JMemPath(final JMemFileSystem fileSystem, final String path) {
    this.fileSystem = fileSystem;
    this.path = path;
  }

  @Override
  public int compareTo(final Path other) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public boolean endsWith(final Path other) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public boolean endsWith(final String other) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public Path getFileName() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public FileSystem getFileSystem() {
    return fileSystem;
  }

  @Override
  public Path getName(final int index) {
    final int[] indexes = getIndexes();
    if (index < 0 || index >= indexes.length)
      throw new IllegalArgumentException("Index must be between 0 and " + (indexes.length - 1));
    return new JMemPath(fileSystem, getPathElement(index));
  }

  @Override
  public int getNameCount() {
    return getIndexes().length;
  }

  @Override
  public Path getParent() {
    final StringBuilder sb = new StringBuilder();
    if (path.startsWith(SEPARATOR)) {
      sb.append(SEPARATOR);
    }
    final int[] indexes = getIndexes();
    for (int i = 0; i < indexes.length - 1; i++) {
      sb.append(getName(i)).append(SEPARATOR);
    }
    sb.deleteCharAt(sb.length() - 1);
    return new JMemPath(fileSystem, sb.toString());
  }

  @Override
  public Path getRoot() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public boolean isAbsolute() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public Iterator<Path> iterator() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public Path normalize() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public WatchKey register(final WatchService watcher, final Kind< ? >... events) throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public WatchKey register(final WatchService watcher, final Kind< ? >[] events, final Modifier... modifiers) throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public Path relativize(final Path other) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public Path resolve(final Path other) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public Path resolve(final String other) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public Path resolveSibling(final Path other) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public Path resolveSibling(final String other) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public boolean startsWith(final Path other) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public boolean startsWith(final String other) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public Path subpath(final int beginIndex, final int endIndex) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public Path toAbsolutePath() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public File toFile() {
    return new File(toString());
  }

  @Override
  public Path toRealPath(final LinkOption... options) throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public String toString() {
    return path;
  }

  @Override
  public URI toUri() {
    try {
      return new URI(JMemConstants.SCHEME + ":" + path);
    }
    catch (final URISyntaxException e) {
      throw new IllegalStateException("Path cannot be converted to URI: " + path);
    }
  }

  private synchronized void buildIndexes() {
    if (nameIndexes == null) {
      if (path.length() == 0) {
        nameIndexes = new int[] { 0 };
      }
      else if (SEPARATOR.equals(path)) {
        nameIndexes = new int[0];
      }
      else {
        int offset = 0;
        if (path.startsWith(SEPARATOR)) {
          offset = 1;
        }
        final String[] chunks = path.length() == 0 ? new String[1] : path.substring(offset).split(SEPARATOR);
        final int[] tempNameIndexes = new int[chunks.length];
        int position = 0;
        for (int i = 0; i < chunks.length; i++) {
          tempNameIndexes[i] = position + offset;
          position += chunks[i].length() + 1;
        }
        nameIndexes = tempNameIndexes;
      }
    }
  }

  private int[] getIndexes() {
    if (nameIndexes == null) {
      buildIndexes();
    }
    return nameIndexes;
  }

  private String getPathElement(final int index) {
    if (index == nameIndexes.length - 1)
      return path.substring(nameIndexes[nameIndexes.length - 1]);
    return path.substring(nameIndexes[index], nameIndexes[index + 1] - 1);
  }
}
