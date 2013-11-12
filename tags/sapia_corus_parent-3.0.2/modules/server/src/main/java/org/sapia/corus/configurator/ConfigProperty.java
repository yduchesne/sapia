package org.sapia.corus.configurator;

/**
 * Models a configuration property, corresponding to a name and its associated value.
 * 
 * @author yduchesne
 *
 */
public class ConfigProperty{
  
  String name, value;
  
  
  ConfigProperty() {
  }

  ConfigProperty(String name, String value){
    this.name = name;
    this.value = value;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  public void setValue(String value) {
    this.value = value;
  }
  
  public String getValue() {
    return value;
  }

}