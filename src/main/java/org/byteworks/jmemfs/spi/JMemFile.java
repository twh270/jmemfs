package org.byteworks.jmemfs.spi;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;

public class JMemFile extends File {
  private static final long serialVersionUID = 4978199407455017266L;
  private JMemPath filePath;
  private transient String path;
  private transient JMemFileSystem fs;
  private transient int prefixLength;

  public JMemFile(JMemPath path) {
    super(path.toString());
    this.filePath = path;
    this.fs = (JMemFileSystem) filePath.getFileSystem();
    this.prefixLength = path.startsWith("/") ? 1 : 0;
  }

  /**
   * Returns the length of this abstract pathname's prefix. For use by
   * FileSystem classes.
   */
  public int getPrefixLength() {
    return prefixLength;
  }

  @Override
  public String getName() {
    return filePath.getFileName().toString();
  }

  @Override
  public String getParent() {
    return filePath.getParent().toString();
  }

  @Override
  public File getParentFile() {
    return filePath.getParent().toFile();
  }

  @Override
  public String getPath() {
    return path;
  }

  @Override
  public boolean isAbsolute() {
    return filePath.isAbsolute();
  }

  @Override
  public String getAbsolutePath() {
    return filePath.toAbsolutePath().toString();
  }

  @Override
  public File getAbsoluteFile() {
    return filePath.toAbsolutePath().toFile();
  }

  @Override
  public String getCanonicalPath() throws IOException {
    return filePath.toRealPath().toString();
  }

  @Override
  public File getCanonicalFile() throws IOException {
    return filePath.toRealPath().toFile();
  }

  @Deprecated
  @Override
  public URL toURL() throws MalformedURLException {
    return new URL("file", "", slashify(getAbsolutePath(), isDirectory()));
  }

  @Override
  public URI toURI() {
    try {
      File f = getAbsoluteFile();
      String sp = slashify(f.getPath(), f.isDirectory());
      if (sp.startsWith("//"))
        sp = "//" + sp;
      return new URI("file", null, sp, null);
    }
    catch (URISyntaxException x) {
      throw new Error(x); // Can't happen
    }
  }

  @Override
  public boolean canRead() {
    return Files.isReadable(filePath);
  }

  @Override
  public boolean canWrite() {
    return Files.isWritable(filePath);
  }

  @Override
  public boolean exists() {
    return Files.exists(filePath);
  }

  @Override
  public boolean isDirectory() {
    return Files.isDirectory(filePath);
  }

  @Override
  public boolean isFile() {
    return Files.isRegularFile(filePath);
  }

  @Override
  public boolean isHidden() {
    try {
      return Files.isHidden(filePath);
    }
    catch (IOException e) {
      throw new RuntimeException("Error determining whether file is hidden: " + path, e);
    }
  }

  @Override
  public long lastModified() {
    try {
      return Files.getLastModifiedTime(filePath).toMillis();
    }
    catch (IOException e) {
      throw new RuntimeException("Error determining file's last modified time: " + path, e);
    }
  }

  @Override
  public long length() {
    try {
      return Files.size(filePath);
    }
    catch (IOException e) {
      throw new RuntimeException("Error determining file's size: " + path, e);
    }
  }

  @Override
  public boolean createNewFile() throws IOException {
    Files.createFile(filePath);
    return true;
  }

  @Override
  public boolean delete() {
    try {
      Files.delete(filePath);
      return true;
    }
    catch (IOException e) {
      throw new RuntimeException("Could not delete file: " + path, e);
    }
  }

  @Override
  public void deleteOnExit() {
    // do nothing...memory-based files are always deleted on VM termination
  }

  @Override
  public String[] list() {
    return filePath.getPathElements();
  }

  @Override
  public String[] list(FilenameFilter filter) {
    String names[] = list();
    if ((names == null) || (filter == null)) {
      return names;
    }
    List<String> v = new ArrayList<>();
    for (int i = 0; i < names.length; i++) {
      if (filter.accept(this, names[i])) {
        v.add(names[i]);
      }
    }
    return v.toArray(new String[v.size()]);
  }

  @Override
  public File[] listFiles() {
    String[] ss = list();
    if (ss == null)
      return null;
    int n = ss.length;
    File[] files = new File[n];
    for (int i = 0; i < n; i++) {
      files[i] = new JMemFile(new JMemPath(fs, ss[i]));
    }
    return files;
  }

  @Override
  public File[] listFiles(FilenameFilter filter) {
    String ss[] = list();
    if (ss == null)
      return null;
    ArrayList<File> files = new ArrayList<>();
    for (String s : ss)
      if ((filter == null) || filter.accept(this, s))
        files.add(new JMemFile(new JMemPath(fs, s)));
    return files.toArray(new File[files.size()]);
  }

  @Override
  public File[] listFiles(FileFilter filter) {
    String ss[] = list();
    if (ss == null)
      return null;
    ArrayList<File> files = new ArrayList<>();
    for (String s : ss) {
      JMemFile f = new JMemFile(new JMemPath(fs, s));
      if ((filter == null) || filter.accept(f))
        files.add(f);
    }
    return files.toArray(new File[files.size()]);
  }

  @Override
  public boolean mkdir() {
    try {
      Files.createDirectory(filePath);
      return true;
    }
    catch (IOException e) {
      throw new RuntimeException("Could not create directory specified by file: " + path, e);
    }
  }

  @Override
  public boolean mkdirs() {
    try {
      Files.createDirectories(filePath);
      return true;
    }
    catch (IOException e) {
      throw new RuntimeException("Could not create directories specified by file: " + path, e);
    }
  }

  @Override
  public boolean renameTo(File dest) {
    try {
      Files.move(filePath, new JMemPath(fs, dest.toString()));
      return true;
    }
    catch (IOException e) {
      throw new RuntimeException("Could not move file from " + path + " to " + dest.toString(), e);
    }
  }

  @Override
  public boolean setLastModified(long time) {
    try {
      Files.setLastModifiedTime(filePath, FileTime.fromMillis(time));
      return true;
    }
    catch (IOException e) {
      throw new RuntimeException("Could not set last modified time for file " + path, e);
    }
  }

  @Override
  public boolean setReadOnly() {
    throw new UnsupportedOperationException("setReadOnly not implemented");
  }

  @Override
  public boolean setWritable(boolean writable, boolean ownerOnly) {
    throw new UnsupportedOperationException("setWritable not implemented");
  }

  @Override
  public boolean setWritable(boolean writable) {
    throw new UnsupportedOperationException("setWritable not implemented");
  }

  @Override
  public boolean setReadable(boolean readable, boolean ownerOnly) {
    throw new UnsupportedOperationException("setReadable not implemented");
  }

  @Override
  public boolean setReadable(boolean readable) {
    throw new UnsupportedOperationException("setReadable not implemented");
  }

  @Override
  public boolean setExecutable(boolean executable, boolean ownerOnly) {
    throw new UnsupportedOperationException("setExecutable not implemented");
  }

  @Override
  public boolean setExecutable(boolean executable) {
    throw new UnsupportedOperationException("setExecutable not implemented");
  }

  @Override
  public boolean canExecute() {
    return Files.isExecutable(filePath);
  }

  @Override
  public long getTotalSpace() {
    throw new UnsupportedOperationException("Cannot get total space for jmemfs filesystem");
  }

  @Override
  public long getFreeSpace() {
    throw new UnsupportedOperationException("Cannot get free space for jmemfs filesystem");
  }

  @Override
  public long getUsableSpace() {
    throw new UnsupportedOperationException("Cannot get usable space for jmemfs filesystem");
  }

  @Override
  public int compareTo(File pathname) {
    return filePath.compareTo(new JMemPath(fs, pathname.toString()));
  }

  @Override
  public boolean equals(Object obj) {
    if ((obj != null) && (obj instanceof JMemFile)) {
      return compareTo((File) obj) == 0;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return filePath.hashCode();
  }

  @Override
  public String toString() {
    return getPath();
  }

  private static String slashify(String path, boolean isDirectory) {
    String p = path;
    if (File.separatorChar != '/')
      p = p.replace(File.separatorChar, '/');
    if (!p.startsWith("/"))
      p = "/" + p;
    if (!p.endsWith("/") && isDirectory)
      p = p + "/";
    return p;
  }

  public Path toPath() {
    return filePath;
  }

}
