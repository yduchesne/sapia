package org.sapia.soto.log;

import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.NotFoundException;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 * 
 *
 * @author Jean-Cedric Desrochers
 */
public class LogServiceSingleton implements EnvAware, ObjectCreationCallback {

  private Env _env;
  private LogService _service;
  private ConfigurationException _error;
  
  public void addLoggerDef(LoggerDef aDef) {
    if (_service != null) {
      _service.addLoggerDef(aDef);
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.EnvAware#setEnv(org.sapia.soto.Env)
   */
  public void setEnv(Env env) {
    _env = env;
    try {
      _service = (LogService) _env.lookup(LogService.class);
    } catch (NotFoundException nfe) {
      _error = new ConfigurationException("Could not find the log service", nfe);
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    if (_error != null) {
      throw _error;
    } else {
      return this;
    }
  }
}
