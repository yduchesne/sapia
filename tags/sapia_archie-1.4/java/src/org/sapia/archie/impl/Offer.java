package org.sapia.archie.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;


/**
 * @author Yanick Duchesne
 */
public class Offer implements java.io.Serializable{
  
  private Properties _attributes;
  private Object     _obj;
  private String     _uniqueId;
  private long       _lastSelectTime = System.currentTimeMillis();
  private long       _selectCount;
  private static long _counter = 0; 
  
  public Offer(Properties attributes, Object obj){
    _attributes = attributes;
    _obj        = obj;
    _uniqueId   = uniqueId();
  }
  
  public Properties getAttributes(){
    return _attributes;
  }
  
  public String getId(){
    return _uniqueId;
  }
  
  public long getLastSelectTime(){
    return _lastSelectTime;
  }
  
  public long getSelectCount(){
    return _selectCount;
  }
  
  Offer select(){
    _lastSelectTime = System.currentTimeMillis();
    ++_selectCount;
    return this;
  }
  
  public boolean matches(Properties props){
    Iterator entries = props.entrySet().iterator();  
    String value;
    while(entries.hasNext()){
	    Map.Entry entry = (Map.Entry)entries.next();
	    value = _attributes.getProperty(entry.getKey().toString());
	    if(value == null && entry.getValue() == null){
	      continue;
	    }
	    else if(value == null && entry.getValue() != null){
	      return false;
	    }
	    else if(value != null && entry.getValue() == null){
	      return false;
	    }
	    else if(value != null && entry.getValue() != null && 
	            entry.getValue().toString().equals(value)){
	      continue;
	    }
	    else{
	      return false;
	    }
    }
    return true;
  }
  
  public Object getObject(){
    return _obj;
  }
  
  public String toString() {
    StringBuffer aBuffer = new StringBuffer(super.toString());
    aBuffer.append("[id=").append(_uniqueId).
            append(" object=").append(_obj).
            append(" selectCount=").append(_selectCount).
            append(" lastSelectTime=").append(_lastSelectTime).
            append(" attributes=").append(_attributes).
              append("]");
    
    return aBuffer.toString();
  }
  
  static synchronized String uniqueId(){
    return "" + (_counter++);
  }  
}
