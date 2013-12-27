/*
 * ServiceEnv.java
 *
 * Created on April 24, 2005, 10:53 AM
 */

package org.sapia.soto.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sapia.soto.Debug;
import org.sapia.soto.Env;
import org.sapia.soto.NotFoundException;
import org.sapia.soto.ServiceSelector;
import org.sapia.soto.Settings;
import org.sapia.soto.SotoConsts;
import org.sapia.resource.Resource;
import org.sapia.resource.ResourceHandler;
import org.sapia.resource.include.IncludeState;
import org.sapia.soto.util.SotoResourceHandlerChain;
import org.sapia.soto.util.Utils;
import org.sapia.util.text.MapContext;
import org.sapia.util.text.SystemContext;
import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectFactoryIF;

/**
 * An instance of this class is assigned to <code>EnvAware</code> instances.
 *
 * @author yduchesne
 */
public class ServiceEnv implements Env, SotoConsts{
  
  private Env _parent;
  private String _baseUri;
  private Map _objects = Collections.synchronizedMap(new HashMap());
  
  
  /** Creates a new instance of ServiceEnv */
  public ServiceEnv(Env parent, String baseUri) {
    _parent = parent;
    _baseUri = baseUri;
  }

  public Settings getSettings() {
    return _parent.getSettings();
  }

  public InputStream resolveStream(String uri) throws IOException {
    
    if(_baseUri != null && Utils.isRelativePath(uri)){
      try{
        String toResolve = Utils.getRelativePath(_baseUri, uri, true);
        if(Debug.DEBUG){
          Debug.debug(getClass(), "Resolving stream: " + toResolve);
        }        
        return _parent.getResourceHandlerFor(toResolve).getResourceObject(toResolve).getInputStream();
      }catch(IOException e){
        try{
          if(Debug.DEBUG){
            Debug.debug(getClass(), "Resolving stream from parent: " + uri);
          }                  
          return _parent.resolveStream(uri);
        }catch(IOException e2){
          throw e;
        }
      }
    }
    else{
      if(Debug.DEBUG){
        Debug.debug(getClass(), "URI is absolute; resolving stream from parent: " + uri);
      }                        
      return _parent.resolveStream(uri);      
    }
  }

  public Resource resolveResource(String uri) throws IOException {
    if(_baseUri != null && Utils.isRelativePath(uri)){
      try{
        String toResolve = Utils.getRelativePath(_baseUri, uri, true);
        if(Debug.DEBUG){
          Debug.debug(getClass(), "Resolving resource: " + toResolve);
        }        
        return _parent.getResourceHandlerFor(toResolve).getResourceObject(toResolve);
      }catch(IOException e){
        try{
          if(Debug.DEBUG){
            Debug.debug(getClass(), "Resolving resource from parent: " + uri);
          }                  
          return _parent.resolveResource(uri);
        }catch(IOException e2){
          throw e;
        }
      }
    }
    else{
      if(Debug.DEBUG){
        Debug.debug(getClass(), "URI is absolute; resolving resource from parent: " + uri);
      }                              
      return _parent.resolveResource(uri);      
    }    
  }

  public Object lookup(String serviceId) throws NotFoundException {
    return _parent.lookup(serviceId);
  }

  public ResourceHandler getResourceHandlerFor(String protocol) {
    return _parent.getResourceHandlerFor(protocol);
  }

  public Object lookup(Class instanceOf) throws NotFoundException {
    return _parent.lookup(instanceOf);
  }

  public List lookup(ServiceSelector selector, boolean returnMetadata) {
    return _parent.lookup(selector, returnMetadata);
  }

  public SotoResourceHandlerChain getResourceHandlers() {
    return _parent.getResourceHandlers();
  }

  public ObjectFactoryIF getObjectFactory() {
    return _parent.getObjectFactory();
  }
  
  public Object include(String uri, Map params) throws ConfigurationException{
    if(params == null) params = new HashMap();
    SotoIncludeContext ctx = (SotoIncludeContext)IncludeState.currentContext(SOTO_INCLUDE_KEY);
    TemplateContextIF templateCtx;
    if(ctx == null){
      templateCtx = new MapContext(params, new SystemContext(), false);
    }
    else{
      templateCtx = new MapContext(params, ctx.getTemplateContext(), false);  
    }
    return doInclude(uri, templateCtx);
  }  
  
  public Object include(String uri, TemplateContextIF context) throws ConfigurationException{
    return doInclude(uri, context);
  }      
  
  public Resource includeResource(String uri, Map params) throws ConfigurationException{
    if(params == null) params = new HashMap();
    SotoIncludeContext ctx = (SotoIncludeContext)IncludeState.currentContext(SOTO_INCLUDE_KEY);
    try{
      if(ctx == null){
        return this.resolveResource(uri);
      }
      else{
        return ctx.includeResource();
      }
    }catch(IOException e){
      throw new ConfigurationException("Problem including: " + uri, e);
    }catch(Exception e){
      if(e instanceof ConfigurationException){
        throw (ConfigurationException)e;
      }
      else{
        throw new ConfigurationException("Problem including: " + uri, e);        
      }
    }    
  }   
  
  public void bind(String id, Object o){
    _objects.put(id, o);
  }
  
  public Object resolveRef(String id) throws NotFoundException{
    Object o = _objects.get(id);
    if(o == null){
      if(_parent == null){
        throw new NotFoundException("Could not resolve reference: " + id);
      }
      return _parent.resolveRef(id);
    }
    return o;
  }
  
  private Object doInclude(String uri, TemplateContextIF templateCtx)
    throws ConfigurationException{
    SotoIncludeContext ctx = (SotoIncludeContext)IncludeState.currentContext(SOTO_INCLUDE_KEY);
    try{
      if(ctx == null){
        return IncludeState.createContext(uri, ctx.getConfig()).include(templateCtx);
      }
      else{
        return IncludeState.createContext(uri, ctx.getConfig()).include(templateCtx);  
      }
    }catch(IOException e){
      throw new ConfigurationException("Problem including: " + uri, e);
    }catch(Exception e){
      if(e instanceof ConfigurationException){
        throw (ConfigurationException)e;
      }
      else{
        throw new ConfigurationException("Problem including: " + uri, e);        
      }
    }
  }
  
}
