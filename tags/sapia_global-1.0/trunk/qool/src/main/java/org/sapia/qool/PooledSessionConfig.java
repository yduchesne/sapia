package org.sapia.qool;

public class PooledSessionConfig {
 
  public boolean transactional;
  public int ackMode;
  
  public PooledSessionConfig setAckMode(int ackMode) {
    this.ackMode = ackMode;
    return this;
  }
  
  public PooledSessionConfig setTransactional(boolean transactional) {
    this.transactional = transactional;
    return this;
  }
}
