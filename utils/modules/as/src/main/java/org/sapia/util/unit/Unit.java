package org.sapia.util.unit;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * This class can be used to convert measurement units, in a broad term (time, memory/storage, 
 * distance), or to parse string representations to the proper unit value.
 * <p>
 * For example, given we wish to convert 1 minute to milliseconds:
 * <pre>
 * long millis = TimeUnit.MINUTE.converstAsLong(1, TimeUnit.MILLIS);
 * </pre>
 * As for parsing, we could convert 1 minute to the proper unit value:
 * <pre>
 * int minute = TimeUnit.PROTOTYPE.parseInt("1m", null); // here, minute will equal 1
 * </pre>
 * All the parseAsXXX method can also perform conversions. For example, we could
 * parse a given input string to the proper unit value and internally have it converted
 * in milliseconds:
 * <pre>
 * // this converts 1 minute to milliseconds
 * long millis = TimeUnit.PROTOTYPE.parseLong("1m", TimeUnit.MILLIS); 
 * </pre>
 * To be parsed properly, strings representing unit values must have the appropriate suffix. For
 * time units, the suffixes are as follows (see {@link org.sapia.util.unit.TimeUnit}):
 * <p>
 * <ul>
 *   <li>ms: millisecond
 *   <li>s: second
 *   <li>m: minute
 *   <li>h: hour
 *   <li>D: day
 *   <li>M: month
 *   <li>Y: year
 * </ul>
 * For memory/storage units (see {@link org.sapia.util.unit.StorageUnit}):
 * <ul>
 *   <li>b: byte
 *   <li>kb: kilobyte
 *   <li>mb: megabyte
 *   <li>gb: gigabyte
 * </ul>
 * For distance units (see {@link org.sapia.util.unit.DistanceUnit}):
 * <ul>
 *   <li>mm: millimiter
 *   <li>cm: centimeter
 *   <li>m: meter
 *   <li>km: kilometer
 * </ul>
 * 
 * @author yduchesne
 *
 */
public class Unit {

  protected int  _base;
  private String _suffix;
  
  public Unit(String suffix, int base){
    _suffix = suffix;
    _base = base;
  }
  
  /**
   * @return the suffix of this instance.
   */
  public String getSuffix(){
    return _suffix;
  }

  /**
   * @param value an integer
   * @param target the <code>Unit</code> to which the given 
   * value must be converted.
   * @return the converted value
   */
  public int convertAsInt(int value, Unit target){
    if(target._base == _base){
      return value;
    }
    value = value * _base;
    return value / target._base;
  }   
  
  /**
   * @param value a long
   * @param target the <code>Unit</code> to which the given 
   * value must be converted.
   * @return the converted value
   */  
  public long convertAsLong(long value, Unit target){
    if(target._base == _base){
      return value;
    }
    value = value * _base;
    return value / target._base;
  }

  /**
   * @param value a float
   * @param target the <code>Unit</code> to which the given 
   * value must be converted.
   * @return the converted value
   */    
  public float convertAsFloat(float value, Unit target){
    if(target._base == _base){
      return value;
    }
    value = value * _base;
    return value / target._base;
  }   
  
  /**
   * @param value a double
   * @param target the <code>Unit</code> to which the given 
   * value must be converted.
   * @return the converted value
   */    
  public double convertAsDouble(double value, Unit target){
    if(target._base == _base){
      return value;
    }
    value = value * _base;
    return value / target._base;
  }
  
  /**
   * @param value the string representation of a value.
   * @param target the <code>Unit</code> to which the given 
   * value must be converted after being parsed, or <code>null</code>
   * if no conversion should occur.
   * @return the parsed (an possibly converted) value, as an integer.
   */    
  public int parseInt(String value, Unit conversionTarget){
    Unit unit = findUnit(value);
    if(unit != null){
      int intValue = Integer.parseInt(value.substring(0, value.length() - unit.getSuffix().length()));
      if(conversionTarget != null){
        intValue = unit.convertAsInt(intValue, conversionTarget);
      }
      return intValue;
    }
    return Integer.parseInt(value);
  }    
  
  /**
   * @param value the string representation of a value.
   * @param target the <code>Unit</code> to which the given 
   * value must be converted after being parsed, or <code>null</code>
   * if no conversion should occur.
   * @return the parsed (an possibly converted) value, as a long.
   */      
  public long parseLong(String value, Unit conversionTarget){
    Unit unit = findUnit(value);
    if(unit != null){
      long longValue = Long.parseLong(value.substring(0, value.length() - unit.getSuffix().length()));
      if(conversionTarget != null){
        longValue = unit.convertAsLong(longValue, conversionTarget);
      }
      return longValue;
    }
    return Long.parseLong(value);
  }

  /**
   * @param value the string representation of a value.
   * @param target the <code>Unit</code> to which the given 
   * value must be converted after being parsed, or <code>null</code>
   * if no conversion should occur.
   * @return the parsed (an possibly converted) value, as an float.
   */      
  public float parseFloat(String value, Unit conversionTarget){
    Unit unit = findUnit(value);
    if(unit != null){
      float floatValue = Float.parseFloat(value.substring(0, value.length() - unit.getSuffix().length()));
      if(conversionTarget != null){
        floatValue = unit.convertAsFloat(floatValue, conversionTarget);
      }
      return floatValue;
    }
    return Float.parseFloat(value);
  }
  
  /**
   * @param value the string representation of a value.
   * @param target the <code>Unit</code> to which the given 
   * value must be converted after being parsed, or <code>null</code>
   * if no conversion should occur.
   * @return the parsed (an possibly converted) value, as a double.
   */      
  public double parseDouble(String value, Unit conversionTarget){
    Unit unit = findUnit(value);
    if(unit != null){
      double doubleValue = Double.parseDouble(value.substring(0, value.length() - unit.getSuffix().length()));
      if(conversionTarget != null){
        doubleValue = unit.convertAsDouble(doubleValue, conversionTarget);
      }
      return doubleValue;
    }
    return Double.parseDouble(value);
  }      
  
  public Set units(){
    return Collections.EMPTY_SET;
  }
  
  private Unit findUnit(String value){
    
    Iterator itr = units().iterator();
    while(itr.hasNext()){
      Unit unit = (Unit)itr.next();
      if(value.length() >= unit.getSuffix().length()){
        int x = value.length() - unit.getSuffix().length();
        if(value.length() > unit.getSuffix().length() && !Character.isDigit(value.charAt(x-1))){
          continue;
        }
        for(int i = 0; i < unit.getSuffix().length(); i++,x++){
          if(value.charAt(x) != unit.getSuffix().charAt(i)){
            break;
          }
        }
        if(x == value.length()) return unit;
      }
    }
    return null;
  }

}
