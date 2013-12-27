/*
 * ConfigExample.java
 *
 * Created on August 10, 2005, 1:29 PM
 *
 */

package org.sapia.soto.configuration.example;

import org.sapia.soto.configuration.ConfigCategory;
import org.sapia.resource.Resource;

/**
 *
 * @author yduchesne
 */
public class ConfigExample{
  
  public int intValue;
  public String textValue;
  public Resource resValue;
  public ConfigCategory category;
  
  /** Creates a new instance of ConfigExample */
  public ConfigExample() {
  }
  
  public void setInteger(int value){
    intValue = value;
  }
  
  public void setText(String text){
    textValue = text;
  }
  
  public void setResource(Resource res){
    resValue = res;
  }
  
  public void setConfig(ConfigCategory aCategory) {
    category = aCategory;
  }
  
}
