/*
 * LangLevel.java
 *
 * Created on June 3, 2005, 8:43 PM
 */

package org.sapia.soto.i18n;

import gnu.trove.THashMap;

import java.util.Locale;

/**
 *
 * @author yduchesne
 */
class LangLevel {
  
  private THashMap _levels = new THashMap();
  private Entry _root;
  
  /** Creates a new instance of LangLevel */
  LangLevel() {
  }
  
  void bind(Entry entry){
    if(entry.getCountry() == null){
      _root = entry;
    }
    else{
      CountryLevel level = (CountryLevel)_levels.get(entry.getCountry());
      if(level == null){
        level = new CountryLevel();
        _levels.put(entry.getCountry(), level);
      }
      level.bind(entry);
    }
  }
  
  Entry lookup(Locale locale){
    CountryLevel level = (CountryLevel)_levels.get(locale.getCountry());
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
