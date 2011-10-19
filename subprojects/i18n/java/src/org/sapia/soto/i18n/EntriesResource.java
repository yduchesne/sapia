/*
 * EntriesResource.java
 *
 * Created on June 3, 2005, 9:51 PM
 */

package org.sapia.soto.i18n;

import java.io.InputStream;
import java.util.Collection;
import java.util.Locale;

import org.sapia.soto.Env;
import org.sapia.resource.Resource;
import org.sapia.util.xml.confix.Dom4jProcessor;

/**
 *
 * @author yduchesne
 */
public class EntriesResource{
  
  private String _uri, _id;
  private long _lastModified;
  private Env _env;
  private LocalizedEntries _entries;
  private Resource _resource;
  
  /**
   * Creates a new instance of EntriesResource 
   */
  public EntriesResource() {
  }
  
  public void setUri(String uri){
    _uri = uri;
  }
  
  public void setId(String id){
    _id = id;
  }
  
  public String getId(){
    return _id;
  }
  
  public Entry lookup(String groupId, Locale locale) throws Exception{
    return entries().lookup(groupId, locale);
  }  
  
  public Collection getGroups() throws Exception{
    return entries().getGroups();
  }
  
  public void init(Env env) throws Exception{
    if(_uri == null){
      throw new IllegalStateException("Localization resource URI not set");
    }
    _env = env;
    _resource = _env.resolveResource(_uri);
    reload();
  }
  
  
  private LocalizedEntries entries() throws Exception{
    if(_lastModified != _resource.lastModified() || _entries == null){
      reload();
    }
    return _entries;
  }
  
  private synchronized void reload() throws Exception{
    if(_lastModified != _resource.lastModified() || _entries == null){
      Dom4jProcessor proc = new Dom4jProcessor(_env.getObjectFactory());
      InputStream is = _resource.getInputStream();
      LocalizedEntries entries = new LocalizedEntries();
      entries.setId(_id);
      try{
        proc.process(entries, is);
      }finally{
        is.close();
      }
      _entries = entries;
      _lastModified = _resource.lastModified();    
    }
  }
  
}
