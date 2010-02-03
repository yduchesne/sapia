package org.sapia.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Resolves resources from the file system.
 * 
 * <pre>
 * FileResourceHandler handler = new FileResourceHandler();
 * 
 * // resolving a file URL
 * InputStream is = handler.getResource(&quot;file:/opt/some/file.txt&quot;);
 * 
 *  // resolving an absolute path
 *  InputStream is = handler.getResource(&quot;/opt/some/file.txt&quot;);
 * 
 *  // resolving a relative path (assuming current dir is /opt)
 *  InputStream is = handler.getResource(&quot;some/file.txt&quot;);
 * 
 *  
 * </pre>
 * 
 * @see org.sapia.resource.FileResource
 * 
 * @author Yanick Duchesne
 */
public class FileResourceHandler implements ResourceHandler, Schemes {

  private boolean _fallBackToClasspath = true;
  private String  _basedir = Utils.replaceWinFileSep(System.getProperty("user.dir"));

  public Resource getResourceObject(String uri) throws IOException {
    String fileUri = mangleUri(uri);
    File f = getFile(fileUri);
    
    if(f.exists()){
      return new FileResource(f);
    } else if (f.getAbsoluteFile().exists()) {
      return new FileResource(f.getAbsoluteFile());
    } else if(_fallBackToClasspath) {
      return new ClasspathResourceHandler().getResourceObject(uri);
    } else {
      throw new ResourceNotFoundException(uri);
    }
  }

  /**
   * @param uri
   *          a URI.
   * @return the <code>File</code> corresponding to the given URI.
   */
  public File getFile(String uri) throws IOException{
    return new File(uri);
  }

  public InputStream getResource(String uri) throws IOException {
    String fileUri = mangleUri(uri);
    File f = getFile(fileUri);

    if(f.exists()) {
      return f.toURL().openStream();
    } else if(_fallBackToClasspath) {
      return new ClasspathResourceHandler().getResourceObject(uri)
          .getInputStream();
    }
    else{
      throw new FileNotFoundException(uri);
    }
  }

  /**
   * @param fallback
   *          if <code>true</code>, this instance will resort to looking up
   *          the classpath if it cannot find a resource on the file system.
   */
  public void setFallBackToClasspath(boolean fallback) {
    _fallBackToClasspath = fallback;
  }

  /**
   * Sets the directory from which relative paths should be evaluated.
   * 
   * @param basedir
   *          the path to a directory
   */
  public void setBasedir(String basedir) {
    _basedir = basedir;
  }

  public boolean accepts(String uri) {
    return doAccepts(Utils.getScheme(uri));
  }
  
  public boolean accepts(URI uri) {
    return doAccepts(uri.getScheme());
  }
  
  private boolean doAccepts(String scheme){
    if(scheme == null || scheme.length() == 0 || scheme.equals(SCHEME_FILE)) {
      return true;
    }
    return false;    
  }

  String mangleUri(String uri) throws IOException{
    uri = Utils.replaceWinFileSep(uri);
    String scheme = Utils.getScheme(uri);
    if(scheme != null && Utils.isWindowsDrive(scheme)){
      return Utils.chopScheme(uri);
    }
    else if(scheme != null){
      uri = Utils.chopScheme(uri);
    }
    if(uri.startsWith("/") || Utils.isWindowsDrive(uri)){
      return uri;
    }
    else if(_basedir != null){
      return Utils.getRelativePath(_basedir, uri, false);      	
    }
    else{
      return uri;
    }
  }
}
