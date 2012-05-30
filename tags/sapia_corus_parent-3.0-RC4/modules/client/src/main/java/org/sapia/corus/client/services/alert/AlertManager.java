package org.sapia.corus.client.services.alert;


/**
 * Specifies the behavior of the module that sends alerts.
 * 
 * @author yduchesne
 *
 */
public interface AlertManager {

  public static String ROLE = AlertManager.class.getName();
  
  /**
   * Identifies the various alert levels.
   * 
   * @author yduchesne
   *
   */
  public enum AlertLevel {
    
    INFO,
    WARNING,
    ERROR, 
    FATAL
    
  }
  
}
