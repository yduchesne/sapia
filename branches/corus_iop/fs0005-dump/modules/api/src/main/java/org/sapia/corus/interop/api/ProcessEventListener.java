package org.sapia.corus.interop.api;

import org.sapia.corus.interop.ProcessEvent;

/**
 * An instance of this interface is notified upon new {@link ProcessEvent}s being received.
 * 
 * @author yduchesne
 *
 */
public interface ProcessEventListener {
  
  /**
   * Invoked when a new process event has been received - implementations should avoid blocking in 
   * this method, and should therefore delegate any time-consuming logic to a separate thread.
   * 
   * @param evt a {@link ProcessEvent}.
   */
  public void onProcessEvent(ProcessEvent evt);

}
