package org.sapia.corus.deployer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sapia.corus.client.annotations.Bind;
import org.sapia.corus.client.common.Arg;
import org.sapia.corus.client.common.IDGenerator;
import org.sapia.corus.client.common.ProgressQueue;
import org.sapia.corus.client.common.ProgressQueueImpl;
import org.sapia.corus.client.exceptions.deployer.ConcurrentDeploymentException;
import org.sapia.corus.client.exceptions.deployer.DistributionNotFoundException;
import org.sapia.corus.client.exceptions.deployer.RunningProcessesException;
import org.sapia.corus.client.services.cluster.ClusterManager;
import org.sapia.corus.client.services.deployer.Deployer;
import org.sapia.corus.client.services.deployer.DeployerConfiguration;
import org.sapia.corus.client.services.deployer.dist.Distribution;
import org.sapia.corus.client.services.deployer.transport.AbstractDeploymentClient;
import org.sapia.corus.client.services.deployer.transport.ClientDeployOutputStream;
import org.sapia.corus.client.services.deployer.transport.DeployOutputStream;
import org.sapia.corus.client.services.deployer.transport.DeploymentClientFactory;
import org.sapia.corus.client.services.deployer.transport.DeploymentMetadata;
import org.sapia.corus.client.services.event.EventDispatcher;
import org.sapia.corus.client.services.http.HttpModule;
import org.sapia.corus.client.services.processor.Processor;
import org.sapia.corus.core.CorusRuntime;
import org.sapia.corus.core.ModuleHelper;
import org.sapia.corus.core.ServerStartedEvent;
import org.sapia.corus.deployer.task.BuildDistTask;
import org.sapia.corus.deployer.task.DeployTask;
import org.sapia.corus.deployer.task.UndeployTask;
import org.sapia.corus.deployer.transport.Deployment;
import org.sapia.corus.deployer.transport.DeploymentConnector;
import org.sapia.corus.deployer.transport.DeploymentProcessor;
import org.sapia.corus.taskmanager.core.TaskConfig;
import org.sapia.corus.taskmanager.core.TaskLogProgressQueue;
import org.sapia.corus.taskmanager.core.TaskManager;
import org.sapia.ubik.net.ServerAddress;
import org.sapia.ubik.net.TCPAddress;
import org.sapia.ubik.rmi.interceptor.Interceptor;
import org.sapia.ubik.rmi.replication.ReplicationStrategy;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * This component implements the {@link Deployer} interface.
 *
 * @author Yanick Duchesne
 */
@Bind(moduleInterface=Deployer.class)
public class DeployerImpl extends ModuleHelper implements Deployer,
  DeploymentConnector, Interceptor {
  /**
   * The file lock timeout property name (<code>file-lock-timeout</code>).
   */
  public static final String LOCK_TIMEOUT = "file-lock-timeout";

  /**
   * The default deployment directory (<code>user.dir/deploy</code>).
   */
  public static final String DEFAULT_DEPLOY_DIR = CorusRuntime.getCorusHome() +
    java.io.File.separator + "deploy";

  /**
   * The default temp directory (<code>user.dir/tmp</code>).
   */
  public static final String DEFAULT_TMP_DIR = CorusRuntime.getCorusHome() +
    java.io.File.separator + "tmp";

  @Autowired
  private EventDispatcher _events;
  
  @Autowired
  private HttpModule _http;
  
  @Autowired
  private TaskManager _taskman;
    
  @Autowired
  private ClusterManager _cluster;
  
  @Autowired
  private DeployerConfiguration _configuration;
  
  private Map<String, FileLock> _deployLocks = new HashMap<String, FileLock>();
  private DeploymentProcessor _processor;
  private DistributionDatabase   _store;

  
  public DeployerConfiguration getConfiguration() {
    return _configuration;
  }
  

  @Override
  public void init() throws Exception {
    
    _store = new DistributionDatabaseImpl();
    
    services().bind(DistributionDatabase.class, _store);
    
    String pattern = CorusRuntime.getCorus().getDomain() + '_' +
      ((TCPAddress) CorusRuntime.getTransport().getServerAddress()).getPort();
    
    DeployerConfigurationImpl config = new DeployerConfigurationImpl();
    config.setFileLockTimeout(_configuration.getFileLockTimeout());
    
    if (_configuration.getDeployDir() != null) {
      config.setDeployDir(_configuration.getDeployDir() + File.separator + pattern);
    } else {
      config.setDeployDir(DEFAULT_DEPLOY_DIR + File.separator + pattern);
    }

    if (_configuration.getTempDir() != null) {
      config.setTempDir(_configuration.getTempDir() + File.separator + pattern);
    } else {
      config.setTempDir(DEFAULT_TMP_DIR + File.separator + pattern);
    }
    _configuration = config;
    
    File f = new File(new File(_configuration.getDeployDir()).getAbsolutePath());
    f.mkdirs();
    assertFile(f);

    f = new File(new File(_configuration.getTempDir()).getAbsolutePath());
    f.mkdirs();
    assertFile(f);

    logger().debug("Deploy dir: " + f.getPath());

    logger().info("Initializing: rebuilding distribution objects");

    _taskman.executeAndWait(new BuildDistTask(_configuration.getDeployDir(), getDistributionStore()));

    logger().info("Distribution objects succesfully rebuilt");

    _events.addInterceptor(ServerStartedEvent.class, this);
  }

  /**
   * Called when the Corus server has started.
   */
  public void onServerStartedEvent(ServerStartedEvent evt) {
    try {
      _processor = new DeploymentProcessor(this, logger());
      _processor.init();
      _processor.start();
    } catch (Exception e) {
      logger().error("Could not start deployment processor", e);
    }
    try{
      DeployerExtension ext = new DeployerExtension(this);
      _http.addHttpExtension(ext);
    }catch (Exception e){
      logger().error("Could not add deployer HTTP extension", e);
    }
    
  }

  /**
   * @see org.sapia.corus.core.soto.Service#dispose()
   */
  public void dispose() {
    if (_processor != null) {
      _processor.dispose();
    }
  }

  /*////////////////////////////////////////////////////////////////////
                    Module INTERFACE IMPLEMENTATION
  ////////////////////////////////////////////////////////////////////*/

  /**
   * @see org.sapia.corus.client.Module#getRoleName()
   */
  public String getRoleName() {
    return Deployer.ROLE;
  }

  /*////////////////////////////////////////////////////////////////////
                    Deployer INTERFACE IMPLEMENTATION
  ////////////////////////////////////////////////////////////////////*/

  public Distribution getDistribution(Arg name, Arg version)
    throws DistributionNotFoundException {
    return getDistributionStore().getDistribution(name, version);
  }

  public List<Distribution> getDistributions() {
    List<Distribution> dists = getDistributionStore().getDistributions();
    Collections.sort(dists);
    return dists;
  }

  public List<Distribution> getDistributions(Arg name) {
    List<Distribution> dists = getDistributionStore().getDistributions(name);
    Collections.sort(dists);
    return dists;
  }
  
  public List<Distribution> getDistributions(Arg name, Arg version) {
    List<Distribution> dists = getDistributionStore().getDistributions(name, version);
    Collections.sort(dists);
    return dists;
  }

  public ProgressQueue undeploy(Arg distName, Arg version) {
    ProgressQueueImpl progress = new ProgressQueueImpl();
    try {
      if(lookup(Processor.class).getProcesses(distName, version).size() > 0){
        throw new RunningProcessesException("Processes for selected configuration are currently running; kill them prior to undeploying");
      }

      TaskConfig cfg = TaskConfig.create(new TaskLogProgressQueue(progress));
      _taskman.executeAndWait(new UndeployTask(getDistributionStore(), distName, version), cfg);
    } catch (Throwable e) {
      progress.error(e);
    }
    return progress;
  }

  /**
   * @return this instance's <code>DistributionStore</code>.
   */
  public DistributionDatabase getDistributionStore() {
    return _store;
  }

  /**
   * @see org.sapia.corus.deployer.transport.DeploymentConnector#connect(org.sapia.corus.deployer.transport.Deployment)
   */
  public void connect(Deployment deployment) {
    String             fileName;
    DeploymentMetadata meta;

    try {
      meta       = deployment.getMetadata();
      fileName   = meta.getFileName() + "." + IDGenerator.makeId();
    } catch (IOException e) {
      deployment.close();
      logger().error("Could not acquire deployment metadata", e);

      return;
    }

    logger().info("Processing incoming deployment: " + fileName);

    DeployOutputStream out;

    // if deployment is clustered...
    if (meta.isClustered()) {
      Set<ServerAddress> siblings;

      try {
        siblings = _cluster.getHostAddresses();
      } catch (RuntimeException e) {
        deployment.close();
        logger().error("Could not lookup ClusterManager while performing deployment",
          e);

        return;
      }

      Set<ServerAddress>  targets = meta.getTargets();
      Set<ServerAddress>  visited = meta.getVisited();
      ServerAddress       addr;
      ServerAddress       current = CorusRuntime.getTransport()
                                                .getServerAddress();
      ReplicationStrategy strat   = new ReplicationStrategy(visited, targets,
          siblings);
			
      // adding this host to visited set
      visited.add(current);

      // if targets have been specified...
      if (targets != null) {
        // check that this host is in targets
        if (targets.contains(current)) {
          try {
            // remove this host from targets and add it to visited set						
            targets.remove(current);

            // if there are remaining targets, chain deployment to the 
            // next one
            if ((targets.size() > 0) && (siblings.size() > 0)) {
              addr = strat.selectNextSibling();

              try {
                out = new ClusteredDeployOutputStreamImpl(_configuration.getTempDir() +
                    File.separator + fileName, fileName, this,
                    new ClientDeployOutputStream(meta,
                      DeploymentClientFactory.newDeploymentClientFor(addr)));
              } catch (IOException e) {
                deployment.close();
                logger().error("Could not create clustered output stream while performing targeted deployment", e);

                return;
              }
            }
            // no remaining targets; deployment chain stops at this 
            // host
            else {
              out = new DeployOutputStreamImpl(_configuration.getTempDir() + File.separator +
                  fileName, fileName, this);
            }
          } catch (FileNotFoundException e) {
            deployment.close();
            logger().error("Could not create output stream while performing targeted deployment", e);

            return;
          }
        }
        // this host not in targets; so jump to the next host right away
        else {
          addr = strat.selectNextSibling();

          try {
            AbstractDeploymentClient client = (AbstractDeploymentClient) DeploymentClientFactory.newDeploymentClientFor(addr);
            out = client.getDeployOutputStream(meta);
          } catch (IOException e) {
            deployment.close();
            logger().error("Could not deploy to host: " + addr + " while performing targeted deployment", e);

            return;
          }
        }
      }
      // clustered deployment with no targets specified: all hosts
      // are thus part of the deployment.
      else {
        try {
					// no next host to deploy to; we have reached end of chain  
  				// - deployment stops here        	
          if ((addr = strat.selectNextSibling()) == null) {
            out = new DeployOutputStreamImpl(_configuration.getTempDir() + File.separator +
                fileName, fileName, this);
          } else {
						// chaining deployment to next host.        	
            out = new ClusteredDeployOutputStreamImpl(_configuration.getTempDir() + File.separator +
                fileName, fileName, this,
                new ClientDeployOutputStream(meta,
                  DeploymentClientFactory.newDeploymentClientFor(addr)));
          }
        } catch (IOException e) {
          deployment.close();
          logger().error("Could not create output stream while performing clustered deployment", e);

          return;
        }
      }
    }
    // deployment is not clustered		
    else {
      try {
        out = new DeployOutputStreamImpl(_configuration.getTempDir() + File.separator + fileName,
            fileName, this);
      } catch (FileNotFoundException e) {
        deployment.close();
        logger().error("Could not create output stream while performing deployment", e);

        return;
      }
    }

    try {
      deployment.deploy(out);
    } catch (IOException e) {
      try {
        out.close();
      } catch (IOException e2) {
        // noop
      }

      logger().error("Problem deploying: " + fileName, e);

      return;
    } finally {
      deployment.close();
    }

    logger().info("Deployment upload completed for: " + fileName);
  }

  synchronized ProgressQueue unlockDeployFile(String fileName) {
    _log.info("Finished uploading " + fileName);
    releaseFileLock(_deployLocks, fileName);
    ProgressQueue progress = new ProgressQueueImpl();
    try {
      _taskman.executeAndWait(
        new DeployTask(
            getDistributionStore(), 
            fileName, 
            _configuration.getTempDir(), 
            _configuration.getDeployDir()),
            TaskConfig.create(new TaskLogProgressQueue(progress)));
      
    } catch (Throwable e) {
      _log.error("Could not deploy", e);
      progress.error(e);
    }
    return progress;
  }

  private synchronized FileLock acquireFileLock(Map<String, FileLock> locks, String fileName)
    throws ConcurrentDeploymentException, InterruptedException {
    FileLock fLock = (FileLock) locks.get(fileName);

    if (fLock == null) {
      fLock = new FileLock(fileName, _configuration.getFileLockTimeout());
      locks.put(fileName, fLock);
    }

    fLock.acquire();

    return fLock;
  }

  private synchronized void releaseFileLock(Map locks, String fileName) {
    FileLock fLock = (FileLock) locks.get(fileName);

    if (fLock != null) {
      fLock.release();
    }
  }

  private void assertFile(File f) {
    f.mkdirs();

    if (!f.isDirectory()) {
      throw new IllegalArgumentException(f.getAbsolutePath() + " not a directory");
    }

    if (!f.exists()) {
      throw new IllegalArgumentException(f.getAbsolutePath() + " does not exist");
    }
  }
}
