package org.sapia.cocoon.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.text.StrLookup;

/**
 * This class provides a chained lookup mechanism. An instance
 * supports adding multiple {@link StrLookup} instances that will
 * be searched in the order in which they were added.
 * 
 * @author yduchesne
 *
 */
public class ChainedStrLookup extends StrLookup {

  private List<StrLookup> _chain = new ArrayList<StrLookup>();

  /**
   * This method searched this instance's internal {@link StrLookup}
   * objects for the value corresponding to the given key. Search
   * stops at the first successful lookup.
   * 
   * @param key the key whose corresponding value should be returned.
   * @return the value matching the given key, or <code>null</code> if
   * no such value could be found.
   */
  public String lookup(String key) {
    for(StrLookup lookup:_chain){
      String str = lookup.lookup(key);
      if(str != null){
        return str;
      }
    }
    return null;
  }

  public ChainedStrLookup add(StrLookup lookup) {
    _chain.add(lookup);
    return this;
  }

}
