package org.sapia.soto.activemq.example;

import java.io.File;
import java.net.URL;

import org.apache.log4j.xml.DOMConfigurator;
import org.sapia.soto.Service;
import org.sapia.soto.SotoContainer;
import org.sapia.soto.activemq.util.QueueSenderPool;

/**
 * This class extends the {@link org.sapia.soto.activemq.util.QueueSenderPool} class
 * in order to benefit from already implemented message-sending plumbing.
 * 
 * @author yduchesne
 *
 */
public class QueueSender extends QueueSenderPool implements Service{
  
  public void sendMessage(String msg) throws Exception{
    System.out.println("Sending message: " + msg);
    super.sendTextMessage(msg);
  }
  
  public static void main(String[] args) {
    try {
      DOMConfigurator.configure(new URL("file:etc/activemq/log.xml"));
      SotoContainer soto = new SotoContainer();
      soto.load(new File("etc/activemq/queueSender.xml"));
      soto.start();
      System.out.println("Container started...");
      QueueSender sender = (QueueSender)soto.lookup("sender");
      sender.sendMessage("Hello World!!!");
      soto.dispose();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }  

}
