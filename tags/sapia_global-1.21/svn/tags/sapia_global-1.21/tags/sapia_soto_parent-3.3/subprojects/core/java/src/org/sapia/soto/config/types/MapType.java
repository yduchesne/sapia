/*
 * MapType.java
 *
 * Created on June 28, 2005, 2:30 PM
 */

package org.sapia.soto.config.types;

import java.util.HashMap;
import java.util.Map;
import org.sapia.soto.config.NullObjectImpl;
import org.sapia.soto.util.Param;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 * Evaluates to a <code>java.util.Map</code>.
 * @author yduchesne
 */
public class MapType implements ObjectCreationCallback{
  
  private Map _map = new HashMap();
  
  /** Creates a new instance of MapType */
  public MapType() {
  }
  
  public MapParam createEntry(){
    return new MapParam(_map);
  }
  
  public Object onCreate() throws ConfigurationException{
    return _map;
  }
  
  public static class MapParam extends Param implements ObjectCreationCallback{
    
    private Object _key;
    private Map _owner;
    
    MapParam(Map owner){
      _owner = owner;
    }
    
    public void setKey(Object key){
      _key = key;
    }
    
    public Object onCreate() throws ConfigurationException{
      if((_key != null || getName() != null) && getValue() != null){
        _owner.put(_key == null ? getName() : _key, getValue());
      }
      return new NullObjectImpl();
    }
    
    
  }
  
}
