package org.sapia.soto.osgi.deployer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.sapia.soto.Debug;

/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
class DirWatcherThread extends Thread{
  
  private DeploymentManagerImpl _manager;
  private long _interval;
    
  DirWatcherThread(DeploymentManagerImpl manager){
    _manager = manager;
  }
  
  void setInterval(long interval){
    _interval = interval;
  }
  
  void syncUndeploy(String type, URL undeployed) throws IOException{
    File destDir = new File(_manager.getBasedir(), type);
    String path = undeployed.getPath();
    path = path.replace('\\','/');
    int i = path.lastIndexOf('/');
    if(i >= 0){
      path = path.substring(i+1);
    }
    File location = new File(destDir, path);
    _manager.getDb().remove(location);
    _manager.getDb().save();
  }
  
  /**
   * @see java.lang.Runnable#run()
   */
  public void run() {
    while(true){
      doWatch();
      try{
        Thread.sleep(_interval);
      }catch(InterruptedException e){
        break;
      }
    }
  }
  
  private void doWatch(){
    File[] toDeploy = getFiles();
    for(int i = 0; toDeploy != null && i < toDeploy.length; i++){
      if(Debug.DEBUG){
        Debug.debug("Discovered file: " + toDeploy[i].getAbsolutePath());
      }
      try{
        URL url = toDeploy[i].toURL();
        DeploymentStatus stat = _manager.deploy(url);
        while(stat.hasNextMessage()){
          if(Debug.DEBUG){
            Debug.debug(stat.nextMessage());
          }
        }
        Debug.debug("Deployment successful");      
      }catch(DeploymentException e){
        if(Debug.DEBUG){
          Debug.debug("Problem while deploying");
          Debug.debug(e);
        }
      }catch(Exception e){
        if(Debug.DEBUG){
          Debug.debug("Problem attempting to deploy");
          Debug.debug(e);
        }      
      }      
      toDeploy[i].delete();
    }
    
    File[] deployed = _manager.getDb().getFiles();
    for(int i = 0; i < deployed.length; i++){
      if(!deployed[i].exists()){
        if(Debug.DEBUG){
          Debug.debug("File was removed from directory: " + deployed[i].getAbsolutePath() + "; undeploying...");
        }
        try{
          URL toUndeploy = deployed[i].toURL();
          Deployer depl = _manager.getDeployerFor(toUndeploy);
          if(depl != null){
            depl.undeploy(toUndeploy);
            if(Debug.DEBUG){
              Debug.debug("Undeployment completed for: " + deployed[i].getAbsolutePath());
            }
          }
        }catch(MalformedURLException e){
          //noop
        }catch(DeploymentException e){
          if(Debug.DEBUG){
            Debug.debug("Could not perform undeployment");
            Debug.debug(e);
          }
        }finally{
          _manager.getDb().remove(deployed[i]);
        }
      }
    }
    try{
      _manager.getDb().save();
    }catch(IOException e){
      if(Debug.DEBUG){
        Debug.debug("Could not synchronize deployment database");
        Debug.debug(e);
      }
    }
  }
  
  private File[] getFiles(){
    File[] files = _manager.getBasedir().listFiles();
    List toReturn = new ArrayList();
    for(int i = 0; i < files.length; i++){
      if(files[i].isFile() && !files[i].getName().equals(DeploymentDb.FILE_NAME)){
        toReturn.add(files[i]);
      }
    }
    return (File[])toReturn.toArray(new File[toReturn.size()]);
  }
}
