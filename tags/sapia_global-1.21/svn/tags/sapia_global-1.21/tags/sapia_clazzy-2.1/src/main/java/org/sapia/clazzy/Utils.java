package org.sapia.clazzy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Holds misc. utilities.
 * 
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class Utils {

  static final int BUFSZ = 1096;

  /**
   * @param is
   *          an <code>InputStream</code>
   * @return the <code>byte</code> s of data contained in the stream.
   * @throws IOException
   *           if an IO problem occurs.
   */
  public static byte[] streamToBytes(InputStream is) throws IOException {
    int read = 0;
    byte[] buf = new byte[BUFSZ];
    ByteArrayOutputStream bos = new ByteArrayOutputStream(BUFSZ);
    while((read = is.read(buf)) > 0) {
      bos.write(buf, 0, read);
    }
    return bos.toByteArray();
  }
  
  /**
   * @param className
   * @return the name of package for the given class name, or null if
   * no package exists (class is part of default package).
   */
  public static String getPackageNameFor(String className){
    int i = className.lastIndexOf('.');
    if(i < 0){
      return null;
    }
    else{
      return className.substring(0, i);
    }
  }

}
