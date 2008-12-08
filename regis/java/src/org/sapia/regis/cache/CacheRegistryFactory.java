package org.sapia.regis.cache;

import java.util.Properties;

import org.sapia.regis.Registry;
import org.sapia.regis.RegistryFactory;

public class CacheRegistryFactory implements RegistryFactory{
  
  /**
   * This constant corresponds to the property indicating the name of the registry
   * factory class to wrap for caching.
   */  
  public static final String FACTORY_CLASS = "org.sapia.regis.cache.factory";
  
  /**
   * This constant corresponds to the property indicating the cache refresh
   * interval, in seconds.
   */
  public static final String REFRESH_INTERVAL = "org.sapia.regis.cache.interval";
  

  public static final int DEFAULT_REFRESH_INTERVAL  = 60 * 2;
  
  public Registry connect(Properties props) throws Exception {
    String intervalProp = (String)props.get(REFRESH_INTERVAL);
    int interval = DEFAULT_REFRESH_INTERVAL;
    if(intervalProp != null){
      interval = Integer.parseInt(intervalProp);
    }
    String className = (String)props.get(FACTORY_CLASS);
    if(className == null){
      throw new IllegalArgumentException("Registry factory class not set - " + FACTORY_CLASS);
    }
    Registry toCache = ((RegistryFactory)Class.forName(className).newInstance()).connect(props);
    CacheRegistry cache = new CacheRegistry(toCache, interval*1000);
    return cache;
  }
}
