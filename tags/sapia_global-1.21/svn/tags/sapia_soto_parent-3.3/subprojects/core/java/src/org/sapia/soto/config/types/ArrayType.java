package org.sapia.soto.config.types;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;
import org.sapia.util.xml.confix.ObjectHandlerIF;

public class ArrayType implements ObjectHandlerIF, ObjectCreationCallback{
  
  private List _items = new ArrayList(5);
  private String _type;
  
  public void setType(String type){
    _type = type;
  }
  
  public Object onCreate() throws ConfigurationException {
    Object[] array;
    if(_type != null){
      try{
        array = (Object[])Array.newInstance(Class.forName(_type), _items.size());
      }catch(Exception e){
        throw new ConfigurationException("Could instantiate array for: " + _type, e);
      }
    }
    else{
      array = _items.toArray(new Object[_items.size()]);
    }
    for(int i = 0; i < _items.size(); i++){
      array[i] = _items.get(i);
    }
    return array;
  }
  
  public void handleObject(String arg0, Object obj) throws ConfigurationException {
    _items.add(obj);
  }

}
