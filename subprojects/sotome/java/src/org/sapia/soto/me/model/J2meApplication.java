package org.sapia.soto.me.model;

import org.sapia.soto.me.ConfigurationException;
import org.sapia.soto.me.SotoMeContainer;
import org.sapia.soto.me.util.Log;

/**
 * This class implements the <code>soto:app</code> tag for the SotoMe container.
 * 
 * @author Jean-Cédric Desrochers
 */
public class J2meApplication extends BaseModel {

  public static final String TAG_SERVICE   = "service";

  /** The SotoMe container of this application. */
  private SotoMeContainer _container;
  
  /**
   * Creates a new J2meApplication instance.
   */
  public J2meApplication(SotoMeContainer aContainer) {
    _container = aContainer;
  }
  
  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectHandler#handleObject(java.lang.String, java.lang.Object)
   */
  public void handleObject(String aName, Object anObject) throws ConfigurationException {
    if (TAG_SERVICE.equals(aName)) {
      assertObjectType(J2meServiceMetaData.class, aName, anObject);
      _container.addServiceMetaData((J2meServiceMetaData) anObject);
      
    } else if (anObject instanceof J2meServiceMetaData) {
      _container.addServiceMetaData((J2meServiceMetaData) anObject);
      
    } else if (!(anObject instanceof J2meApplication)) {
      Log.warn("J2meApplication unable to handle the passed in object - ignore since it is the root level [name=" +
              aName + " object=" + anObject + "]");
    }
  }
}
