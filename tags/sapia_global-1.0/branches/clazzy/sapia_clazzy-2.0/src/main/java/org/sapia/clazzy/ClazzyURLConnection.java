package org.sapia.clazzy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


/**
 * An instance of this class deals with "clazzy" URLs, of the form: 
 * <pre>
 * clazzy:path_to_jar?file_name.
 * </pre>
 * <p>
 * An instance of this class in fact interacts with a <code>JarClassLoader</code>
 * to retrieve resources corresponding to the given URLs (respecting the above
 * format).
 * <p>
 * In order for the URLs handled by this class to work properly, the following
 * system property must be set:
 * <pre>
 * -Djava.protocol.handler.pkgs=org.sapia
 * </pre>
 *
 * @see org.sapia.clazzy.ClazzyURLStreamHandlerFactory
 * @see org.sapia.clazzy.loader.JarLoader
 * @see org.sapia.clazzy.JarClassLoader
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ClazzyURLConnection extends URLConnection{
  
  
  public ClazzyURLConnection(URL url) {
    super(url);
  }
  
  /**
   * @see java.net.URLConnection#connect()
   */
  public void connect() throws IOException {
  }
  
  /**
   * @see java.net.URLConnection#getInputStream()
   */
  public InputStream getInputStream() throws IOException {
    String path = getURL().toExternalForm();
    int i = path.indexOf("?");
    if(i < 0){
      throw new IOException("URL should have format: protocol://path_to_jar?file_name. Got: " + path);
    }
    JarClassLoader loader = new JarClassLoader(new File(getURL().getPath()));
    try{
      return loader.findResourceAsStream(path.substring(i+1));
    }finally{
      loader.close();
    }
  }
  
  /**
   * @see java.net.URLConnection#getOutputStream()
   */
  public OutputStream getOutputStream() throws IOException {
    return super.getOutputStream();
  }
  
  /**
   * @see java.net.URLConnection#getAllowUserInteraction()
   */
  public boolean getAllowUserInteraction() {
    return false;
  }
  
}
