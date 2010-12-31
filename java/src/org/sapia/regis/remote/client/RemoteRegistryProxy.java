package org.sapia.regis.remote.client;

import java.rmi.RemoteException;
import java.util.Properties;

import org.sapia.regis.Configurable;
import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.RegisSession;
import org.sapia.regis.Registry;

public class RemoteRegistryProxy implements Registry, Configurable{

  private Registry _remote;
  private static RemoteSessionProxy _shared = new RemoteSessionProxy();

  RemoteRegistryProxy(Registry remote){
    _remote = remote;
  }
  
  public Node getRoot() {
    return _remote.getRoot();
  }
  
  public RegisSession open() {
    return _shared;
  }
  
  public void close() {
    // noop
  }
  
  public void syncLoad(Path path, String username, String password, String xmlConf, Properties props) throws RemoteException, Exception {
    ((Configurable)_remote).syncLoad(path, username, password, xmlConf, props);
  }
  
  public void load(Path path, String username, String password, String xmlConf, Properties props) throws RemoteException, Exception {
    ((Configurable)_remote).load(path, username, password, xmlConf, props);
  }
}
