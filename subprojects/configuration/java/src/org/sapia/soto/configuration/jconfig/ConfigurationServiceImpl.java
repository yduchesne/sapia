/*
 * ConfigurationServiceImpl.java
 *
 * Created on August 10, 2005, 12:11 PM
 *
 */

package org.sapia.soto.configuration.jconfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import org.jconfig.Category;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;
import org.jconfig.NestedCategory;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.Service;
import org.sapia.soto.configuration.ConfigCategory;
import org.sapia.soto.configuration.ConfigurationService;
import org.sapia.resource.Resource;
import org.sapia.resource.ResourceHandler;
import org.sapia.resource.ResourceNotFoundException;
import org.sapia.soto.util.Utils;

/**
 * This class implements the <code>ConfigurationService</code> interface on
 * top of the <a href="http://www.jconfig.org/">JConfig</a> toolkit.
 *
 * @author yduchesne
 */
public class ConfigurationServiceImpl implements 
  Service, ConfigurationService, ResourceHandler, EnvAware{
  
  private static int CONF_NAME  = 0;
  private static int CAT_NAME   = 1;
  private static int PROP_NAME  = 2;

  private ConfigurationManager _manager = ConfigurationManager.getInstance();
  private Env _env;
  
  /** Creates a new instance of ConfigurationServiceImpl */
  public ConfigurationServiceImpl() {
  }
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public ConfigurationDef createConfiguration(){
    return new ConfigurationDef();
  }
  
  /////// Service interface methods ///////

  public void init() throws Exception {
    _env.getResourceHandlers().prepend(this);
  }
  
  public void start() throws Exception {
  }

  public void dispose() {
  }
  
  /////// Configuration service interface methods ///////
  
  public String getProperty(String configName, String path){
    Configuration config = ConfigurationManager.getConfiguration(configName);
    if(config != null){
      return config.getProperty(path);
    }
    return null;
  }
  
  public Properties getProperties(String configName, String category){
    Configuration config = ConfigurationManager.getConfiguration(configName);
    if(config != null){
      Properties props = config.getProperties(category);
      if(props != null){
        return props;
      }
    } 
    return new Properties();
  }
  
  public String getProperty(String configName, 
                            String category, 
                            String path){
    Configuration config = ConfigurationManager.getConfiguration(configName);
    if(config != null){
      return config.getProperty(path, null, category);
    }
    return null;
  }
 
  /* (non-Javadoc)
   * @see org.sapia.soto.configuration.ConfigurationService#getCategory(java.lang.String, java.lang.String)
   */
  public ConfigCategory getCategory(String aConfigName, String aName) {
    Configuration config = ConfigurationManager.getConfiguration(aConfigName);
    
    if(config != null && !config.isNew()) {
      Category parentCategory = null;
      String categoryName = aName;
      
      // Navigate the category hierarchy specified in the name 
      while (categoryName.indexOf("/") != -1) {
        int index = categoryName.indexOf("/");
        String childName = categoryName.substring(0, index);
        categoryName = categoryName.substring(index + 1);
        
        parentCategory = getChildCategory(config, parentCategory, childName);
        if (parentCategory == null) {
          return null;
        }
      }
      
      // Get the last category of the hierarchy
      Category category = getChildCategory(config, parentCategory, categoryName);
      if (category != null) {
        return new JConfigCategoryAdapter(category);
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  /**
   * Utility method to retrieve the child category.
   * 
   * @param aConfig The configuration object.
   * @param aParent The parent category.
   * @param aName The name of the category.
   * @return The retrieve category or <code>null</code> if none is found.
   */
  private Category getChildCategory(Configuration aConfig, Category aParent, String aName) {
    Category child = null;
    if (aParent == null && aConfig.containsCategory(aName)) {
      child = aConfig.getCategory(aName);
    } else if (aParent != null && aParent instanceof NestedCategory) {
      child = ((NestedCategory) aParent).getCategory(aName);
    }
    
    return child;
  }
  
  /////// ResourceHandler interface methods ///////
  
  public boolean accepts(URI uri) {
    return accepts(uri.toString());
  }
  
  public boolean accepts(String uri) {
    if(Utils.hasScheme(uri)){
      if(uri.startsWith(JConfigResource.SCHEME)){
        return true;
      }
      return false;
    }
    if(uri.length() > 0 && uri.charAt(0) == '/'){
      uri = uri.substring(1);
    }
    int i = uri.indexOf('/');
    if(i > 0){
      String configName = uri.substring(0, i);
      String[] configs = _manager.getConfigurationNames();
      if(configs == null) return false;
      for(i = 0; i < configs.length; i++){
        if(configs[i].equals(configName)){
          return true;
        }
      }
    }
    return false;
  }
  
  public Resource getResourceObject(String uri) throws IOException {
    if(Utils.hasScheme(uri)){
      uri = Utils.chopScheme(uri);
    }    
    if(uri.length() > 0 && uri.charAt(0) == '/'){
      uri = uri.substring(1);
    }    
    String[] parts = Utils.split(uri, '/', true);
    if(parts.length != 3){
      throw new ResourceNotFoundException(uri);
    }
    Configuration conf = ConfigurationManager.getConfiguration(parts[CONF_NAME]);
    if(conf == null){
      throw new ResourceNotFoundException(uri);
    }    
    if(!conf.containsCategory(parts[CAT_NAME])){
      throw new ResourceNotFoundException(uri);
    }
    Category cat = conf.getCategory(parts[CAT_NAME]);
    if(cat.getProperty(parts[PROP_NAME]) != null){
      JConfigResource res = new JConfigResource(parts[CONF_NAME], 
        parts[CAT_NAME], parts[PROP_NAME]);  
      return res;
    }
    throw new ResourceNotFoundException(uri);
  }

  public InputStream getResource(String uri) throws IOException {
    return getResourceObject(uri).getInputStream();
  }
    
}
