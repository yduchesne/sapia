package org.sapia.soto.configuration;

import java.util.List;
import java.util.Properties;

/**
 * 
 *
 * @author Jean-Cedric Desrochers
 */
public interface ConfigCategory {

  /**
   * Returns the name of this configuration category.
   * 
   * @return The name of this configuration category.
   */
  public String getName();
  
  /**
   * Returns the value of the property name passed in fromthis configuration category.
   * 
   * @param aName The name of the property to get.
   * @return The value retrieved or <code>null</code> if no value is found.
   */
  public String getProperty(String aName);
  
  /**
   * Returns the properties of this category as a proeprties object.
   * 
   * @return The properties of this category as a proeprties object.
   */
  public Properties getProperties();
  
  /**
   * Returns the child configuration category of this category whose name is
   * the category name passed in.
   *  
   * @param aName The name of the child category to retrieve. 
   * @return The category retrieved.
   */
  public ConfigCategory getCategory(String aName);
  
  /**
   * Returns a list containing all the child categories of this cateogry.
   *  
   * @return The list of <code>ConfigCategory</code> objects.
   */
  public List getCategories();
}
