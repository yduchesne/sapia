package org.sapia.soto.activemq.util;

import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.Session;

import org.apache.commons.pool.PoolableObjectFactory;

public class PooledTopicPublisherFactory implements PoolableObjectFactory{
  
  private int _ackMode = Session.AUTO_ACKNOWLEDGE;
  private boolean _transacted = false;
  
  TopicConnection _connection;
  Topic _topic;
  
  public PooledTopicPublisherFactory(TopicConnection conn, Topic topic){
    _connection = conn;
    _topic = topic;
  }
  
  public void setTransacted(boolean tx){
    _transacted = tx;
  }
  
  public void setAckMode(int ackMode){
    _ackMode = ackMode;
  }

  public void activateObject(Object obj) throws Exception {
  }

  public void destroyObject(Object obj) throws Exception {
    QueueSenderRef ref = (QueueSenderRef)obj;
    ref.getSender().close();
    ref.getSession().close();
  }

  public Object makeObject() throws Exception {
    TopicSession session = _connection.createTopicSession(_transacted, _ackMode);
    TopicPublisher pub = session.createPublisher(_topic);
    return new TopicPublisherRef(pub, session);
  }

  public void passivateObject(Object obj) throws Exception {
  }

  public boolean validateObject(Object obj) {
    return true;
  }
  
}
