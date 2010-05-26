package org.sapia.util.license;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * A utility class.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class LicenseUtils {
  
  /**
   * Deserialized an object from the given bytes.
   * <p>
   * <b>Note:</b> the given stream is closed by this method.
   * @param bytes a byte array.
   * @return an <code>Object</code>.
   * @throws IOException if an IO problem occurs.
   */
  public static Object fromBytes(InputStream bytes) throws IOException{
    ObjectInputStream ois = new ObjectInputStream(bytes);
    try{
     return ois.readObject();
    }catch(ClassNotFoundException e){
      throw new IOException("Class not found - " + e.getMessage());
    }finally{
      if(ois != null)
        ois.close();
    }
  }
  
  /**
   * Serializes the given object to an array of bytes and returns the latter.
   * 
   * @param serializable an <code>Object</code>.
   * @return an array of bytes.
   * @throws IOException if an IO problem occurs.
   */
  public static byte[] toBytes(Object serializable) throws IOException{
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    toBytes(serializable, bos);
    return bos.toByteArray();
  }
  
  /**
   * Serializes the given object to the given stream.
   * <p>
   * <b>NOTE:</b>
   * This method closesd the given stream prior to returning. 
   * 
   * @param serializable an <code>Object</code>.
   * @param os an <code>OutputStream</code>.
   * @return an array of bytes.
   * @throws IOException if an IO problem occurs.
   */  
  public static void toBytes(Object serializable, OutputStream os) throws IOException{
    ObjectOutputStream oos = new ObjectOutputStream(os);
    try{
      oos.writeObject(serializable);
    }finally{
      oos.flush();
      oos.close();
    }
  }
}
