package org.sapia.regis.remote.client;

import org.sapia.regis.Node;
import org.sapia.regis.RegisSession;

public class RemoteSessionProxy implements RegisSession{

  public Node attach(Node node) {
    return node;
  }

  public void close() {
  }
}
