package org.sapia.util.unit;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TimeUnit extends Unit{

  /**
   * The millisecond time unit, identified by the <code>ms</code> suffix.
   */
  public static final Unit MILLIS = new TimeUnit("ms", 1);    
  
  /**
   * The second time unit, identified by the <code>ss</code> suffix.
   */  
  public static final Unit SECOND = new TimeUnit("s", 1000 * MILLIS._base);
  
  /**
   * The minute time unit, identified by the <code>m</code> suffix.
   */  
  public static final Unit MINUTE = new TimeUnit("m", 60 * SECOND._base);
  
  /**
   * The hour time unit, identified by the <code>h</code> suffix.
   */  
  public static final Unit HOUR   = new TimeUnit("h", 60 * MINUTE._base);
  
  /**
   * The day time unit, identified by the <code>D</code> suffix.
   */
  public static final Unit DAY    = new TimeUnit("D", 24 * HOUR._base);
  
  /**
   * The month time unit, identified by the <code>M</code> suffix.
   */
  public static final Unit MONTH  = new TimeUnit("M", 30 * DAY._base);
  
  /**
   * The year time unit, identified by the <code>Y</code> suffix.
   */  
  public static final Unit YEAR   = new TimeUnit("Y", 365 * DAY._base);
  
  static final Set TIME_UNITS;
  
  static{
    Set units = new HashSet();
    units.add(MILLIS);
    units.add(SECOND);
    units.add(MINUTE);    
    units.add(HOUR);
    units.add(DAY);
    units.add(MONTH);
    units.add(YEAR);    
    TIME_UNITS = Collections.unmodifiableSet(units);
  }
  
  public static final TimeUnit PROTOTYPE = new TimeUnit(null, 0);
  
  public TimeUnit(String suffix, int base){
    super(suffix, base);
  }
  
  public Set units() {
    return TIME_UNITS;
  }
  
}
