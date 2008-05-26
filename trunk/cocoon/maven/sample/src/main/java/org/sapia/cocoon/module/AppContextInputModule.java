package org.sapia.cocoon.module;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.components.modules.input.InputModule;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class AppContextInputModule implements InputModule, ApplicationContextAware{
  
  ApplicationContext context;
  
  
  public void setApplicationContext(ApplicationContext ctx)
      throws BeansException {
    this.context = ctx;
  }
  
  public Iterator getAttributeNames(Configuration arg0, Map arg1)
      throws ConfigurationException {
      return Arrays.asList(context.getBeanDefinitionNames()).iterator();
  }
  
  public Object getAttribute(String name, Configuration arg1, Map arg2)
      throws ConfigurationException {
    return context.getBean(name);
  }
  
  public Object[] getAttributeValues(String name, Configuration arg1, Map arg2)
      throws ConfigurationException {
    return new Object[]{context.getBean(name)};    
  }

}
