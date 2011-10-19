package org.sapia.soto.activemq;

import java.net.URI;
import java.util.List;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.store.memory.MemoryPersistenceAdapter;

public class VmBrokerService extends BrokerService{
  
  public VmBrokerService() throws Exception{
    TransportConnector conn = new TransportConnector();
    setPersistenceAdapter(new MemoryPersistenceAdapter());    
    conn.setUri(new URI("vm://localhost"));
    super.addConnector(conn);
  }

}
