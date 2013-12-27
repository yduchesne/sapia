package org.sapia.soto.regis;

import org.sapia.soto.config.NullObjectImpl;
import org.sapia.util.xml.confix.ConfigurationException;

public class IfTag extends Condition{
  
  /** Creates a new instance of If */
  public IfTag() {
  }
  
  public Object onCreate() throws ConfigurationException{
    if(super.isEqual()) return super.process();
    else return new NullObjectImpl();
  }  

}
