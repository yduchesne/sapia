package org.sapia.soto.me;

/**
 * This interface represents a service specialized for a J2ME environment.
 *
 * @author Jean-Cédric Desrochers
 */
public interface J2meService {
  /**
   * Performs initialization actions.
   * 
   * @throws Exception if a problem occurs while initializing.
   */
  public void init() throws Exception;

  /**
   * Starts this instance.
   * 
   * @throws Exception if a problem occurs while starting.
   */
  public void start() throws Exception;

  /**
   * Pauses this service. The MIDlet soto container may request that a service
   * is suspended for some reason. When called, the service should release any
   * resources before going in "sleep mode".
   * 
   * Once a service is paused, the Soto container will resume the service (at
   * some point in time) by calling the {@link Service#start()} method again. At that
   * moment the service should reacquire any resources required to resume normal
   * behavior. 
   * 
   * @throws Exception if a problem occurs while pausing the service.
   */
  public void pause() throws Exception;

  /**
   * Shuts down this service.
   */
  public void dispose();
  
  /**
   * This method allow injection of objects (attributes/services) to a J2ME service. This
   * "manual" mechanism is somewhat necessary in the J2ME environment since there is no
   * reflexion API available to introspect methods of a class.
   * 
   * @param aName The name of the object to assign to this service.
   * @param aValue The value to assign.
   * @throws ConfigurationException If this service is not able to assign the value passed in.
   */
  public void handleObject(String aName, Object aValue) throws ConfigurationException;
}
