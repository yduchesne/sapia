package org.sapia.soto.ubik.monitor;

import java.util.Properties;

/**
 * This interface is meant to be implemented by services
 * that expect to be monitored. It is similar to the Monitorable
 * interface, so it defines a single method which
 * provides a "standard" monitoring hook that can be invoked by
 * a <code>MonitorAgentLayer</code>. However, this interface defines
 * a result of a Properties object that give to the monitored object
 * the ability to publish some information
 * (identifiers, statistics, threshold, ...).
 * 
 * @see org.sapia.soto.ubik.monitor.impl.MonitorAgentLayer
 * @see org.sapia.soto.ubik.monitor.Monitorable
 * 
 * @author jcdesrochers
 *
 */
public interface FeedbackMonitorable {
  
  /**
   * Internally implements monitoring checks and returns status information
   * in the properties.
   * 
   * @return The result properties of the monitor call
   * @throws Exception if this instance deems itself unfit.
   */
  public Properties monitor() throws Exception;

}
