package org.sapia.soto.properties;

import org.sapia.soto.config.NullObjectImpl;
import org.sapia.util.xml.confix.ConfigurationException;

public class If extends Condition{
  
  /** Creates a new instance of If */
  public If() {
  }
  
  public Object onCreate() throws ConfigurationException{
    if(super.isEqual()) return super.process();
    else return new NullObjectImpl();
  }  

}
