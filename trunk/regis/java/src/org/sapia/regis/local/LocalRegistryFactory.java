package org.sapia.regis.local;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.sapia.regis.RWNode;
import org.sapia.regis.Registry;
import org.sapia.regis.RegistryFactory;
import org.sapia.regis.impl.NodeImpl;
import org.sapia.regis.loader.RegistryConfigLoader;
import org.sapia.regis.util.PropertiesContext;
import org.sapia.regis.util.Utils;
import org.sapia.util.text.SystemContext;

/**
 * This class instantiates an in-memory registry that supports writes, but not transactions
 * (all writes are instantly committed).
 * <p>
 * An instance of this class can be specified the path to an XML configuration resource
 * that must be used to initialize the registry before it is returned:
 * <p>
 * <pre>
 * Properties props = new Properties();
 * props.setProperty(RegistryContext.FACTORY_CLASS, 
 *   LocalRegistryFactory.class.getName());
 * props.setProperty(LocalRegistryFactory.BOOTSTRAP,
 *  "org/acme/registry/config.xml");
 * RegistryContext context = new RegistryContext();
 * Registry regis = context.connect(props);
 * </pre>
 * 
 * <p>
 * Note that the bootstrap property can be optionally specified to indicate a comma-delimited list 
 * of configuration resources that will be loaded at initialization of the local registry. Each resource
 * is interpreted as file (in the file system), a URL, or a classpath resource, whatever works first.
 * 
 * @see org.sapia.regis.local.LocalRegistry
 * 
 * @author yduchesne
 *
 */
public class LocalRegistryFactory implements RegistryFactory{
  
  public static final String BOOTSTRAP = "org.sapia.regis.local.bootstrap";
  
  /**
   * @see RegistryFactory#connect(Properties)
   * @return a <code>LocalRegistry</code> instance. 
   */
  public Registry connect(Properties props) throws Exception {
    RWNode root = new NodeImpl();

    props = Utils.replaceVars(new PropertiesContext(props, new SystemContext()), props);
    String bootstrap = props.getProperty(BOOTSTRAP);

    if(bootstrap != null){
      String[] resources = bootstrap.split(",");
      for(int i = 0; i < resources.length; i++){
        RegistryConfigLoader loader = new RegistryConfigLoader(root);
        InputStream is = Utils.load(LocalRegistryFactory.class, resources[i].trim());
        try{
          Map vars = new HashMap();
          Utils.copyPropsToMap(vars, props);
          loader.load(is, vars);
        }finally{
          is.close();
        }
      }
    }
    return new LocalRegistry(root);
  }
}
