package org.sapia.util.unit;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DistanceUnit extends Unit{

  /**
   * The millimeter distance unit, identified by the <code>cm</code> suffix.
   */  
  public static final Unit MILLIMETER = new Unit("mm", 1);
  
  /**
   * The centimeter distance unit, identified by the <code>cm</code> suffix.
   */  
  public static final Unit CENTIMETER = new Unit("cm", 10);
  
  /**
   * The meter distance unit, identified by the <code>m</code> suffix.
   */  
  public static final Unit METER = new Unit("m", 100);
  
  /**
   * The kilometer distance unit, identified by the <code>km</code> suffix.
   */    
  public static final Unit KILOMETER = new Unit("km", 1000);
  
  static final Set DISTANCE_UNITS;
  
  static{
    Set units = new HashSet();
    units.add(MILLIMETER);
    units.add(CENTIMETER);
    units.add(METER);
    units.add(KILOMETER);
    DISTANCE_UNITS = Collections.unmodifiableSet(units);
  }
  
  public static final DistanceUnit PROTOTYPE = new DistanceUnit(null, 0);
  
  public DistanceUnit(String suffix, int base){
    super(suffix, base);
  }
  
  public Set units() {
    return DISTANCE_UNITS;
  }
  
  
  
}
