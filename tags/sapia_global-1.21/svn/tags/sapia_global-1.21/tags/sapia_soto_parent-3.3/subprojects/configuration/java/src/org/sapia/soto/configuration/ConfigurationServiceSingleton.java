package org.sapia.soto.configuration;

import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.NotFoundException;
import org.sapia.soto.configuration.jconfig.ConfigurationDef;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 * 
 *
 * @author Jean-Cedric Desrochers
 */
public class ConfigurationServiceSingleton implements EnvAware, ObjectCreationCallback {

  private Env _env;
  private ConfigurationService _service;
  private ConfigurationException _error;
  
  /* (non-Javadoc)
   * @see org.sapia.soto.EnvAware#setEnv(org.sapia.soto.Env)
   */
  public void setEnv(Env env) {

    
    try {
      _service = (ConfigurationService) env.lookup(ConfigurationService.class);
      
    } catch (NotFoundException nfe) {
      _error = new ConfigurationException("Unable to find the configuration service", nfe);
    }
  }

  public ConfigurationDef createConfiguration() {
    return new ConfigurationDef();
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
