/*
 * CountryLevel.java
 *
 * Created on June 3, 2005, 8:34 PM
 */

package org.sapia.soto.i18n;

import gnu.trove.THashMap;

import java.util.Locale;

/**
 *
 * @author yduchesne
 */
class CountryLevel {
  
  private THashMap _variants = new THashMap();
  private Entry _root;
  
  /** Creates a new instance of CountryLevel */
  CountryLevel() {
  }
  
  void bind(Entry entry){
    if(entry.getVariant() == null){
      _root = entry;
    }
    else{
      _variants.put(entry.getVariant(), entry);
    }
  }  
  
  Entry lookup(Locale locale){
    Entry entry = (Entry)_variants.get(locale.getVariant());
    if(entry == null){
      return _root;
    }
    return entry;
  }    
  
  void compact(){
    _variants.compact();
  }  
  
}
