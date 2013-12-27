package org.sapia.beeq.hibernate;

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.sapia.beeq.Message;
import org.sapia.beeq.MessageID;
import org.sapia.beeq.Messages;
import org.sapia.beeq.MessageNode;
import org.sapia.beeq.hibernate.queue.HibernateQueue;
import org.sapia.beeq.hibernate.queue.retry.HibernateRetryQueue;
import org.sapia.beeq.queue.QueueListener;

public class HibernateNode implements MessageNode{
  
  private HibernateQueue queue;
  private HibernateRetryQueue retryQueue;
  private String nodeId;
  private SessionFactory sessions;
  
  public HibernateNode(String nodeId, SessionFactory sessions, final QueueListener listener, NodeConfiguration conf){
    this.nodeId = nodeId;
    this.sessions = sessions;
    this.retryQueue = new HibernateRetryQueue(nodeId, listener, sessions, conf);
    
    QueueListener failedListener = new QueueListener(){
      
      public void onMessage(Message msg) throws Exception {
        try{
          listener.onMessage(msg);
        }catch(Exception e){
          e.printStackTrace();
          retryQueue.add(msg);
        }
      }
    };
    
    this.queue = new HibernateQueue(nodeId, failedListener, sessions, conf);
  }
  
  
  public Message create(String clientGeneratedId) {
    HibernateMessage msg = new HibernateMessage();
    msg.setClientGeneratedId(clientGeneratedId);
    return msg;
  }
  
  public MessageID put(Message msg) {
    ((HibernateMessage)msg).setNodeId(nodeId);
    queue.add(msg);
    return msg.getID();
  }
  
  public Message get(MessageID id){
    Session sess = sessions.openSession();
    try{
      HibernateMessage msg = (HibernateMessage)sess.load(HibernateMessage.class, (Serializable)id.getLocalID());
      return Messages.copy(msg);
    }finally{
      sess.close();
    }
  }  

}
