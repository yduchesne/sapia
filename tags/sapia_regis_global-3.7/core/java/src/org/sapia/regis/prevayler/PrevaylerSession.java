package org.sapia.regis.prevayler;

import org.sapia.regis.Node;
import org.sapia.regis.RegisSession;

public class PrevaylerSession implements RegisSession{

  public Node attach(Node node) {
    return node;
  }

  public void close() {
  }
}
