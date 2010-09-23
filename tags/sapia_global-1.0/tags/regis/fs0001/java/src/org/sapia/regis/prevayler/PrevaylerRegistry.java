package org.sapia.regis.prevayler;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Properties;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.sapia.regis.Configurable;
import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.RegisSession;
import org.sapia.regis.Registry;
import org.sapia.regis.impl.NodeImpl;
import org.sapia.regis.util.Utils;

public class PrevaylerRegistry implements Registry, Configurable{
  
  private NodeImpl _root = new NodeImpl(null, Node.ROOT_NAME);
  private Prevayler _prevayler;
  
  private static RegisSession SESSION = new PrevaylerSession();
  
  PrevaylerRegistry(String prevalenceBase, boolean deleteOnStartup) throws Exception{
    if(deleteOnStartup){
      File f = new File(prevalenceBase);
      if(f.exists()){
        Utils.deleteRecurse(f);
      }
      f.mkdirs();
    }
    _prevayler = PrevaylerFactory.createPrevayler(_root, prevalenceBase);
  }
  
  public Node getRoot() {
    try{
      return (Node)_prevayler.execute(new QueryGetRoot());
    }catch(Exception e){
      throw new RuntimeException("Could not get root", e);
    }
  }
  
  public RegisSession open() {
    return SESSION;
  }

  public void close() {
    try{
      _prevayler.close();
    }catch(IOException e){
      //noop
    }
  }
  
  public void load(Path nodePath, String username, String password, String xmlConf, Properties props) throws RemoteException, Exception {
    TxLoad load = new TxLoad(nodePath, xmlConf, props);
    _prevayler.execute(load);
  }
  
  public void syncLoad(Path nodePath, String username, String password, String xmlConf, Properties props) throws RemoteException, Exception {
  }
  
}
