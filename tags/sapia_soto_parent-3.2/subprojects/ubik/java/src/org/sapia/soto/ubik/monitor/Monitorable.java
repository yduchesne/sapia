package org.sapia.soto.ubik.monitor;

/**
 * This interface is meant to be implemented by services
 * that expect to be monitored. A single method is specified, which
 * provides a "standard" monitoring hook that can be invoked by
 * a <code>MonitorAgentLayer</code>.
 * 
 * @see org.sapia.soto.ubik.monitor.impl.MonitorAgentLayer
 * 
 * @author yduchesne
 *
 */

public interface Monitorable {
  
  /**
   * Internally implements monitoring checks.
   * @throws Exception if this instance deems itself unfit.
   */
  public void monitor() throws Exception;

}
