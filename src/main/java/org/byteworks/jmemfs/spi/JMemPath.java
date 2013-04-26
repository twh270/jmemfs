//   Copyright 2013 Thomas Wheeler 
// 
//   Licensed under the Apache License, Version 2.0 (the "License"); 
//   you may not use this file except in compliance with the License. 
//   You may obtain a copy of the License at 
// 
//     http://www.apache.org/licenses/LICENSE-2.0 
// 
//   Unless required by applicable law or agreed to in writing, software 
//   distributed under the License is distributed on an "AS IS" BASIS, 
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
//   See the License for the specific language governing permissions and 
//   limitations under the License. 
//
//   Copyright 2013 Thomas Wheeler 
// 
//   Licensed under the Apache License, Version 2.0 (the "License"); 
//   you may not use this file except in compliance with the License. 
//   You may obtain a copy of the License at 
// 
//     http://www.apache.org/licenses/LICENSE-2.0 
// 
//   Unless required by applicable law or agreed to in writing, software 
//   distributed under the License is distributed on an "AS IS" BASIS, 
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
//   See the License for the specific language governing permissions and 
//   limitations under the License. 
//
package org.byteworks.jmemfs.spi;

import static org.byteworks.jmemfs.spi.JMemConstants.SEPARATOR;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JMemPath implements Path {
  private final String path;
  private final JMemFileSystem fileSystem;
  private int[] nameIndexes = null;

  public JMemPath(final JMemFileSystem fileSystem, final String path2) {
    this.fileSystem = fileSystem;
    this.path = normalize(path2);
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
  public boolean equals(final Object obj) {
    if (!(obj instanceof JMemPath))
      return false;
    final JMemPath o = (JMemPath) obj;
    return fileSystem == o.fileSystem && path.equals(o.path);
  }

  @Override
  public Path getFileName() {
    if (isEmpty())
      return this;
    if (isRoot())
      return null;
    final int[] indexes = getIndexes();
    if (indexes.length == 1 && !path.startsWith(SEPARATOR))
      return this;
    return new JMemPath(fileSystem, getPathElement(indexes.length - 1));
  }

  @Override
  public FileSystem getFileSystem() {
    return fileSystem;
  }

  @Override
  public Path getName(final int index) {
    if (isEmpty())
      return new JMemPath(fileSystem, "");
    if (isRoot())
      throw new IllegalArgumentException("Cannot get name elements for root");
    final int[] indexes = getIndexes();
    if (index < 0 || index >= indexes.length)
      throw new IllegalArgumentException("Index must be between 0 and " + (indexes.length - 1));
    return new JMemPath(fileSystem, getPathElement(index));
  }

  @Override
  public int getNameCount() {
    if (isEmpty())
      return 1;
    if (isRoot())
      return 0;
    return getIndexes().length;
  }

  @Override
  public Path getParent() {
    if (isEmpty())
      return null;
    if (isRoot())
      return null;
    final int[] indexes = getIndexes();
    if (indexes.length == 1 && !isAbsolute())
      return null;
    final StringBuilder sb = new StringBuilder();
    if (isAbsolute()) {
      sb.append(SEPARATOR);
    }
    for (int i = 0; i < indexes.length - 1; i++) {
      sb.append(getName(i)).append(SEPARATOR);
    }
    if (sb.length() > 1) {
      sb.deleteCharAt(sb.length() - 1);
    }
    return new JMemPath(fileSystem, sb.toString());
  }

  @Override
  public Path getRoot() {
    return new JMemPath(fileSystem, SEPARATOR);
  }

  @Override
  public boolean isAbsolute() {
    return path.startsWith(SEPARATOR);
  }

  @Override
  public Iterator<Path> iterator() {
    final String[] parts = getPathElements();
    final List<Path> partsList = new ArrayList<>();
    for (final String part : parts) {
      partsList.add(new JMemPath(fileSystem, part));
    }
    return partsList.iterator();
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
  public Path relativize(final Path obj) {
    // return other path if this one is empty
    if (isEmpty())
      return obj;

    final JMemPath other = JMemPath.asJMemPath(obj);
    if (other.equals(this))
      return new JMemPath(fileSystem, "");

    if (this.isAbsolute() != other.isAbsolute())
      throw new IllegalArgumentException("Cannot relativize different path types -- both or neither must be absolute");

    final int thisCount = this.getNameCount();
    final int otherCount = other.getNameCount();
    final int smallest = (thisCount < otherCount) ? thisCount : otherCount;

    // get the name index where paths do not match
    int mismatchIndex = 0;
    while (mismatchIndex < smallest && this.getName(mismatchIndex).equals(other.getName(mismatchIndex))) {
      mismatchIndex++;
    }

    // number of "../" names we will need is the number of
    // name elements in this path remaining after we've found
    // the element where the paths differ
    int dotdots = thisCount - mismatchIndex;
    final StringBuilder result = new StringBuilder();
    String otherRemainder = "";
    if (mismatchIndex < otherCount) {
      otherRemainder = other.subpath(mismatchIndex, otherCount).toString();
    }
    // result is a  "../" for each remaining name in base
    // followed by the remaining names in other. 
    while (dotdots > 0) {
      result.append("../");
      dotdots--;
    }
    result.append(otherRemainder);
    return new JMemPath(fileSystem, new String(result));
  }

  @Override
  public Path resolve(final Path obj) {
    final JMemPath other = asJMemPath(obj);
    // If other is an absolute path return other. 
    if (other.isAbsolute())
      return other;
    // If other is an empty path then return this path.
    if (other.isEmpty())
      return this;
    // Treat other as a sub-path of this path
    return new JMemPath(fileSystem, this.path + SEPARATOR + other.path);
  }

  @Override
  public Path resolve(final String other) {
    return resolve(new JMemPath(fileSystem, other));
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
    final StringBuilder sb = new StringBuilder();
    final int count = getNameCount();
    if (beginIndex >= count)
      throw new IllegalArgumentException("Start index was too large");
    if (endIndex > count)
      throw new IllegalArgumentException("End index was too large");
    if (beginIndex < 0)
      throw new IllegalArgumentException("Start index was less than zero");
    if (beginIndex >= endIndex)
      throw new IllegalArgumentException("Start index must be less than end");
    for (int i = beginIndex; i < endIndex; i++) {
      sb.append(getName(i)).append(SEPARATOR);
    }
    return new JMemPath(fileSystem, sb.toString());
  }

  @Override
  public Path toAbsolutePath() {
    if (isAbsolute())
      return this;

    return new JMemPath(fileSystem, fileSystem.defaultDir() + SEPARATOR + path);
  }

  @Override
  public File toFile() {
    return new File(toString());
  }

  @Override
  public Path toRealPath(final LinkOption... options) throws IOException {
    return toAbsolutePath();
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

  private void buildIndexChunks() {
    int offset = 0;
    if (isAbsolute()) {
      offset = 1;
    }
    final String[] chunks = isEmpty() ? new String[1] : path.substring(offset).split(SEPARATOR);
    final int[] tempNameIndexes = new int[chunks.length];
    int position = 0;
    for (int i = 0; i < chunks.length; i++) {
      tempNameIndexes[i] = position + offset;
      position += chunks[i].length() + 1;
    }
    nameIndexes = tempNameIndexes;
  }

  private synchronized void buildIndexes() {
    if (nameIndexes == null) {
      if (isEmpty()) {
        nameIndexes = new int[0];
      }
      else if (isRoot()) {
        nameIndexes = new int[] { 0 };
      }
      else {
        buildIndexChunks();
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

  private boolean isEmpty() {
    return path.length() == 0;
  }

  private boolean isRoot() {
    return path.equals(SEPARATOR);
  }

  String[] getPathElements() {
    if (isEmpty())
      return new String[] { "" };
    if (isRoot())
      return new String[0];
    final int count = getIndexes().length;
    final String[] elements = new String[count];
    for (int i = 0; i < count; i++) {
      elements[i] = getPathElement(i);
    }
    return elements;
  }

  private static String normalize(final String input) {
    int len = input.length();
    final int off = 0;
    if (len == 0)
      return input;
    while ((len > 0) && (input.charAt(len - 1) == '/')) {
      len--;
    }
    if (len == 0)
      return "/";
    final StringBuilder sb = new StringBuilder(len);
    char prevChar = 0;
    for (int i = off; i < len; i++) {
      final char c = input.charAt(i);
      if ((c == '/') && (prevChar == '/')) {
        continue;
      }
      if (c == '\u0000')
        throw new IllegalArgumentException("NUL character not allowed in path");
      sb.append(c);
      prevChar = c;
    }
    return sb.toString();
  }

  static JMemPath asJMemPath(final Path path) {
    if (!(path instanceof JMemPath))
      throw new ProviderMismatchException("Path provider mismatch: expected jmemfs path, was "
          + path.getFileSystem().provider().getScheme());
    return (JMemPath) path;
  }

}
