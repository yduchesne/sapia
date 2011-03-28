package org.sapia.soto.activemq.example;

import java.io.File;
import java.net.URL;

import org.apache.log4j.xml.DOMConfigurator;
import org.sapia.soto.SotoContainer;

public class ServerConfig {

  public static void main(String[] args) {
    
    try{
      DOMConfigurator.configure(new URL("file:etc/activemq/log.xml"));
      
      /*BrokerService broker = new BrokerService();
      broker.setPersistenceAdapter(new MemoryPersistenceAdapter());
      broker.setBrokerName("test");
      TransportConnector connector = new TransportConnector();
      connector.setUri(new URI("tcp://localhost:0"));
      connector.setDiscoveryUri(new URI("multicast://default"));
      
      broker.addConnector(connector);
      broker.start();*/
      SotoContainer container = new SotoContainer();      
      container.load(new File("etc/activemq/server.xml"));
      container.start();
      System.out.println("started");
      while(true){
        Thread.sleep(10000);
      }
      
      
    }catch(Exception e){
      e.printStackTrace();
    }
  }
  
}
