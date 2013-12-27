package org.sapia.soto.configuration;

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
public class ConfigCategoryRef implements EnvAware, ObjectCreationCallback {

  private Env _env;
  
  private String _configName;
  
  private String _categoryName;
  
  public ConfigCategoryRef() {
  }

  /**
   * @return Returns the categoryName.
   */
  public String getCategoryName() {
    return _categoryName;
  }
  
  /**
   * @param aCategoryName The categoryName to set.
   */
  public void setCategoryName(String aCategoryName) {
    _categoryName = aCategoryName;
  }
  
  public void setCat(String cat) {
    setCategoryName(cat);
  }
  
  /**
   * @return Returns the configName.
   */
  public String getConfigName() {
    return _configName;
  }
  
  /**
   * @param aConfigName The configName to set.
   */
  public void setConfigName(String aConfigName) {
    _configName = aConfigName;
  }
  
  public void setConf(String conf) {
    setConfigName(conf);
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.EnvAware#setEnv(org.sapia.soto.Env)
   */
  public void setEnv(Env aEnv) {
    _env = aEnv;
  }

  /* (non-Javadoc)
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    // Validation
    if (_configName == null) {
      throw new ConfigurationException("The configuration name of the category is not specified");
    } else if (_categoryName == null) {
      throw new ConfigurationException("The category name of the category is not specified");
    }
    
    try {
      ConfigurationService service = (ConfigurationService) _env.lookup(ConfigurationService.class);
      ConfigCategory category = service.getCategory(_configName, _categoryName);
      if (category == null) {
        throw new ConfigurationException("No configuration category found for the name " + _categoryName + " ont the configuration " + _configName);
      } else {
        return category;
      }
      
    } catch (NotFoundException nfe) {
      throw new ConfigurationException("Could not find the configuration service", nfe);
    }
  }
}
