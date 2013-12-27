package org.sapia.regis.cache;

import org.sapia.regis.Node;
import org.sapia.regis.RegisSession;

public class CacheRegisSession implements RegisSession{
  
  private RegisSession internal;
  
  CacheRegisSession(RegisSession deleg){
    internal = deleg;
  }
  
  public Node attach(Node node) {
    return internal.attach(node);
  }
  
  public void close() {
    internal.close();
  }
  
  public RegisSession internal(){
    return internal;
  }

}
