package org.sapia.corus.interop.client;

public class ProtocolAlreadySetException extends IllegalStateException{
  
  private static final long serialVersionUID = -4144037795526442479L;

  public ProtocolAlreadySetException() {
    super("Interop protocol implementation already set");
  }

}
