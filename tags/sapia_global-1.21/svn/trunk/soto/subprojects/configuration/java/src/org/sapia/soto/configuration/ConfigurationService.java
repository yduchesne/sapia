/*
 * ConfigurationService.java
 *
 * Created on August 10, 2005, 12:10 PM
 */

package org.sapia.soto.configuration;

import java.util.Properties;

/**
 * This interface specifies the behavior of a service that manages configuration.
 *
 * @author yduchesne
 */
public interface ConfigurationService {

  /**
   * @param configName the name of the configuration to search.
   * @param path the path of the property to return.
   * @return the value of the property whose path is given.
   */
  public String getProperty(String configName, String path);
  
  /** 
   * @param configName the name of the configuration to search.
   * @param category the category of the properties to return.
   * @return the <code>Properties</code> corresponding to the given path.
   */  
  public Properties getProperties(String configName, String category);
  
  /**
   * @param configName the name of the configuration to search.
   * @param category the name of the category in which to search
   * @param path the path of the property to return.
   * @return the value of the property whose path is given.
   */
  public String getProperty(String configName, String category, String path);
  
  /**
   * Returns a configuration category from the configuration specified.
   * 
   * @param configName The name of the configuration from which to retrieve the category.
   * @param aName The name of the category to retrieve.
   * @return The configuration category retrieved.
   */
  public ConfigCategory getCategory(String configName, String aName);
  
}
