package org.sapia.soto.ubik.example;

import org.sapia.soto.Service;

import org.sapia.ubik.rmi.server.Stateless;

/**
 * @author Yanick Duchesne 1-Oct-2003
 */
public class UbikServer implements UbikService, Service, Stateless {
  /**
   * Constructor for UbikService.
   */
  public UbikServer() {
    super();
  }

  /**
   * @see org.sapia.soto.ubik.example.UbikService#ping()
   */
  public void ping() {
    System.out.println("Ping received!!!!! @ "
        + Integer.toHexString(super.hashCode()));
  }

  /**
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
  }

  /**
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
  }

  /**
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws Exception {
  }
}
