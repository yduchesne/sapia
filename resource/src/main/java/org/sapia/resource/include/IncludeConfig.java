package org.sapia.resource.include;

import org.sapia.resource.ResourceCapable;

/**
 * An instance of this class encapsulates application specific objects necessary to perform resource-inclusion
 * operations.
 * 
 * @see org.sapia.resource.include.IncludeState#createConfig(String, IncludeContextFactory, ResourceCapable)
 * 
 * @author yduchesne
 *
 */
public class IncludeConfig {

  private String appKey;
  private IncludeContextFactory factory;
  private ResourceCapable resources;
  
  IncludeConfig(String appKey, 
      IncludeContextFactory fac, ResourceCapable resources){
    this.appKey = appKey;
    this.factory = fac;
    this.resources = resources;
  }
  
  /**
   * @return this instance's application key.
   */
  public String getAppKey() {
    return appKey;
  }
  
  /**
   * @return this instance's <code>IncludeContextFactory</code>.
   */
  public IncludeContextFactory getFactory() {
    return factory;
  }
  
  /**
   * @return this instance's <code>ResourceCapable</code> object.
   */
  public ResourceCapable getResources() {
    return resources;
  }
  
}
