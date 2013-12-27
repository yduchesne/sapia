package org.sapia.util.unit;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class StorageUnit extends Unit{

  /**
   * The byte memory unit, identified by the <code>b</code> suffix.
   */  
  public static final Unit BYTE     = new Unit("b", 1);
  
  /**
   * The kilobyte memory unit, identified by the <code>kb</code> suffix.
   */  
  public static final Unit KILOBYTE = new Unit("kb", 1000 * BYTE._base);  
  
  /**
   * The megabyte memory unit, identified by the <code>mb</code> suffix.
   */    
  public static final Unit MEGABYTE = new Unit("mb", 1000 * KILOBYTE._base);
  
  /**
   * The gigabyte memory unit, identified by the <code>gb</code> suffix.
   */  
  public static final Unit GIGABYTE = new Unit("gb", 1000 * MEGABYTE._base);
  
  static final Set STORAGE_UNITS;
  
  static{
    Set units = new HashSet();
    units.add(BYTE);
    units.add(KILOBYTE);
    units.add(MEGABYTE);    
    units.add(GIGABYTE);
    STORAGE_UNITS = Collections.unmodifiableSet(units);
  }
  
  public static final StorageUnit PROTOTYPE = new StorageUnit(null, 0);
  
  public StorageUnit(String suffix, int base){
    super(suffix, base);
  }
  
  public Set units() {
    return STORAGE_UNITS;
  }
  
  
  
}
