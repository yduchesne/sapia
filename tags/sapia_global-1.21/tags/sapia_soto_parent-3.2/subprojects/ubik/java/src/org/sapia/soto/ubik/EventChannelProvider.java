package org.sapia.soto.ubik;

import org.sapia.ubik.mcast.EventChannel;

/**
 * An instance of this class holds an <code>EventChannel</code> that can
 * be used for basic group/domain communications (such as publish/discovery
 * notifications in a distributed computing environment).
 * 
 * @author yduchesne
 *
 */
public interface EventChannelProvider {
  
  /**
   * @return the <code>EventChannel</code> held by this instance.
   */
  public EventChannel getEventChannel();
  

}
