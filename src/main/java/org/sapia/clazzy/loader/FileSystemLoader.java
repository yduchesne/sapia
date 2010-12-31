package org.sapia.clazzy.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.sapia.clazzy.URLEnabled;
import org.sapia.clazzy.Utils;

/**
 * Implements the <code>Loader</code> interface over a file directory.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class FileSystemLoader implements Loader, URLEnabled{
  
  private File _baseDir;
  
  public FileSystemLoader(File baseDir){
    _baseDir = baseDir;
  }
  
  public File getBaseDir(){
    return _baseDir;
  }
  
  /**
   * @return the <code>URL</code> of the <code>File</code> representing the
   *         base directory to which this instance corresponds.
   * 
   * @throws MalformedURLException
   */
  public URL getURL() throws MalformedURLException {
    return _baseDir.toURL();
  }  
  
  /**
   * @see org.sapia.clazzy.loader.Loader#getURL(java.lang.String)
   */
  public URL getURL(String resourceName) {
    try{
      File toReturn = new File(_baseDir, resourceName);
      if(toReturn.exists() && toReturn.isFile()){
        return toReturn.toURL();
      }
      return null;
    }catch(MalformedURLException e){
      return null;
    }
  }
  
  /**
   * @see org.sapia.clazzy.loader.Loader#loadBytes(java.lang.String)
   */
  public byte[] loadBytes(String resourceName){
    File toLoad = new File(_baseDir, resourceName);
    if(!toLoad.exists()){
      return null;
    }
    InputStream byteStream = null;
    try {
      byteStream = new FileInputStream(toLoad);
      return Utils.streamToBytes(byteStream);
    } catch(IOException e) {
      return null;
    } finally {
      if(byteStream != null) {
        try {
          byteStream.close();
        } catch(IOException e) {
        }
      }
    }
  }
  
  /**
   * @see org.sapia.clazzy.loader.Loader#close()
   */
  public void close() {
  }
}
