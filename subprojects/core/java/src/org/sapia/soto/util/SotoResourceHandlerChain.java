package org.sapia.soto.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.sapia.resource.Resource;
import org.sapia.resource.ResourceHandler;
import org.sapia.resource.ResourceHandlerChain;
import org.sapia.resource.ResourceNotFoundException;
import org.sapia.soto.Debug;
import org.sapia.util.text.TemplateException;

/**
 * @author Yanick Duchesne
 * 
 */
public class SotoResourceHandlerChain extends ResourceHandlerChain{

  private List _aliases = new ArrayList();
  private Map  _aliasCache = new WeakHashMap();
  
  /**
   * @param alias a <code>ResourceAlias</code>
   */
  public synchronized void addResourceAlias(ResourceAlias alias){
    _aliases.add(alias);
    _aliasCache.clear();
  }
  
  public synchronized Resource resolveResource(String uri) 
    throws ResourceNotFoundException, IOException{
    Debug.debug(getClass(), "Attempting to find resource handler for " + uri);    
    AliasEntry entry = (AliasEntry)_aliasCache.get(uri);
    if(entry != null){
      if(entry.uri != null){
        uri = entry.uri;
      } 
    }
    else{
      uri = matchUri(uri);
    }
    
    ResourceHandler handler = doSelect(uri);
    if(handler == null){
      throw new ResourceNotFoundException("Could not find resource handler for: " + uri);
    }
    return handler.getResourceObject(uri);
  }

  /**
   * Selects a <code>ResourceHandler</code> from this instance and returns it.
   * The handler is chose based on the given URI. <p/>This instance traverses
   * its handlers, calling the <code>accepts()</code> method on them (the
   * method takes the given URI as a parameter). The first handler that
   * "accepts" the URI (by returning <code>true</code>) is returned to the
   * caller. If no handler as accepted the URI, <code>null</code> is returned.
   * 
   * @param uri
   *          a URI.
   * @return a <b>ResourceHandler </b>, or <code>null</code> if no handler
   *         could be found for the given URI.
   */
  public synchronized ResourceHandler select(String uri) {
    
    Debug.debug(getClass(), "Attempting to find resource handler for " + uri);    
    AliasEntry entry = (AliasEntry)_aliasCache.get(uri);
    if(entry != null){
      if(entry.uri != null){
        uri = entry.uri;
      } 
    }
    else{
      uri = matchUri(uri);
    }
    
    return doSelect(uri);
  }
  
  String matchUri(String uri){
    AliasEntry entry = (AliasEntry)_aliasCache.get(uri);
    if(entry != null){
      if(entry.uri != null){
        uri = entry.uri;
      } 
    }
    else{
      for(int i = 0; i < _aliases.size(); i++){
        ResourceAlias alias = (ResourceAlias)_aliases.get(i);
        try{
          String matched = alias.match(uri);
          Debug.debug(getClass(), "Resource alias matched " + uri + " to " + matched);
          entry = new AliasEntry();
          entry.uri = matched;
          _aliasCache.put(uri, entry);
          if(entry.uri != null){
            uri = entry.uri;
            break;
          }
        }catch(TemplateException e){
          // noop
        }
      }
    }
    return uri;
  }
  
  boolean isCached(String uri){
    return _aliasCache.get(uri) != null;
  }
  
  static final class AliasEntry{
    String uri;
  }
}
