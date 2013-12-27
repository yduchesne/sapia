package org.sapia.soto.me.model;

import org.sapia.soto.me.ConfigurationException;
import org.sapia.soto.me.NotFoundException;
import org.sapia.soto.me.SotoMeContainer;
import org.sapia.soto.me.util.Log;
import org.sapia.soto.me.xml.ObjectCreationCallback;

/**
 * This class implements the <code>soto:serviceRef</code> tag for the SotoMe container.
 *
 * @author Jean-Cédric Desrochers
 */
public class J2meServiceRef extends BaseModel implements ObjectCreationCallback {

  public static final String TAG_ID = "id";
  
  private static final String MODULE_NAME = "ServiceRef";
  
  /** The SotoMe container used by this service reference. */
  private SotoMeContainer _container;
  
  /** The identifier of the service referenced. */
  private String _id;
  
  /**
   * Creates a new J2meServiceRef instance.
   */
  public J2meServiceRef(SotoMeContainer aContainer) {
    _container = aContainer;
  }
  
  /**
   * Returns the id value.
   *
   * @return The id value.
   */
  public String getId() {
    return _id;
  }

  /**
   * Changes the value of the id.
   *
   * @param aId The new id value.
   */
  public void setId(String aId) {
    _id = aId;
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectHandler#handleObject(java.lang.String, java.lang.Object)
   */
  public void handleObject(String aName, Object anObject) throws ConfigurationException {
    if (TAG_ID.equals(aName)) {
      assertObjectType(String.class, aName, anObject);
      setId((String) anObject);
      
    } else {
      throwUnrecognizedObject(aName, anObject);
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    try {
      Object service = _container.lookupService(_id);
      
      if (Log.isDebug()) {
        Log.debug(MODULE_NAME, "Resolved service reference id '" + _id + "' to service: " + service);
      }

      return service;
      
    } catch (NotFoundException nfe) {
      String message = "Unable to resolve service reference id '" + _id + "'";
      Log.error(MODULE_NAME, message);
      throw new ConfigurationException(message, nfe);
    }
  }
}
