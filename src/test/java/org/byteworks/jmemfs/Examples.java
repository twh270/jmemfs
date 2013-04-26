package org.byteworks.jmemfs;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

public class Examples {
  static URI JMEM_ROOT_URI;

  static {
    try {
      JMEM_ROOT_URI = new URI("jmemfs:/");
    }
    catch (final URISyntaxException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void copyDataToFile() throws IOException {
    FileSystem fs = FileSystems.getFileSystem(JMEM_ROOT_URI);
    Path target = fs.getPath("/some_file");
    byte[] data = "This is some data".getBytes();
    Files.copy(new ByteArrayInputStream(data), target, StandardCopyOption.REPLACE_EXISTING);
    assertEquals(data, Files.readAllBytes(target));
  }

  @Test
  public void copyFilesTheOldSchoolWay() throws IOException {
    FileSystem fs = FileSystems.getFileSystem(JMEM_ROOT_URI);
    Path filePath = fs.getPath("/some_file");
    byte[] data = "This is some data".getBytes();
    Files.write(filePath, data, StandardOpenOption.CREATE);
    File sourceFile = filePath.toFile();
    Path destPath = fs.getPath("/another_file");
    File destFile = destPath.toFile();
    copyFile(sourceFile, destFile);
    assertEquals(data, Files.readAllBytes(destPath));
  }
  
  static void copyFile(File sourceFile, File destFile) throws IOException {
    if (!destFile.exists()) {
      destFile.createNewFile();
    }

    FileChannel source = null;
    FileChannel destination = null;

    try {
      source = new FileInputStream(sourceFile).getChannel();
      destination = new FileOutputStream(destFile).getChannel();
      destination.transferFrom(source, 0, source.size());
    }
    finally {
      if (source != null) {
        source.close();
      }
      if (destination != null) {
        destination.close();
      }
    }
  }
}
