package org.byteworks.jmemfs.spi;

import java.net.URI;
import java.net.URISyntaxException;

public class TestCommon {
  public static URI JMEM_ROOT;
  public static URI BAD_URI;

  static {
    try {
      JMEM_ROOT = new URI("jmemfs:/");
      BAD_URI = new URI("notjmemfs:/");
    }
    catch (final URISyntaxException e) {
      e.printStackTrace();
    }
  }

  public static URI JMEM_URI(final String path) {
    try {
      return new URI("jmemfs://" + path);
    }
    catch (final URISyntaxException e) {
      e.printStackTrace();
    }
    return null;
  }

}
