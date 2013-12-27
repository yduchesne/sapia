package org.sapia.clazzy.loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.sapia.clazzy.ClazzyURLStreamHandlerFactory;
import org.sapia.clazzy.URLEnabled;
import org.sapia.clazzy.Utils;

/**
 * Implements the <code>Loader</code> interface over a jar file. The underlying jar file is opened at
 * instantiation time. An instance of this class should be disposed of appropriately (through the 
 * <code>close()</code> method), so that the underlying jar file is properly closed.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class JarLoader implements Loader, URLEnabled{
  
  private File _jar;
  private JarFile _file;

  public JarLoader(File jar) {
    _jar = jar;
    try{
      _file = new JarFile(jar);
    } catch (IOException e){
      //noop;
    }
  }
  
  public File getJarFile(){
    return _jar;
  }
  
  /**
   * @return the <code>URL</code> of the <code>File</code> to which this
   *         instance corresponds.
   * 
   * @throws MalformedURLException
   */
  public URL getURL() throws MalformedURLException {
    return _jar.toURL();
  }
  
  /**
   * @see org.sapia.clazzy.loader.Loader#getURL(java.lang.String)
   */
  public URL getURL(String resourceName) {
    if(_file == null){
      return null;
    }
    ZipEntry entry = _file.getEntry(resourceName);
    if(entry == null){
      return null;
    }
    try{
      return new URL(ClazzyURLStreamHandlerFactory.PROTOCOL, "", _jar.getAbsolutePath()+"?"+resourceName);
    }catch(MalformedURLException e){
      return null;
    }
  }
  
  /**
   * @see org.sapia.clazzy.loader.Loader#loadBytes(java.lang.String)
   */
  public byte[] loadBytes(String resourceName) {
    if(_file == null){
      return null;
    }
    InputStream entryStream = null;
    try {
      ZipEntry entry = _file.getEntry(resourceName);
      if(entry == null) {
        return null;
      }
      entryStream = _file.getInputStream(entry);
      return Utils.streamToBytes(entryStream);
    } catch (IOException e){
      return null;
    } finally {
      if(entryStream != null) {
        try {
          entryStream.close();
        } catch(IOException e) {
        }
      }
    }    
  }

  /**
   * @see org.sapia.clazzy.loader.Loader#close()
   */
  public void close(){
    if(_file != null){
      try{
        _file.close();
      }catch(IOException e){
        //noop
      }
    }
  }

}
