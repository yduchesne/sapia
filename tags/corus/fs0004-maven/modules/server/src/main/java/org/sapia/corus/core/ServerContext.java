package org.sapia.corus.core;

import java.io.IOException;
import java.util.Properties;

import org.sapia.corus.client.Corus;
import org.sapia.ubik.net.TCPAddress;

public interface ServerContext {

  /**
   * @return this instance's {@link Corus}.
   */
  public Corus getCorus();
  
  /**
   * @return the name of the Corus server.
   */
  public String getServerName();
  
  /**
   * @param serverName a name to assign to this Corus server.
   */
  public void overrideServerName(String serverName);
  
  /**
   * @return the home directory of the Corus server.
   */
  public String getHomeDir();
  
  /**
   * @return the domain of the Corus server.
   */
  public String getDomain();
  
  public Properties getProcessProperties() throws IOException;
  
  /**
   * @return the address of the Corus server corresponding to this
   * instance.
   */
  public TCPAddress getServerAddress();
  
  /**
   * @return the {@link CorusTransport}.
   */
  public CorusTransport getTransport();
  
  /**
   * @return the {@link InternalServiceContext} containing the services
   * of the Corus server.
   */
  public InternalServiceContext getServices();
  
  /**
   * Looks up a service of the given interface (internally delegates the call to
   * this instances {@link InternalServiceContext}.
   * @param <S> a service interface type
   * @param serviceInterface the service interface for which to find a service instance.
   * @return the service instance that was found.
   */
  public <S> S lookup(Class<S> serviceInterface);
  
}