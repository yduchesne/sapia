package org.sapia.corus.property;

import java.io.Serializable;

import org.sapia.corus.core.InitContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * This class models a configuration property (name and value).
 * 
 * @author yduchesne
 *
 */
public class PropertyImpl implements Property, ApplicationContextAware{
  
  private String value;
  
  @Override
  public void setApplicationContext(ApplicationContext arg0)
      throws BeansException {
    
  }
  
  public void setValue(String value) {
    this.value = value;
  }

  public String getValue(){
    checkNull();
    return value;
  }
  
  public int getIntValue(){
    checkNull();
    return Integer.parseInt(value);
  }
  
  public long getLongValue(){
    checkNull();
    return Long.parseLong(value);
  }

  public boolean getBooleanValue(){
    checkNull();
    return Boolean.parseBoolean(value);
  }
  
  @Override
  public String toString() {
    return getValue();
  }
  
  private void checkNull(){
    if(value == null){
      throw new IllegalStateException("No value specified for property " + value);
    }
  }
  
}
