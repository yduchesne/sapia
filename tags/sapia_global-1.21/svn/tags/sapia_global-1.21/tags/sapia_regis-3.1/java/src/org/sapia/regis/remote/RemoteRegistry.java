package org.sapia.regis.remote;

import java.io.ByteArrayInputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.sapia.regis.Configurable;
import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.RWNode;
import org.sapia.regis.RWSession;
import org.sapia.regis.RegisDebug;
import org.sapia.regis.RegisSession;
import org.sapia.regis.Registry;
import org.sapia.regis.loader.RegistryConfigLoader;
import org.sapia.regis.util.CompositeProperties;

/**
 * A registry of remote nodes. An instance of this class wraps
 * a given <code>Registry</code> instance in order to make it
 * "remotable".
 * 
 * @see org.sapia.regis.remote.RemoteNode
 * @see org.sapia.regis.remote.RegistryExporter
 * @author yduchesne
 *
 */
public class RemoteRegistry implements Registry, Configurable, Remote, Authenticating{
  
  private Registry _delegate;
  private Node _root;
  private Map _peers;
  private Authenticator _auth;
  private Properties _boostrapProps;
  RemoteRegistry(Authenticator auth, Registry deleg, Properties boostrapProps){
    _delegate = deleg;
    _auth = auth;
    RegisSession s = deleg.open();
    try{
      _root = new RemoteNode(deleg.getRoot());
    }finally{
      s.close();
    }
    _boostrapProps = boostrapProps;
  }
  
  public RegisSession open() {
    RegisSession s = _delegate.open();
    RemoteSessions.join(s);
    return new RemoteSession(s);
  }
  
  public Node getRoot() {
    return _root;
  }
  
  public void close() {
    _delegate.close();
  }
  
  public Registry internal(){
    return _delegate;
  }
  
  public void load(Path path, String username, String password, String xmlConf, Properties props) throws RemoteException, Exception {
    doLoad(path, username, password, xmlConf, true, props);
  }

  public void syncLoad(Path path, String username, String password, String xmlConf, Properties props) throws RemoteException, Exception {
    doLoad(path, username, password, xmlConf, false, props);
  }
  
  public boolean authenticate(String username, String password) {
    try{
      _auth.authenticate(username, password);
      return true;
    }catch(IllegalStateException e){
      e.printStackTrace();
      return false;
    }
  }
  
  public void doLoad(Path path, String username, String password, String xmlConf, boolean sync, Properties props) throws RemoteException, Exception {
    _auth.authenticate(username, password);
    RegisSession sess = null;
    try{
      //RegistryServerLockManager.lock().readLock().unlock();
      //RegistryServerLockManager.lock().writeLock().lock();
      sess = _delegate.open();
      Node node = _delegate.getRoot();      
      if(_delegate instanceof Configurable){
        loadConfigurable((Configurable)_delegate, path, xmlConf, props);
      }
      else if(node instanceof Configurable){
        loadConfigurable((Configurable)node, path, xmlConf, props);        
      }
      else{
        RWSession rw = (RWSession)sess;
        rw.begin();
  
        try{
          if(path != null && !path.isRoot()){
            node = node.getChild(path);
          }
          
          RegistryConfigLoader loader = new RegistryConfigLoader((RWNode)node);
          CompositeProperties cProps = new CompositeProperties(_boostrapProps);
          if(props != null){
            cProps.addChild(props);
          }
          loader.load(new ByteArrayInputStream(xmlConf.getBytes()), cProps);
          rw.commit();
        }catch(Exception e){
          rw.rollback();
        }
      }
      if(sync){
        dispatch(path, username, password, xmlConf);
      }
      else{
        RegisDebug.debug(this, "Receiving configuration from peer");        
      }
    }catch(ClassCastException e){
      e.printStackTrace();
      throw new IllegalStateException("Registry does not support remote operations");
    }catch(RuntimeException e){
      e.printStackTrace();
      throw new IllegalStateException("Could not load configuration - " + e.getMessage());
    }finally{
      sess.close();
      //RegistryServerLockManager.lock().readLock().lock();
      //RegistryServerLockManager.lock().writeLock().unlock();      
    }
  }
  
  void setPeers(Map peers){
    _peers = peers;
  }
  
  private void loadConfigurable(Configurable conf, Path path, String xmlConf, Properties props) throws Exception{
    CompositeProperties cProps = new CompositeProperties(_boostrapProps);
    if(props != null){
      cProps.addChild(props);
    }
    conf.load(path, null, null, xmlConf, cProps);
  }  
  
  private void dispatch(Path path, String username, String password, String xmlConf) throws Exception{
    if(_peers != null){
      RegisDebug.debug(this, "Sending configuration to peers (got " + _peers.size() + " peers in list)");      
      synchronized(_peers){
        Iterator itr = _peers.values().iterator();
        while(itr.hasNext()){
          Configurable cfg = (Configurable)itr.next();
          try{
            cfg.syncLoad(path, username, password, xmlConf, null);
          }catch(RemoteException e){
            RegisDebug.debug(this, "One peer is down; removing from list");            
            itr.remove();
          }
        }
      }
    }
  }


}
