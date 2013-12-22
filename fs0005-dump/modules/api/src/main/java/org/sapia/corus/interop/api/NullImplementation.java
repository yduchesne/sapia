/*
 * NullImplementation.java
 *
 * Created on October 26, 2005, 9:57 AM
 */

package org.sapia.corus.interop.api;

/**
 * @author yduchesne
 */
public class NullImplementation implements Implementation{
  
  public static final int UNDEFINED_PORT = -1;  
  
  /** Creates a new instance of NullImplementation */
  public NullImplementation() {
  }
  
  @Override
  public String getCorusPid() {
    return System.getProperty(Consts.CORUS_PID);
  }

  @Override
  public String getDistributionName() {
    return System.getProperty(Consts.CORUS_DIST_NAME);
  }

  @Override
  public String getDistributionVersion() {
    return System.getProperty(Consts.CORUS_DIST_VERSION);
  }

  @Override
  public String getDistributionDir() {
    return System.getProperty(Consts.CORUS_DIST_DIR);
  }

  @Override
  public String getCorusHost() {
    return System.getProperty(Consts.CORUS_SERVER_HOST);
  }

  @Override
  public int getCorusPort() {
    if (System.getProperty(Consts.CORUS_SERVER_PORT) != null) {
      return Integer.parseInt(System.getProperty(Consts.CORUS_SERVER_PORT));
    }

    return UNDEFINED_PORT;
  }

  @Override
  public boolean isDynamic() {
    return false;
  }

  @Override
  public void restart(){}

  @Override
  public void shutdown() {}

  @Override
  public void addShutdownListener(ShutdownListener listener){}
  
  @Override
  public synchronized void addStatusRequestListener(StatusRequestListener listener) {}  
  
  @Override
  public void addProcessEventListener(ProcessEventListener listener) {}
}
