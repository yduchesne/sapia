/*
 * LocalizedEntries.java
 *
 * Created on June 3, 2005, 7:55 PM
 */

package org.sapia.soto.i18n;

import gnu.trove.THashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 *
 * @author yduchesne
 */
public class LocalizedEntries implements ObjectCreationCallback{
  
  private String _id;
  private THashMap _groupsById = new THashMap();
  private ArrayList _groups = new ArrayList();
  
  /** Creates a new instance of LocalizedEntries */
  public LocalizedEntries() {
  }
  
  public void setId(String id){
    _id = id;
  }
  
  public String getId(){
    return _id;
  }
  
  public Group createGroup(){
    Group g = new Group();
    _groups.add(g);
    return g;
  }
  
  public Entry lookup(String groupId, Locale locale){
    Group g = (Group)_groupsById.get(groupId);
    return g == null ? null : g.lookup(locale);
  }
  
  public Collection getGroups(){
    return Collections.unmodifiableCollection(_groups);
  }
  
  public LocalizedEntries createEntries(){
    return this;
  }
  
  public Object onCreate() throws ConfigurationException{
    if(_id == null){
      throw new ConfigurationException("Localized entry set has no identifier");
    }
    for(int i = 0; i < _groups.size(); i++){
      Group group = (Group)_groups.get(i);
      _groupsById.put(group.getId(), group);
    }
    _groups.trimToSize();
    _groupsById.compact();
    return this;
  }    
  
}
