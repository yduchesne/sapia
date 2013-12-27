package org.sapia.regis.loader;

import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;
import org.sapia.util.xml.confix.ObjectFactoryIF;

public class ParamRefTag implements TagFactory, ObjectCreationCallback{
  
  private TemplateContextIF _ctx;
  private String name, defaultVal;
  
  public void setDefault(String defaultVal) {
    this.defaultVal = defaultVal;
  }

  public void setName(String name) {
    this.name = name;
  }  
  
  public Object onCreate() throws ConfigurationException {
    Object val = _ctx.getValue(name);
    if(val == null){
      if(defaultVal == null){
        throw new ConfigurationException("No parameter value found for: " + name);
      }
      else{
        return this.defaultVal;
      }
    }
    else{
      return val;
    }
  }
  
  public Object create(TemplateContextIF context, ObjectFactoryIF fac) throws Exception {
    _ctx = context;
    return this;
  }

}
