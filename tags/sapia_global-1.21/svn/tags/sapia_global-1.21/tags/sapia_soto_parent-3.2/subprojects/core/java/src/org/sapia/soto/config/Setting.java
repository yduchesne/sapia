package org.sapia.soto.config;

import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.NullObject;
import org.sapia.util.xml.confix.ObjectCreationCallback;

public class Setting implements EnvAware, ObjectCreationCallback{
  
  private String name;
  private Object value;
  private Env env;
  
  static final NullObject NULL = new NullObject(){};
  
  public void setEnv(Env env) {
    this.env = env;
  }
  public void setName(String name) {
    this.name = name;
  }
  public void setValue(Object value) {
    this.value = value;
  }
  
  public Object onCreate() throws ConfigurationException {
    if(name != null && value != null){
      if(value instanceof Boolean){
        env.getSettings().addBoolean(name, ((Boolean)value).booleanValue());
      }
      else if(value instanceof Integer){
        env.getSettings().addInt(name, ((Integer)value).intValue());
      }
      else if(value instanceof Float){
        env.getSettings().addFloat(name, ((Float)value).floatValue());
      }      
      else if(value instanceof Long){
        env.getSettings().addLong(name, ((Long)value).longValue());
      }
      else if(value instanceof Double){
        env.getSettings().addDouble(name, ((Double)value).doubleValue());
      }            
      else if (value instanceof String){
        env.getSettings().addString(name, (String)value);
      }
    }
    return NULL;
  }

}
