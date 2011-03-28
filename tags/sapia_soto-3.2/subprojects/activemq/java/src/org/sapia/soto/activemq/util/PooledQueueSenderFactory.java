package org.sapia.soto.activemq.util;

import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.apache.commons.pool.PoolableObjectFactory;

public class PooledQueueSenderFactory implements PoolableObjectFactory{
  
  private int _ackMode = Session.AUTO_ACKNOWLEDGE;
  private boolean _transacted = false;
  
  QueueConnection _connection;
  Queue _queue;
  
  public PooledQueueSenderFactory(QueueConnection conn, Queue queue){
    _connection = conn;
    _queue = queue;
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
    QueueSession session = _connection.createQueueSession(_transacted, _ackMode);
    QueueSender  sender  = session.createSender(_queue);
    return new QueueSenderRef(sender, session);
  }

  public void passivateObject(Object obj) throws Exception {
  }

  public boolean validateObject(Object obj) {
    return true;
  }
  
}
