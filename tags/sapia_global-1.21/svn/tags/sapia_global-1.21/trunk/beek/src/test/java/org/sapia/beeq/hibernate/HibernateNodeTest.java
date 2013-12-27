package org.sapia.beeq.hibernate;

import org.hibernate.SessionFactory;
import org.sapia.beeq.Message;
import org.sapia.beeq.MessageID;
import org.sapia.beeq.MessageNode;
import org.sapia.beeq.TestQueueListener;
import org.sapia.beeq.hibernate.conf.ConfigLoader;

import junit.framework.TestCase;

public class HibernateNodeTest extends TestCase{
  
  private SessionFactory sessions;
  private MessageNode node;
  private TestQueueListener listener;
    
  protected void setUp() throws Exception {
    sessions = ConfigLoader.createEmbedded().buildSessionFactory();
    listener = new TestQueueListener();
    NodeConfiguration conf = new NodeConfiguration();
    conf.setMaxThreads(1);
    conf.setBatchSize(50);
    node = new HibernateNode("test", sessions, listener, conf);
  }
  
  @Override
  protected void tearDown() throws Exception {
    sessions.close();
  }
  

  public void testPut() throws Exception{
    Message msg = node.create("12345");
    msg.setContentType("text/xml");
    msg.setPayload("<payload>Hello World</payload>");
    msg.setDestination("http://acme.org");
    MessageID id = node.put(msg);
    listener.waitForMessage(2000);
    assertEquals(id, listener.get().getID());
  }

}

