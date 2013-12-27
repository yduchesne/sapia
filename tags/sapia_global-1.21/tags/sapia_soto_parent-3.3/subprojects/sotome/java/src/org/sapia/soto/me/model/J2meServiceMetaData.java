/**
 * 
 */
package org.sapia.soto.me.model;

import javolution.util.FastMap;

import org.sapia.soto.me.ConfigurationException;
import org.sapia.soto.me.J2meService;
import org.sapia.soto.me.SotoMeLifeCycle;

/**
 * This class implements the <code>soto:service</code> tag for the SotoMe container.
 *
 * @author Jean-Cédric Desrochers
 */
public class J2meServiceMetaData extends BaseModel {

  public static final String TAG_ID = "id";
  
  /** The lifecycle of this service meta data. */
  private SotoMeLifeCycle _serviceLyfeCycle;
  
  /** The identifier of this service. */
  private String _id;
  
  /** The service instance of this meta data. */ 
  private J2meService _service;
  
  /** The service's name of this meta data. */ 
  private String _serviceName;
  
  /** The attributes of this service meta data. */
  private FastMap _attributes;
  
  /**
   * Creates a new J2meServiceMetaData instance.
   */
  public J2meServiceMetaData() {
    _serviceLyfeCycle = new SotoMeLifeCycle();
    _attributes = new FastMap();
  }
  
  /**
   * Returns the lifecycle object of this service meta data.
   * 
   * @return The lifecycle object of this service meta data.
   */
  public SotoMeLifeCycle getLifeCycle() {
    return _serviceLyfeCycle;
  }
  
  /**
   * Returns the identifier of this service meta data.
   * 
   * @return The identifier of this service meta data.
   */
  public String getId() {
    return _id;
  }
  
  /**
   * Changes the identifier of this service meta data.
   * 
   * @param anId The new service identifier.
   */
  public void setId(String anId) {
    _id = anId;
  }
  
  /**
   * Returns the name of this service as it was loaded from the configuration file.
   * 
   * @return The name of this service as it was loaded from the configuration file.
   */
  public String getServiceName() {
    return _serviceName;
  }
  
  /**
   * Changes the value of the serviceName.
   *
   * @param aServiceName The new serviceName value.
   */
  public void setServiceName(String aServiceName) {
    _serviceName = aServiceName;
  }

  /**
   * Returns the enclosed soto service as a {@link J2meService}.
   * 
   * @return The enclosed soto service as a {@link J2meService}.
   * @exception ClassCastException If the service is not a J2meService. 
   */
  public J2meService getService() throws ClassCastException {
    return (J2meService) _service;
  }
  
  /**
   * Changes the value of the service.
   *
   * @param aService The new service value.
   */
  public void setService(J2meService aService) {
    _service = aService;
  }

  /**
   * Adds a generic attribute to this service meta data.
   * 
   * @param aName The attribute's name.
   * @param aValue The attribute's value.
   */
  public void addAttribute(String aName, String aValue) {
    _attributes.put(aName, aValue);
  }
  
  /**
   * Returns the value of a given attribute.
   * 
   * @param aName The name of the attribute to retrieve.
   * @return The value of a given attribute, or <code>null</code> if not attribute is found.
   */
  public String getAttribute(String aName) {
    return (String) _attributes.get(aName);
  }
  
  /**
   * Returns true of this meta data contains the specified attribute's value.
   * 
   * @param aName The name of the attribute to verify.
   * @param anExpectedValue The expected value of the attribute.
   * @return True if the attribute is found with the same value. 
   */
  public boolean hasAttribute(String aName, String anExpectedValue) {
    String actualValue = (String) _attributes.get(aName);
    return (anExpectedValue == null && actualValue == null) ||  
           (anExpectedValue != null && anExpectedValue.equals(actualValue));
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectHandler#handleObject(java.lang.String, java.lang.Object)
   */
  public void handleObject(String aName, Object anObject) throws ConfigurationException {
    if (TAG_ID.equals(aName)) {
      assertObjectType(String.class, aName, anObject);
      setId((String) anObject);

    } else if (anObject instanceof J2meService) {
      setService((J2meService) anObject);
      setServiceName(aName);

    } else if (anObject instanceof String) {
      addAttribute(aName, (String) anObject);
      
    } else {
      throwUnrecognizedObject(aName, anObject);
    }
  }
}
