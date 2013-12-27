package org.sapia.resource.include;

import java.io.IOException;
import java.io.InputStream;

import org.sapia.resource.Resource;
import org.sapia.resource.ResourceNotFoundException;
import org.sapia.resource.Utils;

/**
 * An instance of this class encapsulates state pertaining to a single resource-include. It is 
 * created by an application-specific {@link org.sapia.resource.include.IncludeContextFactory}.
 * 
 * <p>
 * This class is meant to be inherited. It's {@link #doInclude(InputStream, Object)} method will
 * typically be overridden in order to implement application-specific resource processing behavior (see the
 * {@link #include()} method for more information.
 * 
 * @see org.sapia.resource.include.IncludeContextFactory
 * 
 * @author yduchesne
 *
 */
public class IncludeContext {
 
  private IncludeContext _parent;
  private String _uri;
  private IncludeConfig _conf;
  private Resource _resource;
  
  void setParent(IncludeContext parent){
    _parent = parent;
  }
  
  void setUri(String uri){
    _uri = uri;
  }
  
  void setConfig(IncludeConfig conf){
    _conf = conf;
  }
  
  public IncludeConfig getConfig(){
    return _conf;
  }
  
  /**
   * @return the parent <code>IncludeContext</code> of this instance, or <code>null</code>
   * if this context correspond to a "root" resource-include.
   */
  public IncludeContext getParent(){
    return _parent;
  }
  
  /**
   * @return the URI of the resource that this instance includes (as specified by the application).
   */
  public String getUri(){
    return _uri;
  }
  
  /**
   * @return the absolute, canonical URI of the resource that this instance includes.
   * @throws ResourceNotFoundException
   * @throws IOException
   */
  public String getCanonicalUri() throws ResourceNotFoundException, IOException, Exception{
    return resolve().getURI();
  }
  
  /**
   * This method resolves the stream corresponding to this instance's URI and internally
   * calls the {@link #doInclude(InputStream, Object)} method of this instance, passing it
   * the stream that was resolved - the called method is expected to return an object corresponding
   * to the given stream. This method then pops this instance from the include stack
   * prior to returning the included object.
   * 
   * @return the <code>Object</code> that was "included" by the {@link #doInclude(InputStream, Object)}
   * template method that is called by this method.
   * 
   * @throws ResourceNotFoundException if no resource was found for this instance's URI.
   * @throws IOException if an IO problem occurred while performing this operation.
   * @throws Exception if an undetermined problem occurred while performing this operation.
   */
  public Object include() throws ResourceNotFoundException, IOException, Exception{
    InputStream is = null;
    try{
      Resource res = resolve();
      is = res.getInputStream();
      Object obj = doInclude(is, null);
      pop();
      return obj;
    }finally{
      if(is != null)
        is.close();

    }
  }
  
  /**
   * This method takes an arbitrary, application-specific context object. This is up to classes inheriting
   * from this class to handler that object in a consistent manner.
   * 
   * @see #include() 
   * @param context an arbitrary context object.
   */
  public Object include(Object context) throws ResourceNotFoundException, IOException, Exception{
    InputStream is = null;
    try{
      Resource res = resolve();
      is = res.getInputStream();
      Object obj = doInclude(is, context);
      pop();
      return obj;
    }finally{
      if(is != null)
        is.close();
    }
  }  

  /**
   * This method this instance from the include stack prior to returning 
   * the included stream.
   * 
   * @return the <code>InputStream</code> that this instance includes. 
   * 
   * @throws ResourceNotFoundException if no resource was found for this instance's URI.
   * @throws IOException if an IO problem occurred while performing this operation.
   * @throws Exception if an undetermined problem occurred while performing this operation.
   */
  public InputStream includeStream() throws ResourceNotFoundException, IOException, Exception{
    Resource res = resolve();
    InputStream is = res.getInputStream();
    pop();
    return is;
  }
  
    
  /**
   * This method this instance from the include stack prior to returning 
   * the included resource.
   * 
   * @return the <code>Resource</code> that this instance includes. 
   * 
   * @throws ResourceNotFoundException if no resource was found for this instance's URI.
   * @throws IOException if an IO problem occurred while performing this operation.
   * @throws Exception if an undetermined problem occurred while performing this operation.
   */  
  public Resource includeResource() throws ResourceNotFoundException, IOException, Exception{
    Resource res = resolve();
    pop();
    return res;
  }   
  
  /**
   * @return this instance's <code>Resource</code>. 
   * 
   * @throws ResourceNotFoundException if no resource was found for this instance's URI.
   * @throws IOException if an IO problem occurred while performing this operation.
   * @throws Exception if an undetermined problem occurred while performing this operation.
   */  
  public Resource getResource() throws ResourceNotFoundException, IOException, Exception{
    return resolve();
  }
  
  /**
   * This method attempts returning a <code>Resource</code> that is relative to this instance's
   * <code>Resource</code>. If the given URI is not relative, this instance will attempt resolving 
   * it as an absolute resource. 
   * 
   * @param uri a relative or absolute uri.
   * @return a <code>Resource</code>
   * 
   * @throws ResourceNotFoundException if no resource was found for the given URI.
   * @throws IOException if an IO problem occurred while performing this operation.
   * @throws Exception if an undetermined problem occurred while performing this operation.
   */
  public Resource getRelative(String uri) throws ResourceNotFoundException, IOException, Exception{
      if(_uri == null){
        return _conf.getResources().resolveResource(uri);
      }
      if(Utils.isAbsolute(uri)){
      	return _conf.getResources().resolveResource(uri);        
      }
      uri = Utils.chopScheme(uri);
      return resolve().getRelative(uri);        
  }
  
  /**
   * This method's default implementation returns the passed in stream. It is meant to be 
   * overridden if this default implementation does not correspond to the desired result. 
   * 
   * @param is the <code>InputStream</code> that was resolved.
   * @param context an application-specific context object (<code>null</code> if no such object was
   * passed to the calling <code>include()</code> method).
   * @throws IOException
   * @throws Exception
   * 
   * @see #include()
   * @see #include(Object)
   */
  protected Object doInclude(InputStream is, Object context) throws IOException, Exception{
    return is;
  }
  
  Resource resolve() throws ResourceNotFoundException, IOException, Exception{
    if(_resource != null){
      return _resource;
    }
    if(_parent != null && _parent._uri != null){
      Resource res = _parent.resolve();
      if(Utils.isAbsolute(uri())){
       _resource = _conf.getResources().resolveResource(uri());        
      }
      else{
        _resource = res.getRelative(uri());        
      }
    }
    else{
      _resource = _conf.getResources().resolveResource(uri());
    }
    return _resource;
  }
  
  private void pop(){
    IncludeState.popContext(_conf.getAppKey());
  }
  
  private String uri(){
    return _uri;
  }
  
  public String toString(){
    return new StringBuffer("[")
      .append("uri=").append(_uri)
      .append("]").toString();
  }
}
