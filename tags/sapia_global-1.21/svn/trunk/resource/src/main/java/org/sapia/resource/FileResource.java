package org.sapia.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Implements a <code>Resource</code> over a <code>File</code>
 * 
 * @see java.io.File
 * 
 * @author Yanick Duchesne
 */
public class FileResource implements Resource, URLCapable {
  private File _file;

  public FileResource(File f) {
    _file = f;
  }
  
  public String getURI() {
    try {
      return _file.toURL().toExternalForm();
    } catch(MalformedURLException e) {
      throw new IllegalStateException("Could not create URL from resource: "
          + _file.getAbsolutePath() + "; caught: " + e.getClass().getName()
          + " - " + e.getMessage());
    }
  }
  
  public URL getURL() throws IOException {
    return _file.toURL();
  }
  
  public Resource getRelative(String uri) throws IOException {
	  if(Utils.hasScheme(uri)){
	    if(Utils.isAbsolute(uri)){
	      throw new IOException("URI is absolute: " + uri + "; must be relative");
	    }
	  }
	  File relative;
	  if(_file != null){
	    relative = new File(Utils.getRelativePath(_file.getAbsolutePath(), uri, true));
	    if(!relative.exists()){
	      relative = new File(uri);
	    }
	  }
	  else{
	    relative = new File(uri);        
	  }
	  return new FileResource(relative);      
  }

  public InputStream getInputStream() throws IOException {
    return new FileInputStream(_file);
  }

  public long lastModified() {
    return _file.lastModified();
  }

  public String toString() {
    return _file.getAbsolutePath();
  }
}
