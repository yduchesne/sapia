package org.sapia.corus.configurator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.sapia.corus.admin.Arg;
import org.sapia.corus.db.DbMap;

public class PropertyStore {

  private DbMap<String, ConfigProperty> properties;

  public PropertyStore(DbMap<String, ConfigProperty> properties) {
    this.properties = properties;
  }
  
  public void addProperty(String name, String value) {
    properties.put(name, new ConfigProperty(name, value));
  }
  
  public String getProperty(String name) {
    ConfigProperty prop = properties.get(name);
    if(prop == null){
      return null;
    }
    return prop.getValue();
  }
  
  public void removeProperty(String name) {
    properties.remove(name); 
  }

  public void removeProperty(Arg pattern) {
    List<String> toRemove = new ArrayList<String>();
    Iterator<String> names = properties.keys();
    while(names.hasNext()){
      String name = names.next();
      if(pattern.matches(name)){
        toRemove.add(name);
      }
    }
    for(String r:toRemove){
      properties.remove(r);
    }
  }

  public Properties getProperties() {
    Iterator<String> names = properties.keys();
    Properties props = new Properties();
    while(names.hasNext()){
      String name = names.next();
      ConfigProperty prop = properties.get(name);
      if(prop != null && prop.getValue() != null){
        props.setProperty(name, prop.getValue());
      }
    }
    return props;
  }

}
