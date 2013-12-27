package org.sapia.soto.osgi.deployer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.sapia.soto.util.Utils;

/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
class DeploymentThread extends Thread{
  private List _deploymentQueue = new ArrayList();
  private DeploymentManagerImpl _manager;
  
  DeploymentThread(DeploymentManagerImpl manager){
    super(DeploymentThread.class.getName());
    _manager = manager;
  }
  
  synchronized void add(DeploymentStatusQueue queue, Deployer deployer){
    DeploymentInfo info = new DeploymentInfo();
    info.queue = queue;
    info.deployer = deployer;
    _deploymentQueue.add(info);
    notify();
  }
  
  /**
   * @see java.lang.Thread#run()
   */
  public synchronized void run() {
    while(true){
      while(_deploymentQueue.size() == 0){
        try{
          wait();
        }catch(InterruptedException e){
          return;
        }
      }
      DeploymentInfo info = (DeploymentInfo)_deploymentQueue.remove(0);
      Deployer deployer = info.deployer;
      File destDir = new File(_manager.getBasedir(), deployer.getType());
      String path = info.queue.getURL().getPath();
      path = path.replace('\\','/');
      int i = path.lastIndexOf('/');
      if(i >= 0){
        path = path.substring(i+1);
      }
      File location = new File(destDir, path);
      
      try{
        
        //transferring file to deployer dir...
        URL locationURL = location.toURL();        
        FileOutputStream out = new FileOutputStream(location);
        Utils.copy(info.queue.getURL().openStream(), out);
        
        //substituting...
        info.queue.setURL(locationURL);
        
        deployer.deploy(info.queue);
        _manager.getDb().add(location);
        try{
          _manager.getDb().save();
        }catch(IOException e){}
        info.queue.completed();
      }catch(IOException e){
        info.queue.error(new DeploymentException("IO problem occured while copying deployment from deploy directory to destination directory: " + destDir.getAbsolutePath()));
        location.delete();
      }catch(DeploymentException e){
        info.queue.error(e);
        location.delete();
      }catch(RuntimeException e){
        info.queue.error(new DeploymentException("Unexcepted problem occured while deploying", e));
        location.delete();
      }
    }
  }
  
  static final class DeploymentInfo{
    DeploymentStatusQueue queue;
    Deployer deployer;
  }
}
