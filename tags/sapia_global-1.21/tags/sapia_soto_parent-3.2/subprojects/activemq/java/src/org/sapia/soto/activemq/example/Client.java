package org.sapia.soto.activemq.example;

import java.io.File;
import java.net.URL;

import javax.jms.Connection;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.sapia.soto.SotoContainer;

public class Client {
  
  public static void main(String[] args) {
    try {

      DOMConfigurator.configure(new URL("file:etc/activemq/log.xml"));
      //ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("discovery:multicast://default");
      //ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
      SotoContainer soto = new SotoContainer();
      soto.load(new File("etc/activemq/client.xml"));
      soto.start();
      System.out.println("Container started...");
      Connection connection = ((ActiveMQConnectionFactory)soto.lookup("factory")).createConnection();
      System.out.println("Got connection");
      connection.start();
      System.out.println("Started connection...");
      connection.stop();
      connection.close();
      
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
