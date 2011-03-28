package org.sapia.soto.state.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import org.sapia.soto.Debug;

import org.sapia.resource.FileResource;
import org.sapia.resource.FileResourceHandler;
import org.sapia.resource.Resource;
import org.sapia.resource.ResourceHandler;
import org.sapia.soto.util.Utils;

/**
 * Implements a <code>ResourceHandler</code> over a
 * <code>ServletContext</code>. Internally uses the
 * <code>getRealPath()</code> method on the context to resolve resources.
 * Delegates to superclass in case of failure.
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
public class ServletResourceHandler extends FileResourceHandler implements
    ResourceHandler {

  private static final String WEBINF = "/WEB-INF";
  private ServletContext _ctx;
  
  public ServletResourceHandler(ServletContext ctx) {
    _ctx = ctx;
  }

  /**
   * @see org.sapia.soto.util.ResourceHandler#accepts(java.lang.String)
   */
  public boolean accepts(String uri) {
    if(Debug.DEBUG){
      Debug.debug(getClass(), "Got URI: " + uri);
    }    
    if(Utils.hasScheme(uri)) {
      if(Debug.DEBUG){
        Debug.debug(getClass(), "Cannot handle: " + uri);
      }
      return false;
    } else {
      if(Debug.DEBUG){
        Debug.debug(getClass(), "Can handle: " + uri);
      }      
      String path = _ctx.getRealPath(process(uri));
      if(path == null  || !new File(path).exists()) {
        path = _ctx.getRealPath(new StringBuffer(WEBINF).append(process(uri)).toString());
      }
      else{
        return true;
      }
      if(Debug.DEBUG){
        Debug.debug(getClass(), "Real path: " + path);
      }         
      if(path == null || !new File(path).exists()) {      
        return false;
      }
      return true;
    }
  }

  /**
   * @see org.sapia.soto.util.ResourceHandler#getResource(java.lang.String)
   */
  public InputStream getResource(String uri) throws IOException {
    if(Debug.DEBUG){
      Debug.debug(getClass(), "Getting resource: " + uri);
    }
    String path = _ctx.getRealPath(process(uri));
    if(path == null  || !new File(path).exists()) {
      path = _ctx.getRealPath(new StringBuffer(WEBINF).append(process(uri)).toString());
    }      
    if(Debug.DEBUG){
      Debug.debug(getClass(), "Real path: " + path);
    }    
    if(path == null || !new File(path).exists()) {
      return super.getResource(uri);
    }
    return new FileInputStream(path);
  }

  /**
   * @see org.sapia.soto.util.ResourceHandler#getResourceObject(java.lang.String)
   */
  public Resource getResourceObject(String uri) throws IOException {
    if(Debug.DEBUG){
      Debug.debug(getClass(), "Getting resource object: " + uri);
    }    
    String path = _ctx.getRealPath(process(uri));
    if(path == null || !new File(path).exists()) {
      String res = new StringBuffer(WEBINF).append(process(uri)).toString();
      if(Debug.DEBUG){
        Debug.debug(getClass(), "Could not find real path for: " + uri + "; trying: " + res);
      }          
      path = _ctx.getRealPath(res);
    }  
    if(Debug.DEBUG){
      Debug.debug(getClass(), "Real path: " + path);
    }    
    if(path == null || !new File(path).exists()){
      if(Debug.DEBUG){
        Debug.debug("Resolving resource " + uri + " with superclass");
      }
      return super.getResourceObject(uri);
    }
    if(Debug.DEBUG){
      Debug.debug("Got resource " + path);
    }    
    return new FileResource(new File(path));
  }
  
  private String process(String uri){
    if(uri.charAt(0) != '/'){
      return new StringBuffer().append("/").append(uri).toString();
    }
    return uri;
  }

}
