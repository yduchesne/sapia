package org.sapia.qool;

import java.io.Serializable;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

import org.sapia.qool.Constants.TxStatus;
import org.sapia.qool.Constants.Type;

/**
 * Base class for pooled JMS sessions.
 * 
 * @author yduchesne
 *
 */
public class PooledJmsSession implements Session{

  protected Session delegate;
  private TxStatus status;
  private Type type;
  private PooledJmsConnection owner;
  private boolean isValid = true;
  
  protected PooledJmsSession(Type type, Session delegate, boolean transacted){
    this.type = type;
    this.delegate = delegate;
    if(transacted){
      status = TxStatus.STARTED;
    }
    else{
      status = TxStatus.NONE;
    }
  }
  
  public void close() throws JMSException {
    owner.release(this);
  }

  public QueueBrowser createBrowser(Queue arg0, String arg1)
      throws JMSException {
    return delegate.createBrowser(arg0, arg1);
  }

  public QueueBrowser createBrowser(Queue arg0) throws JMSException {
    return delegate.createBrowser(arg0);
  }

  /////////////////////////////////////////////////////////////////////
  // MESSAGE CREATION
  /////////////////////////////////////////////////////////////////////

  public BytesMessage createBytesMessage() throws JMSException {
    return delegate.createBytesMessage();
  }
  
  public MapMessage createMapMessage() throws JMSException {
    return delegate.createMapMessage();
  }

  public Message createMessage() throws JMSException {
    return delegate.createMessage();
  }

  public ObjectMessage createObjectMessage() throws JMSException {
    return delegate.createObjectMessage();
  }

  public ObjectMessage createObjectMessage(Serializable arg0)
      throws JMSException {
    return delegate.createObjectMessage(arg0);
  }

  public StreamMessage createStreamMessage() throws JMSException {
    return delegate.createStreamMessage();
  }

  public TextMessage createTextMessage() throws JMSException {
    return delegate.createTextMessage();
  }

  public TextMessage createTextMessage(String arg0) throws JMSException {
    return delegate.createTextMessage(arg0);
  }

  /////////////////////////////////////////////////////////////////////
  // MESSAGE CONSUMPTION
  /////////////////////////////////////////////////////////////////////

  public MessageConsumer createConsumer(Destination arg0, String arg1,
      boolean arg2) throws JMSException {
    return delegate.createConsumer(arg0, arg1, arg2);
  }

  public MessageConsumer createConsumer(Destination arg0, String arg1)
      throws JMSException {
    return delegate.createConsumer(arg0, arg1);
  }

  public MessageConsumer createConsumer(Destination arg0) throws JMSException {
    return delegate.createConsumer(arg0);
  }
  
  public TopicSubscriber createDurableSubscriber(Topic arg0, String arg1,
      String arg2, boolean arg3) throws JMSException {
    return delegate.createDurableSubscriber(arg0, arg1, arg2, arg3);
  }

  public TopicSubscriber createDurableSubscriber(Topic arg0, String arg1)
      throws JMSException {
    return delegate.createDurableSubscriber(arg0, arg1);
  }

  public void unsubscribe(String arg0) throws JMSException {
    delegate.unsubscribe(arg0);
  }  
  
  /////////////////////////////////////////////////////////////////////
  // MESSAGE PRODUCTION
  /////////////////////////////////////////////////////////////////////

  public MessageProducer createProducer(Destination arg0) throws JMSException {
    return delegate.createProducer(arg0);
  }
  
  /////////////////////////////////////////////////////////////////////
  // DESTINATIONS
  /////////////////////////////////////////////////////////////////////  

  public Queue createQueue(String arg0) throws JMSException {
    return delegate.createQueue(arg0);
  }

  public TemporaryQueue createTemporaryQueue() throws JMSException {
    return delegate.createTemporaryQueue();
  }

  public TemporaryTopic createTemporaryTopic() throws JMSException {
    return delegate.createTemporaryTopic();
  }

  public Topic createTopic(String arg0) throws JMSException {
    return delegate.createTopic(arg0);
  }

  /////////////////////////////////////////////////////////////////////
  // TRANSACTIONS
  /////////////////////////////////////////////////////////////////////  
  
  public boolean getTransacted() throws JMSException {
    return delegate.getTransacted();
  }

  public void recover() throws JMSException {
    delegate.recover();
  }

  public void rollback() throws JMSException {
    delegate.rollback();
    status = TxStatus.ROLLED_BACK;
  }
  
  public void commit() throws JMSException {
    delegate.commit();
    status = TxStatus.COMMITTED;
  }

  /////////////////////////////////////////////////////////////////////
  // MISC
  /////////////////////////////////////////////////////////////////////
  
  public void run() {
    delegate.run();
  }

  public void setMessageListener(MessageListener arg0) throws JMSException {
    delegate.setMessageListener(arg0);
  }
  
  public MessageListener getMessageListener() throws JMSException {
    return delegate.getMessageListener();
  }
  
  public int getAcknowledgeMode() throws JMSException {
    return delegate.getAcknowledgeMode();
  }

  /////////////////////////////////////////////////////////////////////
  // RESTRICTED
  /////////////////////////////////////////////////////////////////////
  
  Session getDelegate() {
    return delegate;
  }

  void setOwner(PooledJmsConnection connection){
    this.owner = connection;
  }
  
  PooledJmsConnection getOwner(){
    return this.owner;
  }
  
  Type getType() {
    return type;
  }
  
  TxStatus getStatus() {
    return status;
  }
  
  void flagTransacted(){
    this.status = TxStatus.STARTED;
  }
  
  boolean isValid() {
    return isValid;
  }
  
  boolean recycle(){
    if(status == TxStatus.NONE){
      return isValid;
    }
    else if(status == TxStatus.STARTED){
      // we are attempting a rollback (status should
      // be committed or rolled back at this point)
      try{
        this.delegate.rollback();
      }catch(JMSException e){
        try{
          this.delegate.close();
        }catch(JMSException e2){
          // noop
        }
      }
      // we do not pool transacted sessions
      return false;
    }
    else{
      return isValid;
    }
  }

}
