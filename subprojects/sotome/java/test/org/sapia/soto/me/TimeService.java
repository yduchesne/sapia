/**
 * 
 */
package org.sapia.soto.me;

import org.sapia.soto.me.ConfigurationException;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class TimeService implements J2meService {

  private String _timezone;
  
  /**
   * Creates a new TimeService instance.
   */
  public TimeService() {
  }
  
  /* (non-Javadoc)
   * @see org.sapia.soto.me.J2meService#handleObject(java.lang.String, java.lang.Object)
   */
  public void handleObject(String aName, Object aValue) throws ConfigurationException {
    if ("timezone".equals(aName)) {
      _timezone = (String) aValue;
      
    } else {
      throw new ConfigurationException("Unable to handle object named '" + aName + "' : " + aValue);
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws Exception {
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.J2meService#pause()
   */
  public void pause() throws Exception {
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
  }
  
  /**
   * Returns the current time in milliseconds.
   * 
   * @return The current time in milliseconds.
   */
  public long getTime() {
    return System.currentTimeMillis();
  }
  
  public String getTimeZone() {
    return _timezone;
  }
}
