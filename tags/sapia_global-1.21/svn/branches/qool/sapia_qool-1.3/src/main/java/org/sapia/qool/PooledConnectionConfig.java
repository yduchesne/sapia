package org.sapia.qool;

public class PooledConnectionConfig {

  public String username, password;
  
  public PooledConnectionConfig setCredentials(String username, String password) {
    this.username = username;
    this.password = password;
    return this;
  }
}
