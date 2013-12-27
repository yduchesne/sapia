package org.sapia.soto;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * An instance of this class keeps strongly-typed application settings. It offers
 * methods to a) add settings; b) read settings; c) test for the existence of given settings.
 * <p>
 * A setting is a name/value pair, where the value can be a String, an int, a long, a float, or 
 * a double.
 * <p>
 * For the getter methods that allow reading settings (getXXX) without a passed in default value, 
 * an {@link java.lang.IllegalStateException} will be thrown if no corresponding setting could be found.
 * In the case of the getter methods that allow passing in a default value, that value will be returned
 * if no corresponding setting could be found.
 * <p>
 * To test for the existence of a given setting, call the {@link #exists(String)} method.
 * 
 * @see Env#getSettings()
 * @see org.sapia.soto.EnvAware
 *   
 * @author yduchesne
 *
 */
public class Settings implements Serializable{

  static final long serialVersionUID = 1L;
  
  private Map _settings;
  
  public Settings(){
    this(new HashMap());
  }
  
  public Settings(Map settings){
    _settings = Collections.synchronizedMap(settings);
  }  
  
  public void addString(String name, String value){
    _settings.put(name, value);
  }
  
  public void addInt(String name, int value){
    _settings.put(name, new Integer(value));
  }
  
  public void addLong(String name, long value){
    _settings.put(name, new Long(value));
  }
  
  public void addFloat(String name, float value){
    _settings.put(name, new Float(value));
  }
  
  public void addDouble(String name, double value){
    _settings.put(name, new Double(value));
  }
  
  public void addBoolean(String name, boolean value){
    _settings.put(name, new Boolean(value));
  }  
  
  public boolean exists(String name){
    return _settings.get(name) != null;
  }
  
  public String getString(String name){
    Object val = _settings.get(name);
    if(val == null){
      throw new IllegalStateException("No setting found for " + name);
    }
    if(val instanceof String){
      return (String)val;
    }
    else{
      return val.toString();
    }
  }
  
  public String getString(String name, String dflt){
    Object val = _settings.get(name);
    if(val == null){
      return dflt;
    }
    if(val instanceof String){
      return (String)val;
    }
    else{
      return val.toString();
    }
  }  
  
  public int getInt(String name){
    Object val = _settings.get(name);
    if(val == null){
      throw new IllegalStateException("No setting found for " + name);
    }
    if(val instanceof String){
      return Integer.parseInt((String)val);
    }
    else if(val instanceof Integer){
      return ((Integer)val).intValue();
    }
    else{
      throw new IllegalStateException("Setting is instance of " + val.getClass().getName() + "; " 
          + "cannot convert to int");
    }
  }
  
  public int getInt(String name, int dflt){
    Object val = _settings.get(name);
    if(val == null){
      return dflt;
    }
    if(val instanceof String){
      return Integer.parseInt((String)val);
    }
    else if(val instanceof Integer){
      return ((Integer)val).intValue();
    }
    else{
      throw new IllegalStateException("Setting is instance of " + val.getClass().getName() + "; " 
          + "cannot convert to int");
    }
  }  
  
  public long getLong(String name){
    Object val = _settings.get(name);
    if(val == null){
      throw new IllegalStateException("No setting found for " + name);
    }
    if(val instanceof String){
      return Long.parseLong((String)val);
    }
    else if(val instanceof Long){
      return ((Long)val).longValue();
    }
    else{
      throw new IllegalStateException("Setting is instance of " + val.getClass().getName() + "; " 
          + "cannot convert to long");
    }
  }
  
  public long getLong(String name, long dflt){
    Object val = _settings.get(name);
    if(val == null){
      return dflt;
    }
    if(val instanceof String){
      return Long.parseLong((String)val);
    }
    else if(val instanceof Long){
      return ((Long)val).longValue();
    }
    else{
      throw new IllegalStateException("Setting is instance of " + val.getClass().getName() + "; " 
          + "cannot convert to long");
    }
  }    
  
  public float getFloat(String name){
    Object val = _settings.get(name);
    if(val == null){
      throw new IllegalStateException("No setting found for " + name);
    }
    if(val instanceof String){
      return Float.parseFloat((String)val);
    }
    else if(val instanceof Float){
      return ((Float)val).floatValue();
    }
    else{
      throw new IllegalStateException("Setting is instance of " + val.getClass().getName() + "; " 
          + "cannot convert to float");
    }
  }  
  
  public float getFloat(String name, float dflt){
    Object val = _settings.get(name);
    if(val == null){
      return dflt;
    }
    if(val instanceof String){
      return Float.parseFloat((String)val);
    }
    else if(val instanceof Long){
      return ((Float)val).floatValue();
    }
    else{
      throw new IllegalStateException("Setting is instance of " + val.getClass().getName() + "; " 
          + "cannot convert to float");
    }
  }  
  
  public double getDouble(String name){
    Object val = _settings.get(name);
    if(val == null){
      throw new IllegalStateException("No setting found for " + name);
    }
    if(val instanceof String){
      return Double.parseDouble((String)val);
    }
    else if(val instanceof Double){
      return ((Double)val).doubleValue();
    }
    else{
      throw new IllegalStateException("Setting is instance of " + val.getClass().getName() + "; " 
          + "cannot convert to double");
    }
  }
  
  public double getDouble(String name, double dflt){
    Object val = _settings.get(name);
    if(val == null){
      return dflt;
    }
    if(val instanceof String){
      return Double.parseDouble((String)val);
    }
    else if(val instanceof Double){
      return ((Double)val).doubleValue();
    }
    else{
      throw new IllegalStateException("Setting is instance of " + val.getClass().getName() + "; " 
          + "cannot convert to double");
    }
  }
  
  public boolean getBoolean(String name){
    Object val = _settings.get(name);
    if(val == null){
      throw new IllegalStateException("No setting found for " + name);
    }
    if(val instanceof String){
      String str = (String)val;
      return str.equalsIgnoreCase("true") || str.equalsIgnoreCase("yes") || str.equalsIgnoreCase("on");
    }    
    else if(val instanceof Boolean){
      return ((Boolean)val).booleanValue();
    }
    else{
      throw new IllegalStateException("Setting is instance of " + val.getClass().getName() + ";" 
          + " cannot convert to boolean");
    }    
  }  
  
  public boolean getBoolean(String name, boolean dflt){
    Object val = _settings.get(name);
    if(val == null){
      return dflt;
    }
    if(val instanceof String){
      String str = (String)val;
      return str.equalsIgnoreCase("true") || str.equalsIgnoreCase("yes") || str.equalsIgnoreCase("on");
    }    
    else if(val instanceof Boolean){
      return ((Boolean)val).booleanValue();
    }
    else{
      throw new IllegalStateException("Setting is instance of " + val.getClass().getName() + ";" 
          + " cannot convert to boolean");
    }    
  }
  
  /**
   * @return returns this instance's settings as properties.
   */
  public Properties getProperties(){
    Properties props = new Properties();
    Iterator itr = _settings.entrySet().iterator();
    while(itr.hasNext()){
      Map.Entry entry = (Map.Entry)itr.next();
      if(entry.getValue() == null)
        props.setProperty((String)entry.getKey(), null);
      else
        props.setProperty((String)entry.getKey(), entry.getValue().toString());
    }
    return props;
  }
  
  /**
   * @return this instance's settings, in an unmodifiable <code>Map</code>.
   */
  public Map getSettings(){
    return Collections.unmodifiableMap(_settings);
  }
  
}
