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
      return new URI("jmemfs:" + path);
    }
    catch (final URISyntaxException e) {
      e.printStackTrace();
    }
    return null;
  }

}
