package org.sapia.soto.activemq.example;

import java.io.File;
import java.net.URL;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.xml.DOMConfigurator;
import org.sapia.soto.SotoContainer;
import org.sapia.soto.activemq.MessageListenerFactory;
import org.sapia.soto.activemq.util.QueueMessageProcessor;

/**
 * This class extends the {@link org.sapia.soto.activemq.util.QueueMessageProcessor} and 
 * implements the {@link javax.jms.MessageListener} interface in order to handle incoming JMS
 * messages.
 * 
 * @author yduchesne
 *
 */
public class QueueReceiver extends QueueMessageProcessor 
  implements MessageListener, MessageListenerFactory{
  
  public void onMessage(Message msg) {
    TextMessage txtMsg = (TextMessage)msg;
    try{
      System.out.println("Received message: " + txtMsg.getText());
    }catch(JMSException e){
      e.printStackTrace();
    }
    
  }
  
  public MessageListener createMessageListener() {
    return this;
  }
  
  public void init() throws Exception {
    super.setMessageListenerFactory(this);
    super.init();
  }
  
  public static void main(String[] args) {
    SotoContainer soto = new SotoContainer();    
    try {
      DOMConfigurator.configure(new URL("file:etc/activemq/log.xml"));
      
      soto.load(new File("etc/activemq/queueReceiver.xml"));
      soto.start();
      System.out.println("Container started...");
      while(true){
        Thread.sleep(10000000);
      }
    } catch (Exception e) {
      soto.dispose();
      e.printStackTrace();
    }
  }    

}
