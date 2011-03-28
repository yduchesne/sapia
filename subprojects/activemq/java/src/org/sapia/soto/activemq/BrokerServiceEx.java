package org.sapia.soto.activemq;

import org.apache.activemq.broker.BrokerService;
import org.sapia.soto.Debug;
import org.sapia.soto.Service;

public class BrokerServiceEx extends BrokerService implements Service{
  
  
  public void init() throws Exception {
  }
  
  public void start() throws Exception {
    super.start();
  }
  
  public void dispose() {
    try{
      super.stop();
    }catch(Exception e){
      Debug.debug(e);
    }
  }

}
