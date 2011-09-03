package org.sapia.regis.util;

import java.util.Properties;

import org.sapia.util.text.TemplateContextIF;

public class PropertiesContext implements TemplateContextIF{
  
  Properties props;
  TemplateContextIF parent;
  
  public PropertiesContext(Properties props, TemplateContextIF parent){
    this.props = props;
    this.parent = parent;
  }
  
  public Object getValue(String name) {
    Object prop = props.getProperty(name);
    if(prop == null && parent != null){
      prop = parent.getValue(name);
    }
    return prop;
  }
  public void put(String arg0, Object arg1) {
  }
  
}
