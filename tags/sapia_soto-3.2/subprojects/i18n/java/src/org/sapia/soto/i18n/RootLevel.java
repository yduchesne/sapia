/*
 * RootLevel.java
 *
 * Created on June 3, 2005, 8:33 PM
 */

package org.sapia.soto.i18n;

import gnu.trove.THashMap;

import java.util.Locale;

/**
 *
 * @author yduchesne
 */
class RootLevel {
  
  private THashMap _levels = new THashMap();
  private Entry _root;
  
  /**
   * Creates a new instance of RootLevel 
   */
  RootLevel() {
  }
  
  void bind(Entry entry){
    if(entry.getLanguage() == null){
      _root = entry;
    }
    else{
      LangLevel level = (LangLevel)_levels.get(entry.getLanguage());
      if(level == null){
        level = new LangLevel();
        _levels.put(entry.getLanguage(), level);
      }
      level.bind(entry);
    }
  }
  
  Entry lookup(Locale locale){
    LangLevel level = (LangLevel)_levels.get(locale.getLanguage());
    if(level == null){
      return _root;
    }
    Entry entry = level.lookup(locale);
    return entry == null ? _root : entry;    
  }
  
  void compact(){
    _levels.compact();
  }
  
}
