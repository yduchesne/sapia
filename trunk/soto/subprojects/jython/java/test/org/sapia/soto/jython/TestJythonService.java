/*
 * TestJythonService.java
 *
 * Created on December 1, 2005, 2:46 PM
 */

package org.sapia.soto.jython;

/**
 *
 * @author yduchesne
 */
public interface TestJythonService {
  
  public boolean isInit();
  
  public boolean isStarted();
  
  public boolean isDisposed();
  
  public void printMessage(String msg);
  
}
