package org.sapia.soto.osgi.deployer;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
class DeploymentStatusQueue implements DeploymentStatus, Deployment{
  
  private List _queue = Collections.synchronizedList(new ArrayList());
  private boolean _completed = false;
  private Exception _err;
  private URL _url;
  
  DeploymentStatusQueue(URL toDeploy) {

    _url = toDeploy;
  }
  
  /**
   * @see org.sapia.soto.osgi.deployer.Deployment#getURL()
   */
  public URL getURL() {
    return _url;
  }
  
  /**
   * @see org.sapia.soto.osgi.deployer.DeploymentStatus#hasNextMessage()
   */
  public synchronized boolean hasNextMessage() throws DeploymentException{
    while(_queue.size() == 0 && !_completed && _err == null){
      try{
        wait();
      }catch(InterruptedException e){
        throw new DeploymentException("Thread interrupted during deployment");
      }
    }
    if(_queue.size() > 0){
      return true;
    }    
    if(_err != null){
      if(_err instanceof DeploymentException){
        throw (DeploymentException)_err;
      }
      throw new DeploymentException("Could not deploy", _err);
    }
    return false;
  }
  
  /**
   * @see org.sapia.soto.osgi.deployer.DeploymentStatus#nextMessage()
   */
  public synchronized String nextMessage(){
    if(_queue.size() == 0){
      throw new NoSuchElementException();
    }
    return (String)_queue.remove(0);
  }

  /**
   * @see org.sapia.soto.osgi.deployer.Deployment#info(java.lang.String)
   */
  public synchronized void info(String msg){
    _queue.add(msg);
    notify();
  }

  synchronized void completed(){
    _completed = true;
    notify();
  }
  
  synchronized void error(Exception e){
    _err = e;
    notify();
  }
  
  void setURL(URL url){
    _url = url;
  }
}
