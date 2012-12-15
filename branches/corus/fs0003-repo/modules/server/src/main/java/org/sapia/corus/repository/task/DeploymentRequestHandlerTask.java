package org.sapia.corus.repository.task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.sapia.corus.client.services.cluster.Endpoint;
import org.sapia.corus.client.services.deployer.transport.ClientDeployOutputStream;
import org.sapia.corus.client.services.deployer.transport.DeployOsAdapter;
import org.sapia.corus.client.services.deployer.transport.DeployOutputStream;
import org.sapia.corus.client.services.deployer.transport.DeploymentClientFactory;
import org.sapia.corus.client.services.deployer.transport.DeploymentMetadata;
import org.sapia.corus.client.services.processor.ExecConfig;
import org.sapia.corus.client.services.repository.ConfigNotification;
import org.sapia.corus.client.services.repository.DistributionDeploymentRequest;
import org.sapia.corus.client.services.repository.ExecConfigNotification;
import org.sapia.corus.taskmanager.util.RunnableTask;
import org.sapia.ubik.net.ServerAddress;
import org.sapia.ubik.util.Collections2;
import org.sapia.ubik.util.Function;

/**
 * This task performs the deployment to a provided list of nodes, following a {@link DistributionDeploymentRequest}.
 * 
 * @author yduchesne
 *
 */
public class DeploymentRequestHandlerTask extends RunnableTask {
    
  private static final int BUFSZ = 2048;
  
  private List<Endpoint> targets;
  private File           distributionFile;
  
  public DeploymentRequestHandlerTask(File distFile, List<Endpoint> targets) {
    this.distributionFile = distFile;
    this.targets          = targets;
  }
  
  public void run() { 
    if (targets.isEmpty()) {
      context().debug("No targets to deploy to");
    } else {
      try {
        context().debug("Deploying to: " + targets);
        doSendConfig();
        doDeployDistribution();
        doSendExecConfig();
      } catch (Exception e) {
        context().error("Problem performing deployment", e);
      }
    }
  }

  // --------------------------------------------------------------------------
  // Restricted visibility methods - for unit testing
  
  private void doSendConfig() throws Exception {
    Properties props = context().getServerContext().getProcessProperties();
    Set<String> tags = context().getServerContext().getServices().getConfigurator().getTags();
    
    if (props.isEmpty() && tags.isEmpty()) {
      context().debug("No tags or properties to push to: " + targets);
    } else {
      context().debug("Sending configuration notification to: " + targets);
      ConfigNotification notif = new ConfigNotification();
      notif.getTargets().addAll(targets);
      notif.addProperties(props);
      notif.addTags(tags);
    
      context()
        .getServerContext()
        .getServices()
        .getClusterManager()
        .send(notif);
    }
  }
  
  void doSendExecConfig() throws Exception {
    List<ExecConfig>       confs   = new ArrayList<ExecConfig>(context().getServerContext().getServices().getExecConfigs().getConfigs());
    
    if (confs.isEmpty()) {
      context().debug("Sending execution configurations to push to: " + targets);
    } else {
      context().debug("Sending execution configuration notification to: " + targets);
      ExecConfigNotification notif   = new ExecConfigNotification(confs);
      notif.getTargets().addAll(targets);
      context()
        .getServerContext()
        .getServices()
        .getClusterManager()
        .send(notif);
    }
  }
  
  void doDeployDistribution() throws IOException {
    OutputStream        os     = null;
    BufferedInputStream bis    = null;
    boolean             closed = false;
    
    List<Endpoint> targetsCopy = new ArrayList<Endpoint>(targets);
    
    try {
      
      DeploymentMetadata meta = new DeploymentMetadata(distributionFile.getName(), distributionFile.length(), true);
      
      Endpoint first = targetsCopy.remove(0);
      
      meta.getTargeted().addAll(Collections2.convertAsSet(targetsCopy, new Function<ServerAddress, Endpoint>() {
        public ServerAddress call(Endpoint arg) {
          return arg.getServerAddress();
        }
      }));
      
      DeployOutputStream dos = new ClientDeployOutputStream(meta, DeploymentClientFactory.newDeploymentClientFor(first.getServerAddress()));

      os  = new DeployOsAdapter(dos);
      bis = new BufferedInputStream(new FileInputStream(distributionFile));
      
      byte[] b    = new byte[BUFSZ];
      int    read;
      
      while ((read = bis.read(b)) > -1) {
        os.write(b, 0, read);
      }
      
      os.flush();
      os.close();
      closed = true;
      
    } finally {
      if ((os != null) && !closed) {
        try {
          os.close();
        } catch (IOException e) {
        }
      }
      
      if (bis != null) {
        try {
          bis.close();
        } catch (IOException e) {
        }
      }
    }
  }

}
