package org.sapia.corus.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.sapia.corus.client.common.CompositeStrLookup;
import org.sapia.corus.client.common.PropertiesStrLookup;
import org.sapia.corus.util.IOUtils;
import org.sapia.corus.util.PropertiesFilter;
import org.sapia.corus.util.PropertiesTransformer;
import org.sapia.corus.util.PropertiesUtil;

/**
 * Helper class used to load the Corus properties.
 * 
 * @author yduchesne
 *
 */
class CorusPropertiesLoader {

  private CorusPropertiesLoader() {
  }
  
  /**
   * @param corusConfigFile the Corus config file.
   * @return the Properties that were loaded.
   * @throws IOException if an IO error occurs trying to load the given properties in
   * the given file.
   */
  static Properties load(File corusConfigFile) throws IOException {
    FileInputStream config = null;
    try {
      config = new FileInputStream(corusConfigFile);
      return doLoad(config);
    } finally {
      try {
        if (config != null) {
          config.close();
        }
      } catch (IOException e) {
        // noop
      }
    }
  }
  
  private static Properties doLoad(FileInputStream config) throws IOException {
    final Properties props = new Properties();
    InputStream defaults = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/sapia/corus/default.properties");
    if(defaults == null){
      throw new IllegalStateException("Resource 'org/sapia/corus/default.properties' not found");
    }
    
    InputStream tmp = IOUtils.replaceVars(new PropertiesStrLookup(System.getProperties()), defaults);
    defaults.close();
    props.load(tmp);
    
    CompositeStrLookup lookup = new CompositeStrLookup();
    lookup.add(new PropertiesStrLookup(props)).add(new PropertiesStrLookup(System.getProperties()));
    
    
    tmp = IOUtils.replaceVars(lookup, config);
    props.load(tmp);
    
    // transforming Corus properties that correspond 1-to-1 to Ubik properties into their Ubik counterpart
    PropertiesUtil.transform(
        props, 
        PropertiesTransformer.MappedPropertiesTransformer.createInstance()
          .add(CorusConsts.PROPERTY_CORUS_ADDRESS_PATTERN, org.sapia.ubik.rmi.Consts.IP_PATTERN_KEY)
          .add(CorusConsts.PROPERTY_CORUS_MCAST_ADDRESS,  org.sapia.ubik.rmi.Consts.MCAST_ADDR_KEY)
          .add(CorusConsts.PROPERTY_CORUS_MCAST_PORT,       org.sapia.ubik.rmi.Consts.MCAST_PORT_KEY)
    );
    
    // copying Ubik-specific properties to the System properties. 
    PropertiesUtil.copy(
        PropertiesUtil.filter(props, PropertiesFilter.NamePrefixPropertiesFilter.createInstance("ubik")), 
        System.getProperties()
    );

    return props;
  }
  
}
