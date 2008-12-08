package org.sapia.regis.local;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Properties;

import org.sapia.regis.Configurable;
import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.RWNode;
import org.sapia.regis.RegisSession;
import org.sapia.regis.Registry;
import org.sapia.regis.impl.NodeImpl;
import org.sapia.regis.loader.RegistryConfigLoader;

/**
 * This class implements an in-memory registy. It does support writes,
 * but not transactional. This has the following implications:
 * 
 * <ul>
 *   <li>An instance of this class instantiates <code>RWNode</code>s.
 *   <li>An instance of this class creates <code>RWSession</code>s.
 *   <li>The begin, commit and rollback methods of the <code>RWSession</code>s
 *   that an instance of this class creates have no effect.
 * </ul>
 * 
 * @author yduchesne
 *
 */
public class LocalRegistry implements Registry, Configurable {
  
  private RWNode _root;
  
  public LocalRegistry(){
    this(new NodeImpl(null, Node.ROOT_NAME));
  }
  
  public LocalRegistry(RWNode root){
    _root = root;
  }
  
  public RegisSession open() {
    return new LocalRegisSession();
  }
  
  public Node getRoot() {
    return _root;
  }
  
  public void load(File config) throws Exception{
    load(new FileInputStream(config));
  }
  
  public void load(InputStream is) throws Exception{
    if(_root == null){
      _root = new NodeImpl(null, Node.ROOT_NAME);
    }
    RegistryConfigLoader loader = new RegistryConfigLoader(_root);
    loader.load(is);
  }
  
  public void load(Path nodePath, String username, String password, String xmlConf, Properties props) throws RemoteException, Exception {
    Node child = _root.getChild(nodePath);
    if(child == null){
      throw new IllegalStateException("No such node: " + nodePath.toString());
    }
    RegistryConfigLoader loader = new RegistryConfigLoader((RWNode)child);

    if (props == null) {
      loader.load(new ByteArrayInputStream(xmlConf.getBytes()));
    } else {
      loader.load(new ByteArrayInputStream(xmlConf.getBytes()), props);
    }
  }
  
  public void syncLoad(Path nodePath, String username, String password, String xmlConf, Properties props) throws RemoteException, Exception {
  }
  
  public void close() {
  }

}
