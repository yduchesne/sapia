/**
 * 
 */
package org.sapia.soto.me.xml;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class NullObject {

  /** The static singleton instance. */
  private static final NullObject _SINGLETON = new NullObject();

  /**
   * Returns the static singleton instance of a NullObject.
   * 
   * @return The static singleton instance of a NullObject.
   */
  public static NullObject getInstance() {
    return _SINGLETON;
  }

  /**
   * Creates a new NullObject instance.
   */
  private NullObject() {
  }
}
