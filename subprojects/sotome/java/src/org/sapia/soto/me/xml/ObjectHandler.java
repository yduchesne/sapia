package org.sapia.soto.me.xml;

import org.sapia.soto.me.ConfigurationException;



/**
 * An instance of this interface handles objects that corresponds to given XML elements.
 *
 * @author Jean-Cedric Desrochers
 */
public interface ObjectHandler {
  
  /**
   * Handles the passed in object that was created for the element name passed in.
   *
   * @param aName The xml name for which the object was created.
   * @param anObject The object to handle.
   *
   * @throws ConfigurationException if this instance does not "know" (cannot handle) the
   *         passed in object/element name.
   */
  public void handleObject(String aName, Object anObject) throws ConfigurationException;
}
