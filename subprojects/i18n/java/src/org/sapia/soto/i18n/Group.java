/*
 * Group.java
 *
 * Created on June 3, 2005, 11:16 PM
 */

package org.sapia.soto.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 * An instance of this class holds a group of <code>Entry</code>
 * instances that each holds internationalized text. Entries are looked up
 * on a per-Locale basis.
 * 
 * @see Entry
 *
 * @author yduchesne
 */
public class Group implements ObjectCreationCallback{
  
  private List _entries = new ArrayList();
  private RootLevel _level = new RootLevel();
  private String _id;
  
  /** Creates a new instance of Group */
  public Group() {
  }

  /**
   * @param id this instance's ID.
   */
  public void setId(String id){
    if(_id != null)
      throw new IllegalArgumentException("Group ID already set");
    _id = id;
  }
  
  /**
   * @return this instance's ID.
   */  
  public String getId(){
    return _id;
  }  

  /**
   * Returns the entry corresponding to the given locale.
   *
   * @param locale a <code>Locale</code>
   * @return an <code>Entry</code>, or <code>null</code> if no entry exists
   * for the given locale.
   */
  public Entry lookup(Locale locale){
    return _level.lookup(locale);
  }

  public Entry createEntry(){
    Entry entry = new Entry();
    _entries.add(entry);
    return entry;
  }
  
  public Object onCreate() throws ConfigurationException{
    if(_id == null){
      throw new ConfigurationException("Localized entry group has no identifier");
    }
    for(int i = 0; i < _entries.size(); i++){
      Entry entry = (Entry)_entries.get(i);
      _level.bind(entry);
    }
    _entries = null;
    _level.compact();
    return this;
  }  
  
}
