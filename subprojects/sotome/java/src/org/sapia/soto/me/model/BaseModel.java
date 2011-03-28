/**
 * 
 */
package org.sapia.soto.me.model;

import org.sapia.soto.me.ConfigurationException;
import org.sapia.soto.me.xml.ObjectHandler;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public abstract class BaseModel implements ObjectHandler {

  /**
   * Creates a new BaseModel instance.
   */
  protected BaseModel() {
  }
  
  /**
   * Utility method to asserts the type of a given object.
   * 
   * @param eType The expected type of the object.
   * @param objectName The name of the object validated. 
   * @param actual The actual object to validate.
   * @exception ConfigurationException If the assertion fails.
   */
  protected void assertObjectType(Class eType, String objectName, Object actual) throws ConfigurationException {
    if (!eType.isInstance(actual)) {
      throw new ConfigurationException("Invalid type for the object named '" + objectName + "' on the class" +
              getClass().getName() + " : expected " + eType + " but was " + actual.getClass().getName());
    }
  }

  /**
   * Utility method that throws a configuration exception. Called when a model object encounters an object
   * for which it does not recognize the name.
   *  
   * @param aName The name of the object.
   * @param anObject The object handled.
   * @throws ConfigurationException The created exception with the passed in arguments.
   */
  protected void throwUnrecognizedObject(String aName, Object anObject) throws ConfigurationException {
    throw new ConfigurationException("Unrecognized object to handle on " + getClass().getName() +
            " : name=" + aName + " object=" + anObject);
  }
}
