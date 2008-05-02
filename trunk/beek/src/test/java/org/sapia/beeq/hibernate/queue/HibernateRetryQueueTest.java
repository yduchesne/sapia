package org.sapia.beeq.hibernate.queue;

import java.io.Serializable;
import java.util.Collection;

import org.apache.log4j.PropertyConfigurator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.sapia.beeq.Message;
import org.sapia.beeq.MessageID;
import org.sapia.beeq.MessageStatus;
import org.sapia.beeq.Messages;
import org.sapia.beeq.hibernate.HibernateMessage;
import org.sapia.beeq.hibernate.NodeConfiguration;
import org.sapia.beeq.hibernate.conf.ConfigLoader;
import org.sapia.beeq.hibernate.queue.retry.FixedIntervalRetryPolicy;
import org.sapia.beeq.hibernate.queue.retry.HibernateRetryQueue;

import junit.framework.TestCase;

public class HibernateRetryQueueTest extends TestCase {
  
  private SessionFactory sessions;
  private TestRetryQueueListener listener = new TestRetryQueueListener();
    
  protected void setUp() throws Exception {
    PropertyConfigurator.configure("conf/log4j.properties");
    sessions = ConfigLoader.createEmbedded().buildSessionFactory();
    listener.throwException = true;
  }
  
  @Override
  protected void tearDown() throws Exception {
    try{
      sessions.close();
    }catch(Throwable e){}
  }
  
  public void testAdd() throws Exception{

    NodeConfiguration conf = new NodeConfiguration();
    FixedIntervalRetryPolicy policy = new FixedIntervalRetryPolicy();
    policy.setIntervalMillis(1000);
    conf.setRetryPolicy(policy);
    HibernateRetryQueue queue = new HibernateRetryQueue("test", listener, sessions, conf);
    HibernateMessage msg = new HibernateMessage();
    msg.setNodeId("test");
    msg.setContentType("test-content");
    msg.setPayload("<payload>test</payload>");
    queue.add(msg);
    listener.waitForMessages(3, 2000);
    Collection<Message> messages = listener.getAll();
    assertEquals(3, messages.size());
    Thread.sleep(2000);
    assertEquals(MessageStatus.FAILED, load(messages.iterator().next().getID()).getStatus());
    queue.close();    
    
  }
  
  private Message load(MessageID id){
    Session sess = sessions.openSession();
    try{
      HibernateMessage msg = (HibernateMessage)sess.load(HibernateMessage.class, (Serializable)id.getLocalID());
      return Messages.copy(msg);
    }finally{
      sess.close();
    }    
  }

}
