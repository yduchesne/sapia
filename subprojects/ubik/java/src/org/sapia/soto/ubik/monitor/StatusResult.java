package org.sapia.soto.ubik.monitor;

import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class StatusResult implements Comparable {
  
  private Exception _error;
  private Properties _props;
  private String _id, _className;

  public StatusResult(String id, String className){
    _id = id;
    _className = className;
  }
  
  public StatusResult(Exception error, String id, String className){
    this(id, className);
    _error = error;
  }
  
  public Properties getProperties(){
    return _props; 
  }
  
  public Exception getError(){
    return _error;
  }
  
  public boolean isError(){
    return _error != null;
  }
  
  public String getServiceId(){
    return _id;
  }
  
  public String getServiceClassName(){
    return _className;
  }
  
  public StatusResult setProperties(Properties props){
    _props = props;
    return this;
  }
  
  public StatusResult addProperty(String prop, String val){
    if(_props == null) _props = new Properties();
    _props.setProperty(prop, val);
    return this;
  }
  
  public int compareTo(Object obj) {
    StatusResult other = (StatusResult)obj;
    if(_id != null){
      if(other._id != null){
        return _id.compareTo(other._id);
      }
      else{
        return -1;
      }
    }
    else{
      if(other._id != null){
        return 1;
      }
      else if(_className != null){
        if(other._className != null){
          return _className.compareTo(other._className);
        }
        else{
          return -1;
        }
      }
      else{
        return -1;
      }      
    }
  }
  
  public Map getSortedProperties(){
    Map props = new TreeMap();
    props.putAll(_props);
    return props;
  }

}
