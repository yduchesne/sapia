package org.sapia.regis.remote;

import java.rmi.Remote;

import org.sapia.regis.Node;
import org.sapia.regis.RegisSession;

public class RemoteSession implements RegisSession, Remote{
  
  private RegisSession _delegate;
  
  RemoteSession(RegisSession deleg){
    _delegate = deleg;
  }

  public Node attach(Node node) {
    if(node instanceof RemoteNode){
      return _delegate.attach(((RemoteNode)node).node());
    }
    else{
      return _delegate.attach(node);
    }
  }

  public void close() {
    _delegate.close();
  }

}
