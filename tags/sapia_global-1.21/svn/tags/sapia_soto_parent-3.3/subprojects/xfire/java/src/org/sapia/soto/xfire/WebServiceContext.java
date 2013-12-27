package org.sapia.soto.xfire;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.TransportManager;

/**
 * An instance of this class is used by the {@link org.sapia.soto.xfire.XFireLayer} as
 * a hook to the {@link org.sapia.soto.xfire.SotoXFireServlet}.
 * 
 * @author yduchesne
 *
 */
public interface WebServiceContext {
  
  /**
   * Registers the given service.
   * 
   * @param service a <code>Service</code>
   * @throws Exception
   */
  public void register(Service service) throws Exception;
  
  /**
   * @return the current <code>TransportManager</code>.
   * @throws Exception
   */
  public TransportManager getTransportManager() throws Exception;
  
  

}
