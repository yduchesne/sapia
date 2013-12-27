package org.sapia.soto.me.model;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.sapia.soto.me.ConfigurationException;
import org.sapia.soto.me.SotoMeContainer;
import org.sapia.soto.me.util.Log;
import org.sapia.soto.me.xml.ObjectCreationCallback;

/**
 * This class implements the <code>soto:serviceSelect</code> tag for the SotoMe container.
 *
 * @author Jean-Cédric Desrochers
 */
public class J2meServiceSelector extends BaseModel implements ObjectCreationCallback {

  private static final String MODULE_NAME = "ServiceSelector";
  
  /** The SotoMe container used by this service selector. */
  private SotoMeContainer _container;
  
  /** The attributes of this selector. */
  private FastMap _attributes;
  
  /**
   * Creates a new J2meServiceSelector instance.
   */
  public J2meServiceSelector(SotoMeContainer aContainer) {
    _attributes = new FastMap();
    _container = aContainer;
  }
  
  /**
   * Returns the attributes value.
   *
   * @return The attributes value.
   */
  public FastMap getAttributes() {
    return _attributes;
  }

  /**
   * Adds a new attribute.
   *
   * @param aName The new attribute's name.
   * @param aValue The new attribute's value.
   */
  public void addAttribute(String aName, String aValue) {
    _attributes.put(aName, aValue);
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectHandler#handleObject(java.lang.String, java.lang.Object)
   */
  public void handleObject(String aName, Object anObject) throws ConfigurationException {
    assertObjectType(String.class, aName, anObject);
    addAttribute(aName, (String) anObject);
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    FastList metaDatas = _container.getJ2meMetaDatas();

    J2meServiceMetaData metaData = null;
    boolean isFound = false;
    for (FastList.Node n = (FastList.Node) metaDatas.head(); !isFound && (n = (FastList.Node) n.getNext()) != metaDatas.tail(); ) {
      metaData = (J2meServiceMetaData) n.getValue();
      
      boolean isMatching = true;
      for (FastMap.Entry attr = _attributes.head(); isMatching && (attr = (FastMap.Entry) attr.getNext()) != _attributes.tail(); ) {
        isMatching = metaData.hasAttribute((String) attr.getKey(), (String) attr.getValue());
      }
      
      isFound = isMatching;
    }
    
    if (isFound) {
      if (Log.isDebug()) {
        Log.debug(MODULE_NAME, "Resolved selector attributes " + _attributes + " to the service name '"
                + metaData.getServiceName() + "' : " + metaData.getService());
      }
      return metaData.getService();
    } else {
      String message = "No service selected for specified attributes: " + _attributes;
      Log.error(MODULE_NAME, message);
      throw new ConfigurationException(message);
    }
  }
}
