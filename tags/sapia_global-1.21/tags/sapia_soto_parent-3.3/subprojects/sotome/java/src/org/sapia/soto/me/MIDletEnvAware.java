/**
 * 
 */
package org.sapia.soto.me;

/**
 * Defines a tag interface for service wanted to get access to the MIDletEnv object (the SotoMe context).
 *
 * @author Jean-Cédric Desrochers
 */
public interface MIDletEnvAware {

  /**
   * Set the MIDlet environment (SotoMe context). Called by the SotoMe runtime on the service implementing
   * this interface.
   *  
   * @param aMIDletEnv The MIDlet environment. 
   */
  public void setMIDletEnv(MIDletEnv aMIDletEnv);
  
}
