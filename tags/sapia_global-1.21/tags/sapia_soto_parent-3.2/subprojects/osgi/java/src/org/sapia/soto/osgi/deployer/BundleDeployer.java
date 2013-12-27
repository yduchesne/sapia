package org.sapia.soto.osgi.deployer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.sapia.soto.Debug;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.Service;
import org.sapia.soto.osgi.OSGICallback;
import org.sapia.soto.util.matcher.PathPattern;
import org.sapia.soto.util.matcher.Pattern;

/**
 * An instance of this class implements a <code>Deployer</code> that 
 * deploys OSGI bundles. An instance of this class first attempts to 
 * register itself with an existing <code>DeploymentManager</code>. 
 * A <code>BundleDeployer</code> can have its deployment manager specified
 * through dependency injection. If this method is not used, the bundle
 * deployer will attempt looking up a <code>DeploymentManager</code> through
 * its container's environment.
 * <p>
 * Once a bundle deployer has properly registered itself, it attempts acquiring
 * its <code>BundleContext</code> (corresponding to the OSGI implementation in which
 * it evolves). This consists of the bundle deployer looking up an <code>OSGICallback</code>
 * instance (which holds a <code>BundleContext</code>).
 * <p>
 * The <code>BundleContext</code> is used by an instance of this class to perform deployments and
 * undeployments.
 * <p>
 * A bundle deployer should be configured with URL patterns that determine which URLs are
 * "accepted" by it, and with a base directory under which the deployed resources are kept.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class BundleDeployer implements Deployer, Service, EnvAware {
  
  private List _patterns = new ArrayList();
  private DeploymentManager _manager;
  private OSGICallback _callback;
  private Env _env;
  private String _type;
  
  /**
   * Adds a pattern that determines which URLs are "accepted" by this instance.
   * 
   * @param urlPattern a pattern.
   */
  public void addPattern(String urlPattern){
    _patterns.add(PathPattern.parse(urlPattern, true));
  }
  
  /**
   * @param type the logical type of this deployer.
   */
  public void setType(String type){
    _type = type;
  }
  
  /**
   * @see org.sapia.soto.osgi.deployer.Deployer#getType()
   */
  public String getType() {
    return _type;
  }
  
  /**
   * @param manager the <code>DeploymentManager</code> that this instance should add
   * itself to.
   */
  public void setDeploymentManager(DeploymentManager manager){
    _manager = manager;
  }
  
  /**
   * @see org.sapia.soto.EnvAware#setEnv(org.sapia.soto.Env)
   */
  public void setEnv(Env env) {
    _env = env;
  }
  
  /**
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
    _callback = (OSGICallback)_env.lookup(OSGICallback.class);
    if(_manager == null){
      _manager = (DeploymentManager)_env.lookup(DeploymentManager.class);
    }
    if(_type == null){
      throw new IllegalStateException("Deployer type not set");
    }
  }
  
  /**
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws Exception {
    _manager.add(this);
  }
  
  /**
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
    if(_manager != null){
      _manager.remove(this);
    }
  }
  
  /**
   * @see org.sapia.soto.osgi.deployer.Deployer#accepts(java.net.URL)
   */
  public boolean accepts(URL toDeploy) {
    for(int i = 0; i < _patterns.size(); i ++){
      if(((Pattern)_patterns.get(i)).matches(toDeploy.toExternalForm())){
        return true;
      }
    }
    return false;
  }
  
  /**
   * @see org.sapia.soto.osgi.deployer.Deployer#deploy(org.sapia.soto.osgi.deployer.Deployment)
   */
  public void deploy(Deployment toDeploy) throws DeploymentException {
    try{
      if(Debug.DEBUG)
        Debug.debug("Installing bundle: " + toDeploy.getURL().toExternalForm());      
      _callback.getBundleContext().installBundle(toDeploy.getURL().toExternalForm(), toDeploy.getURL().openStream());
    }catch(BundleException e){
      throw new DeploymentException("Could not install bundle: " + toDeploy.getURL().toExternalForm());
    }catch(IOException e){
      throw new DeploymentException("Could not open bundle archive: " + toDeploy.getURL().toExternalForm());
    }
  }
  
  /**
   * @see org.sapia.soto.osgi.deployer.Deployer#undeploy(java.net.URL)
   */
  public void undeploy(URL toUndeploy) throws DeploymentException {
    try{
      Bundle[] bundles = _callback.getBundleContext().getBundles();
      for(int i = 0; i < bundles.length; i++){
        if(bundles[i].getLocation().equals(toUndeploy.toExternalForm())){
          if(Debug.DEBUG)
            Debug.debug("Uninstalling bundle: " + bundles[i].getLocation());
          bundles[i].uninstall();
          break;
        }
      }
    }catch(BundleException e){
      throw new DeploymentException("Could not deploy bundle: " + toUndeploy.toExternalForm());
    }
  }
  
}
