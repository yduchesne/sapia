/**
 * 
 */
package org.sapia.soto.me;

import org.sapia.soto.me.ConfigurationException;
import org.sapia.soto.me.xml.ObjectHandler;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class Property implements ObjectHandler {

  public String name;
  public String value;
  
  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectHandler#handleObject(java.lang.String, java.lang.Object)
   */
  public void handleObject(String aName, Object anObject) throws ConfigurationException {
    name = aName;
    value = (String) anObject;
  }

  public String toString() {
    return super.toString() + "[" + name + "=" + value + "]";
  }
}
