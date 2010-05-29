package org.sapia.corus.property;

import java.util.Properties;

import org.sapia.corus.core.PropertyContainer;


public interface PropertyProvider {
  
  public PropertyContainer getInitProperties();
  
  public void overrideInitProperties(PropertyContainer props);

}
