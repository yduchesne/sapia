package org.sapia.regis.prevayler;

import java.io.File;
import java.util.Properties;

import org.sapia.regis.Registry;
import org.sapia.regis.RegistryFactory;

public class PrevaylerRegistryFactory implements RegistryFactory{

  /**
   * This constant corresponds to the base directory where the registry's persistent state will
   * be kept. 
   */
  public static final String BASE_DIR           = "org.sapia.regis.prevayler.basedir";
  
  /**
   * This constant indicates if the content of the registry's persistent directory should
   * be deleted at startup. 
   */
  public static final String DELETE_ON_STARTUP  = "org.sapia.regis.prevayler.deleteOnStartup";
  
  /**
   * This corresponds to the default base directory of the registry's persistent state (the
   * directory is ${user.home}/.sapia_regis_prevayler).
   */
  public static final String DEFAULT_BASE_DIR   = System.getProperty("user.home")+File.separator+".sapia_regis_prevayler";
  
  public Registry connect(Properties props) throws Exception {
    String baseDir = props.getProperty(BASE_DIR, DEFAULT_BASE_DIR);
    String deleteOnStartup = props.getProperty(DELETE_ON_STARTUP, "false");    
    File fBaseDir = new File(baseDir);
    fBaseDir.mkdirs();
    PrevaylerRegistry registry = new PrevaylerRegistry(fBaseDir.getAbsolutePath(), 
        new Boolean(deleteOnStartup).booleanValue());
    return registry;
  }
}
