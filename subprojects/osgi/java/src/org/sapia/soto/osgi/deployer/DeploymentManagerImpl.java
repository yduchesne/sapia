package org.sapia.soto.osgi.deployer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.sapia.soto.Debug;
import org.sapia.soto.Service;
import org.sapia.soto.ServiceMetaData;
import org.sapia.soto.SotoContainer;
import org.sapia.soto.osgi.OSGICallback;

/**
 * This class implements the <code>DeploymentManager</code> interface as a Soto service.
 * <p>
 * An instance of this class keeps deployments under a specified base directory. Each
 * <code>Deployer</code> registered with this instance has its own directory created under
 * the base directory. The name of a deployer's directory corresponds to its locigal type.
 * <p>
 * An instance of this class encapsulates a thread that watches this instance's
 * base directory for new uploads (allowing hot-deployment of bundles by simply copying 
 * bundle archives under this instance's directory).
 * <p>
 * When detecting the presence of a new archive, an instance of this class will proceed
 * as follows:
 * <ul>
 *   <li>It will go through its list of deployers, attempting to find one that 
 *   handles the new archive.
 *   <li>If a deployer is found, the detected archive is copied to the deployer's directory
 *   (itself under this instance's base directory).
 *   <li>The deploy() method is then called on the deployer, that is provided with the URL
 *   of the copied archive (which will remain in that directory for has long has the corresponding
 *   application will be deployed.
 * </ul>
 * <p>
 * To update an application, simply place an archive with the same name as the currently deployed
 * application archive under this instance's base directory. Deployers that are provided with URLs 
 * for which they already have existing applications will handle such situations as updates.
 * <p>
 * Furthermore, note that removing an archive from a deployer directory will be interpreted
 * as an "undeployment" - deployer directories are checked periodically by this instance's
 * thread. 
 * <p>
 * Of course, deployments and undeployments can be triggered by directly calling the
 * <code>deploy()</code> and <code>undeploy()</code> methods on an instance of this class,
 * as specified by the <code>DeploymentManager</code> interface. 
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class DeploymentManagerImpl implements DeploymentManager, Service{

  static final long DEFAULT_INTERVAL = 5000;
  private List _deployers = Collections.synchronizedList(new ArrayList());
  private DeploymentThread _deployThread; 
  private File _basedir;
  private long _interval = DEFAULT_INTERVAL;
  private DirWatcherThread _dirWatcher;  
  private DeploymentDb _deployments;  
  
  /**
   * @return this instance's base directory
   */
  public File getBasedir() {
    return _basedir;
  }

  /**
   * @param dir the path to the base directory under which this 
   * instance keeps deployed resources.
   */
  public void setBasedir(String dir){
    _basedir = new File(dir);
    if(!_basedir.exists()){
      _basedir.mkdir();
      if(!_basedir.exists()){
        throw new IllegalStateException("Could not create directory: " + _basedir.getAbsolutePath());
      }
    }      
  }
  
  /**
   * @param millis the interval at which the internal thread that
   * watches the directory wakes up.
   */
  public void setIntervalMillis(long millis){
    _interval = millis;
  }
  
  /**
   * @param seconds the interval (in seconds) at which the internal thread that
   * watches the directory wakes up.
   */  
  public void setInterval(int seconds){
    _interval = seconds * 1000;
  }  
  
  /**
   * @see org.sapia.soto.osgi.deployer.DeploymentManager#add(org.sapia.soto.osgi.deployer.Deployer)
   */
  public void add(Deployer deployer){
    if(deployer.getType() == null){
      throw new IllegalArgumentException("Deployer type not specified (null)");
    }
    if(deployer.getType().indexOf('/') >=0 || deployer.getType().indexOf('\\') > 0){
      throw new IllegalArgumentException("Deployer type must not contain path separator character");
    }
    synchronized(_deployers){
      for(int i = 0; i < _deployers.size(); i++){
        if(((Deployer)_deployers.get(i)).getType().equals(deployer.getType())){
          throw new IllegalArgumentException("A deployer has already been registered for the given type: " + deployer.getType());
        }
      }
      _deployers.add(deployer);
      File deployerDir = new File(_basedir, deployer.getType());
      if(!deployerDir.exists()){
        deployerDir.mkdir();
        if(!deployerDir.exists()){
          throw new IllegalStateException("Could not create directory: " + deployerDir.getAbsolutePath());
        }
      }
    }
  }
  
  /**
   * @see org.sapia.soto.osgi.deployer.DeploymentManager#insert(org.sapia.soto.osgi.deployer.Deployer)
   */
  public void insert(Deployer deployer) {
    synchronized(_deployers){    
      _deployers.add(0, deployer);
    }
  }
  
  /**
   * @see org.sapia.soto.osgi.deployer.DeploymentManager#remove(org.sapia.soto.osgi.deployer.Deployer)
   */
  public void remove(Deployer deployer) {
    _deployers.remove(deployer);
  }
 
  /**
   * @see org.sapia.soto.osgi.deployer.DeploymentManager#deploy(java.net.URL)
   */
  public synchronized DeploymentStatus deploy(URL toDeploy) throws DeploymentException{
    if(_deployThread == null){
      _deployThread = new DeploymentThread(this);
      _deployThread.start();
    }
    
    if(Debug.DEBUG)
      Debug.debug(getClass() + ">> Initiating deployment for: " + toDeploy.toExternalForm());    
    
    List deployers;
    synchronized(_deployers){
      deployers = new ArrayList(_deployers);
    }
    if(Debug.DEBUG)
      Debug.debug(getClass() + ">> Finding appropriate deployer...");
    for(int i = 0; i < deployers.size(); i++){
      Deployer deployer = (Deployer)deployers.get(i);
      if(deployer.accepts(toDeploy)){
        if(Debug.DEBUG)
          Debug.debug(getClass() + ">> Deployer found. Deploying...");        
        DeploymentStatusQueue queue = new DeploymentStatusQueue(toDeploy);
        _deployThread.add(queue, deployer);
        return queue;
      }
    }
    
    throw new DeploymentException("No deployer can handle the resource: " + toDeploy);
  }
  
  /**
   * @see org.sapia.soto.osgi.deployer.DeploymentManager#undeploy(java.net.URL)
   */
  public synchronized void undeploy(URL toUndeploy) throws DeploymentException {
    if(Debug.DEBUG)
      Debug.debug(getClass() + ">> Initiating undeployment for: " + toUndeploy.toExternalForm());    
    synchronized(_deployers){
      for(int i = 0; i < _deployers.size(); i++){
        Deployer deployer = (Deployer)_deployers.get(i);
        if(deployer.accepts(toUndeploy)){
          if(Debug.DEBUG)
            Debug.debug(getClass() + ">> Deployer found. Undeploying...");
          deployer.undeploy(toUndeploy);
          try{
            _dirWatcher.syncUndeploy(deployer.getType(), toUndeploy);
          }catch(IOException e){
            throw new DeploymentException("IO problem syncing deployment database");
          }
        }
      }      
    }
  }

  /**
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
  }
  
  /**
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws Exception {
    _deployments = new DeploymentDb(_basedir);
    _deployments.load();    
    _dirWatcher = new DirWatcherThread(this);
    _dirWatcher.setInterval(_interval);
    _dirWatcher.start();
  }
  
  /**
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
    if(_deployThread != null){
      while(_deployThread.isAlive()){
        _deployThread.interrupt();        
        try{
          _deployThread.join(2000);
        }catch(InterruptedException e){
          break;
        }
      }
    }
    if(_dirWatcher != null){
      while(_dirWatcher.isAlive()){
        _dirWatcher.interrupt();
        try{
          _dirWatcher.join(2000);
        }catch(InterruptedException e){
          break;
        }
      }
    }    
    try{
      if(_deployments != null){
        _deployments.save();
      }
    }catch(IOException e){
      //noop
    }
  }
  
  Deployer getDeployerFor(URL location){
    synchronized(_deployers){
      for(int i = 0; i < _deployers.size(); i++){
        Deployer deployer = (Deployer)_deployers.get(i);
        if(deployer.accepts(location))
          return deployer;
      }      
    }    
    return null;
  }
  
  List getDeployers(){
    return _deployers;
  }  
  
  DeploymentDb getDb(){
    return _deployments;
  }
  
  public static void main(String[] args) {
    try {
      SotoContainer c = new SotoContainer();
      OSGICallback callback = new OSGICallback(){
        
        /**
         * @see org.sapia.soto.osgi.OSGICallback#getBundleContext()
         */
        public BundleContext getBundleContext() {
          return null;
        }
        
      };
      c.bind(new ServiceMetaData(c, "test", callback));
      c.load(new File("etc/osgi/deployer/sapia-osgi-deployer.xml"));
      c.start();
      c.dispose();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}
