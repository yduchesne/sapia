/*
 * StatelessUbikServer.java
 *
 * Created on October 27, 2005, 11:03 PM
 */

package org.sapia.soto.ubik.example;

import java.io.File;
import org.sapia.soto.SotoContainer;
import org.sapia.ubik.rmi.server.Log;

/**
 *
 * @author yduchesne
 */
public class StatelessUbikServer extends UbikServer implements StatelessUbikService{
  
  /** Creates a new instance of StatelessUbikServer */
  public StatelessUbikServer() {
  }
  
  public static void main(String[] args){
    Log.setInfo();
    try{
      SotoContainer soto = new SotoContainer();
      soto.load(new File("etc/ubik/remoteService.xml"));
      while(true){
        Thread.sleep(100000);
      }
    }catch(Throwable t){
      t.printStackTrace();
    }
  }
  
}
