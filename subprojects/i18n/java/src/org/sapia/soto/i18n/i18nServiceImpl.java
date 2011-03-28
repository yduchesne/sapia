/*
 * i18nServiceImpl.java
 *
 * Created on June 3, 2005, 9:45 PM
 */

package org.sapia.soto.i18n;

import gnu.trove.THashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.Service;
import org.sapia.util.CompositeRuntimeException;

/**
 * Implements the <code>i18nService</code> interface. Allows for configuring 
 * localized entries through XML.
 * <p>
 * An instance of this class encapsulates <code>EntriesResource</code> instances
 * to which it delegates lookups of internationalized entries.
 * 
 * @author yduchesne
 */
public class i18nServiceImpl implements i18nService, Service, EnvAware{
  
  private static final List EMPTY_LIST = new ArrayList(0);
  private THashMap _entryMap = new THashMap();
  private List _entries = new ArrayList();
  private Env _env;
  private static final String NO_TEXT = "NO TEXT FOUND";
  
  /**
   * Creates a new instance of i18nServiceImpl 
   */
  public i18nServiceImpl() {
  }

  public String getText(String id, String groupId, java.util.Map params, Locale locale) {
    EntriesResource res = (EntriesResource)_entryMap.get(id);
    if(res == null){
      return NO_TEXT + ": " + id + " - " + groupId + "; locale: " + locale;
    }
    try{
      Entry entry = res.lookup(groupId, locale);
      if(entry == null){
        return NO_TEXT + ": " + id + " - " + groupId + "; locale: " + locale;      
      }
      return entry.getText(params);
    }catch(Exception e){
      throw new CompositeRuntimeException("Could not find localized text for: " + id, e);
    }
  }

  public String getText(String id, String groupId, java.util.Locale locale) {
    EntriesResource res = (EntriesResource)_entryMap.get(id);
    if(res == null){
      return NO_TEXT + ": " + id + " - " + groupId + "; locale: " + locale;
    }
    try{
      Entry entry = res.lookup(groupId, locale);
      if(entry == null){
        return NO_TEXT + ": " + id + " - " + groupId+ "; locale: " + locale;      
      }
      return entry.getText();    
    }catch(Exception e){
      throw new CompositeRuntimeException("Could not find localized text for: " + id + ":" + groupId, e);
    }
  }
  
  public Collection getGroups(String id){
    EntriesResource res = (EntriesResource)_entryMap.get(id);
    if(res == null){
      return EMPTY_LIST;
    }    
    try{
      return res.getGroups();
    }catch(Exception e){
      if(e instanceof RuntimeException){
        throw (RuntimeException)e;
      }
      throw new CompositeRuntimeException("Could not get groups for: " + id, e);
    }
  }

  public void init() throws Exception {
    for(int i = 0; i < _entries.size(); i++){
      EntriesResource entries = (EntriesResource)_entries.get(i);
      if(entries.getId() == null){
        throw new IllegalStateException("Localized entry set has no identifier");
      }
      entries.init(_env); 
      if(_entryMap.get(entries.getId()) != null){
        throw new IllegalStateException("Cannot accept localized entry set " +
          "with already existing identifier: " + entries.getId());
      }
      _entryMap.put(entries.getId(), entries);
    }
    _entries = null;
  }
  
  public void start() throws Exception {
  }
  
  
  public void dispose() {
  }
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public EntriesResource createEntries(){
    EntriesResource entries = new EntriesResource();
    _entries.add(entries);
    return entries;
  }
  
}
