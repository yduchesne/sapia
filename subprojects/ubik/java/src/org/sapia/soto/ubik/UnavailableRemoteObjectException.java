/*
 * UnavailableRemoteObjectException.java
 *
 * Created on August 18, 2005, 3:16 PM
 *
 */

package org.sapia.soto.ubik;

/**
 * Thrown when a remote object could not be found by the ProxyService.
 *
 * @see ProxyService
 *
 * @author yduchesne
 */
public class UnavailableRemoteObjectException extends RuntimeException{
  
  /** Creates a new instance of UnavailableRemoteObjectException */
  public UnavailableRemoteObjectException(String msg) {
    super(msg);
  }
  
}
