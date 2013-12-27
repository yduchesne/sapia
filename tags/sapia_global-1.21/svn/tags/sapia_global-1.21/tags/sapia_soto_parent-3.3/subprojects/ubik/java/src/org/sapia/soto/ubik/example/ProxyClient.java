/*
 * StatelessProxyClient.java
 *
 * Created on October 27, 2005, 11:11 PM
 */

package org.sapia.soto.ubik.example;

import java.io.File;
import org.sapia.soto.SotoContainer;
import org.sapia.ubik.rmi.server.Log;

/**
 *
 * @author yduchesne
 */
public class ProxyClient {
  
  /** Creates a new instance of StatelessProxyClient */
  public ProxyClient() {
  }
  
  public static void main(String[] args){
    Log.setInfo();
    try{
      SotoContainer soto = new SotoContainer();
      soto.load(new File("etc/ubik/proxyService.xml"));
      UbikService service = (UbikService)soto.lookup("proxy");
      int count = 0;
      while(true){
        System.out.println((++count) + ". ping...");
        service.ping();
        Thread.sleep(2000);
      }
    }catch(Throwable t){
      t.printStackTrace();
    }
  }
  
  
}
