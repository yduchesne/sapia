package org.sapia.regis.gui;

import org.sapia.regis.remote.RegistryServer;

public class GuiServer {
  
  public static void main(String[] args) {
    RegistryServer.main(new String[]{"etc/prevaylerServer.properties"});
  }

}
