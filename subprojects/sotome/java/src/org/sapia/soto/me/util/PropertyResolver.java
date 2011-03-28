package org.sapia.soto.me.util;

/**
 * Simple interface to resolve properties.
 *
 * @author Jean-Cédric Desrochers
 */
public interface PropertyResolver {

  /**
   * Returns the value of the property as it exists in the SotoME environment.
   *    
   * @param aName The name of the property to retrieve.
   * @return The value of the property found, or <code>null</code> if no property is found.
   */
  public String getProperty(String aName);
}
