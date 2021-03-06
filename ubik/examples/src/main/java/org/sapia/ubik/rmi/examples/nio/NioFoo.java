package org.sapia.ubik.rmi.examples.nio;

import java.util.Properties;

import org.sapia.ubik.rmi.Consts;
import org.sapia.ubik.rmi.examples.UbikFoo;
import org.sapia.ubik.rmi.server.Hub;
import org.sapia.ubik.rmi.server.transport.mina.MinaTransportProvider;

public class NioFoo {

  public static void main(String[] args) {
    try {
      //Log.setDebug();
      Properties props = new Properties();
      props.setProperty(Consts.TRANSPORT_TYPE,
        MinaTransportProvider.TRANSPORT_TYPE);
      props.setProperty(MinaTransportProvider.PORT, "6060");
      props.setProperty(Consts.SERVER_MAX_THREADS, "10");      
      
      Hub.exportObject(new UbikFoo(), props);
      
      System.out.println("Server started");

      while (true) {
        Thread.sleep(100000);
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }  
}
