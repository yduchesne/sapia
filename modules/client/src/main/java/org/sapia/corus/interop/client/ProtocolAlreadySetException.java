package org.sapia.corus.interop.client;

public class ProtocolAlreadySetException extends IllegalStateException{
  
  public ProtocolAlreadySetException() {
    super("Interop protocol implementation already set");
  }

}
