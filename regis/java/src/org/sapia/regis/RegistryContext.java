package org.sapia.regis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.sapia.regis.util.PropertiesContext;
import org.sapia.regis.util.Utils;
import org.sapia.util.text.SystemContext;
import org.sapia.util.text.TemplateException;
import org.sapia.util.text.TemplateFactory;

/**
 * An instance of this class internally uses a given <code>RegistryFactory</code> class
 * to instantiate a <code>Registry</code>; it expects the <code>org.sapia.regis.factory</code>
 * property to correspond to the name of the <code>RegistryFactory</code> implementation
 * to use.
 * <p>
 * Additionally, an instance of this class allows setting a property indicating one or multiple
 * Java property resources to load within this instance's given properties (which are passed at
 * instantiation time). That property is: <code>org.sapia.regis.boostrap</code>, and allows
 * setting a comma-delimited list of resources are to be exclusively loaded. Each resource is
 * interpreted as being either (whichever matches first): 
 * <p>
 * <ul>
 *   <li>a file
 *   <li>a URL
 *   <li>a classpath resource
 * </ul>
 * The first resource that can be loaded will stop the loading process (i.e.: the Java properties
 * in that resource will be loaded, but attempting to load the other resources will abort).
 * <p>
 * Note that specified resource names can contain variables of the form <code>${var_name}</code>. These
 * variables are resolved using the properties passed to the instance of this class at construction
 * time, or using system properties.
 * <p>
 * The properties in the resource that was found will be added to the ones passed to the instance
 * of this class at contruction time, prior to the actual registry factory being instantiated.
 * <p>
 * This feature was introduced to allow connecting to different registries, depending on the environment.
 * For example, imagine that a <code>registry.properties</code> file is kept in the user home directory on developer
 * workstations. That file contains the properties necessary to connect to a {@link org.sapia.regis.local.LocalRegistry}
 * (in order to avoid dealing with remote connections when developing). But let's say that when in other environments
 * (dev, QA, prod...), the <code>registry.properties</code> file to use is stored by convention in the classpath, under the
 * path <code>regis/conf/registry.properties</code>, and holds properties used to connect to a remote registry, shared by
 * distributed applications (see {@link org.sapia.regis.remote.RemoteRegistry}).
 * <p> 
 * In order to load the appropriate <code>registry.properties</code>, we could set a property (in the <code>Properties</code> passed to the
 * constructor of this class) :
 * <pre>
 * org.sapia.regis.bootstrap=${user.home}/regis/registry.properties, regis/conf/registry.properties
 * </pre>
 * <p>
 * Then, upon its <code>connect()</code> method being called, the instance of this class resolves the boostrap properties (according
 * to the above-described algorithm), which add themselves to the properties that were passed in at construction time. Thus, the
 * boostrap properties are additive, and are used just as the others when instantiation registry factory.
 * 
 * @see org.sapia.regis.RegistryFactory
 * 
 * @author yduchesne
 *
 */
public class RegistryContext {
  
  /**
   * This constant corresponds to the property that indicates which <code>RegistryFactory</code>
   * to use to instantiate a <code>Registry</code>.
   */
  public static final String FACTORY_CLASS = "org.sapia.regis.factory";
  
  /**
   * This constant corresponds to the property that indicates which Java propertis (file, URL, classpath resource)
   * to use to initialiase an instance of this class.
   */
  public static final String BOOTSTRAP = "org.sapia.regis.bootstrap";  
  
  /**
   * This constant corresponds to the property indicating if properties should be interpolated
   * prior to being returned (defaults to true).
   */
  public static final String INTERPOLATION_ACTIVE = "org.sapia.regis.interpolation.active";
  
  private Properties _props;
  
  /**
   * @param props the <code>Properties</code> to used to connect to the desired
   * <code>Registry</code>.
   */
  public RegistryContext(Properties props){
    _props = props;
  }
  
  /**
   * @return a <code>Registry</code>.
   * @throws Exception if a problem occurs while attempting to connect.
   */
  public Registry connect() throws Exception{
    _props = Utils.replaceVars(new PropertiesContext(_props, new SystemContext()), _props);
    RegisLog.init(_props);
    String bootstrap = _props.getProperty(BOOTSTRAP);
    if(bootstrap != null){
      loadBootstrap(bootstrap);
    }
    String className = _props.getProperty(FACTORY_CLASS);
    if(className == null){
      throw new IllegalArgumentException("Property not specified: " + FACTORY_CLASS + "; got: " + _props);
    }
    RegistryFactory factory = (RegistryFactory)Class.forName(className).newInstance();
    return factory.connect(_props);
  }
  
  private void loadBootstrap(String bootstrap) throws Exception{
    String[] resources = bootstrap.split(",");
    TemplateFactory fac = new TemplateFactory();
    PropertiesContext context = new PropertiesContext(_props, new SystemContext());
    for(int i = 0; i < resources.length; i++){
      String resource = resources[i].trim();
      try{
        resource = fac.parse(resource).render(context);
        Utils.loadProps(RegistryContext.class, _props, resource);
        return;
      }catch(TemplateException e){
        continue;
      }catch(IOException e){
        continue;
      }
    }
    throw new FileNotFoundException("Could not load any bootstrap resource: " + bootstrap);
  }
  

}
