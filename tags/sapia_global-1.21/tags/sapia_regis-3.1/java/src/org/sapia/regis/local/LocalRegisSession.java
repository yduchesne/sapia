package org.sapia.regis.local;

import org.sapia.regis.Node;
import org.sapia.regis.RWSession;

public class LocalRegisSession implements RWSession{
  
  public void begin() {}
  
  public void commit() {}
  
  public void rollback() {}
  
  public void close() {}
  
  public Node attach(Node node) {
    return node;
  }
  
}
