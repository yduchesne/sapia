package org.sapia.soto.ubik.example;

import org.sapia.soto.ConfigurationException;
import org.sapia.soto.Service;

/**
 * @author Yanick Duchesne 1-Oct-2003
 */
public class UbikClient implements Service, Runnable {
  private UbikService _svc;

  /**
   * Constructor for UbikClient.
   */
  public UbikClient() {
    super();
  }

  public void setUbikService(UbikService svc) {
    _svc = svc;
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
    if(_svc == null) {
      throw new ConfigurationException(
          "This instance was not initialized with a UbikService instance");
    }

    Thread t = new Thread(this);
    t.setDaemon(true);
    t.start();
  }

  public void run() {
    try {
      while(true) {
        _svc.ping();
        System.out.println("Ping successfull");
        Thread.sleep(1500);
      }
    } catch(Throwable e) {
      e.printStackTrace();
    }
  }
}
