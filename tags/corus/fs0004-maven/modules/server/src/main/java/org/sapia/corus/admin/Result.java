package org.sapia.corus.admin;

import org.sapia.ubik.net.ServerAddress;

public class Result<T> {

  private ServerAddress origin;
  private T data;
  
  
  public Result(ServerAddress origin, T data) {
    this.origin = origin;
    this.data = data;
  }
  
  public T getData() {
    return data;
  }
  
  public ServerAddress getOrigin() {
    return origin;
  }
}
