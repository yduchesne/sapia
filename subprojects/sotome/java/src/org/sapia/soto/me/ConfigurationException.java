package org.sapia.soto.me;

import org.sapia.soto.me.util.CompositeException;

/**
 * Thrown is the case of misconfiguration.
 */
public class ConfigurationException extends CompositeException {
  /**
   * Constructor for ConfigurationException.
   * 
   * @param arg0
   * @param arg1
   */
  public ConfigurationException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  /**
   * Constructor for ConfigurationException.
   * 
   * @param arg0
   */
  public ConfigurationException(String arg0) {
    super(arg0);
  }
}
