package org.sapia.qool;

import javax.jms.Connection;
import javax.jms.Session;

/**
 * Holds various configuration parameters.
 * 
 * @author yduchesne
 *
 */
public class Config {

  public static final int NO_MAX = -1;
  
  public static final int DEFAULT_MAX_CONNECTIONS = 10;
  public static final int DEFAULT_MAX_SESSIONS    = 10;

  private int maxConnections = DEFAULT_MAX_CONNECTIONS;
  private int maxSessions = DEFAULT_MAX_SESSIONS;
  private boolean registerShutdownHooks = true;
  private Debug.Level debugLevel = Debug.Level.ERROR;
   
  /**
   * @return the maximum number of pooled {@link Connection}s to pool.
   */
  public int getMaxConnections() {
    return maxConnections;
  }

  public void setMaxConnections(int maxConnections) {
    this.maxConnections = maxConnections;
  }

  /**
   * @return the maximum number of {@link Session}s to pool.
   */
  public int getMaxSessions() {
    return maxSessions;
  }

  public void setMaxSessions(int maxSessions) {
    this.maxSessions = maxSessions;
  }

  /**
   * @return indicates to the {@link PooledJmsConnectionFactory} that it should register a VM
   * shutdown hook in order to detect VM termination and proceed to closing its pooled {@link Connection}s.
   * (defaults to <code>true</code>; <b>recommended</b>).
   */
  public boolean isRegisterShutdownHooks() {
    return registerShutdownHooks;
  }

  public void setRegisterShutdownHooks(boolean registerShutdownHooks) {
    this.registerShutdownHooks = registerShutdownHooks;
  }
  
  /**
   * @param debugLevel a debug level.
   */
  public void setDebugLevel(Debug.Level debugLevel) {
    this.debugLevel = debugLevel;
  }
  
  public Debug.Level getDebugLevel() {
    return debugLevel;
  }
}

