package org.sapia.beeq.hibernate.queue;

import org.hibernate.SessionFactory;
import org.sapia.beeq.MessageID;
import org.sapia.beeq.TestQueueListener;
import org.sapia.beeq.hibernate.HibernateMessage;
import org.sapia.beeq.hibernate.NodeConfiguration;
import org.sapia.beeq.hibernate.conf.ConfigLoader;

import junit.framework.TestCase;

public class HibernateQueueTest extends TestCase {
  
  private SessionFactory sessions;
  private TestQueueListener listener = new TestQueueListener();
    
  protected void setUp() throws Exception {
    sessions = ConfigLoader.createEmbedded().buildSessionFactory();
  }
  
  @Override
  protected void tearDown() throws Exception {
    sessions.close();
  }  

  public void testAdd() throws Exception{
    HibernateQueue queue = new HibernateQueue("test", listener, sessions, new NodeConfiguration());
    HibernateMessage msg = new HibernateMessage();
    msg.setNodeId("test");
    msg.setContentType("test");
    msg.setPayload("<payload>test</payload>");
    MessageID id = queue.add(msg);
    listener.waitForMessage(2000);
    queue.close();
    assertEquals(id, listener.get().getID());
  }

}
