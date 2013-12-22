package org.sapia.corus.interop.api;


/**
 * This interface can be implemented by application modules that
 * wish to be notified about shutdown in order to cleanly terminate.
 *
 * @author Yanick Duchesne 
 */
public interface ShutdownListener {
  /**
   * Called when the corus server has requested a VM shutdown.
   */
  public void onShutdown();
}
