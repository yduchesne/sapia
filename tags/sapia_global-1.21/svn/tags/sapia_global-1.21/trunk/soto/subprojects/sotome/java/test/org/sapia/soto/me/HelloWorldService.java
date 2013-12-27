/**
 * 
 */
package org.sapia.soto.me;

import java.util.Date;

import org.sapia.soto.me.ConfigurationException;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class HelloWorldService implements J2meService, MIDletEnvAware {

  private MIDletEnv _env;
  
  private TimeService _timeService;
  
  /**
   * Creates a new HelloWorldService instance.
   */
  public HelloWorldService() {
    print("Creating Hello World!!!");
  }
  
  /* (non-Javadoc)
   * @see org.sapia.soto.me.J2meService#handleObject(java.lang.String, java.lang.Object)
   */
  public void handleObject(String aName, Object aValue) throws ConfigurationException {
    if ("timeService".equals(aName)) {
      if (aValue instanceof TimeService) {
        _timeService = (TimeService) aValue;
      } else {
        throw new ConfigurationException("HelloWorldService: Unable to handle object named '" + aName +
                "' - the value is not a TimeService : " + aValue);
      }
    } else if ("property".equals(aName)) {
      print("handling property " + aValue);
      
    } else {
      throw new ConfigurationException("HelloWorldService: Unable to handle object named '" + aName +
              "' - unrecognized name for value : " + aValue);
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.J2meService#pause()
   */
  public void pause() throws Exception {
    print("Pause Hello World!!!");
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
    print("Dispose Hello World!!!");
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
    print("Init Hello World!!!");
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws Exception {
    if (!_env.getMetaDataFor(this).getLifeCycle().isPaused()) {
      print("Starting Hello World!!!");
    } else {
      print("Resume Hello World!!!");
    }
  }

  protected void print(String aMessage) {
    if (_timeService == null) {
      System.out.println(aMessage);
    } else {
      Date now = new Date(_timeService.getTime());
      System.out.println(now + " [" + _timeService.getTimeZone() + "] : " + aMessage);
    }
  }
  
  /* (non-Javadoc)
   * @see org.sapia.soto.me.MIDletEnvAware#setMIDletEnv(org.sapia.soto.me.MIDletEnv)
   */
  public void setMIDletEnv(MIDletEnv aMIDletEnv) {
    _env = aMIDletEnv;
  }

}
