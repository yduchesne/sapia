package org.sapia.soto.me.model;

import org.sapia.soto.me.ConfigurationException;

/**
 * This class implements the <code>soto:defs</code> tag for the SotoMe container.
 * 
 * @author Jean-Cédric Desrochers
 */
public class Definitions extends BaseModel {

  /**
   * Creates a new Definitions instance.
   */
  public Definitions() {
  }
  
  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectHandler#handleObject(java.lang.String, java.lang.Object)
   */
  public void handleObject(String aName, Object anObject) throws ConfigurationException {
    if (!(anObject instanceof Namespace)) {
      throwUnrecognizedObject(aName, anObject);
    }
  }
}
