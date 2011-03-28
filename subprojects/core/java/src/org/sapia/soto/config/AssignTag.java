package org.sapia.soto.config;

import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;
import org.sapia.util.xml.confix.ObjectWrapperIF;

public class AssignTag implements 
  ObjectCreationCallback, ObjectWrapperIF{
  
  private Object _obj;
  
  public void setObject(Object obj){
    _obj = obj;
  }
  
  public Object getWrappedObject() {
    try{
      return onCreate();
    }catch(ConfigurationException e){
      throw new IllegalStateException("Could not acquire object", e);
    }
  }

  public Object onCreate() throws ConfigurationException {
    if(_obj == null){
      throw new ConfigurationException("Object not set");
    }
    return _obj;
  }
}
