/*
 * If.java
 *
 * Created on November 2, 2005, 4:37 PM
 */

package org.sapia.soto.configuration;
import org.sapia.soto.config.NullObjectImpl;
import org.sapia.util.xml.confix.ConfigurationException;


/**
 *
 * @author yduchesne
 */
public class Unless extends Condition{
  
  /** Creates a new instance of Unless */
  public Unless() {
  }
  
  public Object onCreate() throws ConfigurationException{
    if(!super.isEqual()) return super.process();
    else return new NullObjectImpl();
  }
  
}
