package org.sapia.soto.activemq.util;

import java.io.Serializable;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.Session;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.sapia.soto.Service;
import org.sapia.soto.ubik.monitor.FeedbackMonitorable;

/**
 * An instance of this class pools {@link org.sapia.soto.activemq.util.TopicPublisherRef} instances. This
 * class provides convenience methods to client application that wish sending basic
 * JMS text and object messages. If applications need more control, then this
 * class should be extended - so that the pooled TopicPublisherRef instances
 * can be used directly. 
 * 
 * @author yduchesne
 *
 */
public class TopicPublisherPool extends GenericObjectPool 
  implements Service, FeedbackMonitorable{
  
  private int _ackMode = Session.AUTO_ACKNOWLEDGE;
  private boolean _transacted = false;
  
  private Topic _topic;
  private TopicConnection _connection;
  private TopicConnectionFactory _factory;
  
  /**
   * The default <code>Topic</code> to which this instance will send messages.
   * @param topic a <code>Topic</code>.
   */
  public void setTopic(Topic topic){
    _topic = topic;
  }

  /**
   * @param fac the <code>TopicConnectionFactory</code> used by this instance to create
   * <code>TopicConnection</code>s.
   * 
   * @throws JMSException
   */
  public void setTopicConnectionFactory(TopicConnectionFactory fac){
    _factory = fac;
  }
  
  /**
   * @param tx <code>transacted</code>
   */
  public void setTransacted(boolean tx){
    _transacted = tx;
  }
  
  /**
   * @param ackMode sets the JMS message acknowledgement mode of this instance. 
   * Defaults to <code>Session.AUTO_ACKNOWLEDGE</code>.
   */
  public void setAckMode(int ackMode){
    _ackMode = ackMode;
  }  
  
  /**
   * Sends the given message to the topic held by this instance.
   * @param msg a message string.
   * @throws Exception if a problem occurs sending the given message.
   * @throws JMSException if the given message could not be sent by
   * the underlying JMS <code>TopicPubliser</code>.
   */
  public void sendTextMessage(String msg) throws Exception, JMSException{
    TopicPublisherRef ref = null;
    try{
      ref = (TopicPublisherRef)super.borrowObject();
      ref.getPublisher().send(ref.getSession().createTextMessage(msg));
    }finally{
      if(ref != null){
        super.returnObject(ref);
      }
    }
  }
  
  /**
   * Sends the given message to the queue held by this instance.
   * @param msg a serializable message object.
   * @throws Exception if a problem occurs sending the given message.
   * @throws JMSException if the given message could not be sent by
   * the underlying JMS <code>TopicPublisher</code>.
   */
  public void sendObjectMessage(Serializable msg) throws Exception, JMSException{
    TopicPublisherRef ref = null;
    try{
      ref = (TopicPublisherRef)super.borrowObject();
      ref.getPublisher().send(ref.getSession().createObjectMessage(msg));
    }finally{
      if(ref != null){
        super.returnObject(ref);
      }
    }
  }  
  
  public Properties monitor() throws Exception {
    Properties props = new Properties();
    props.setProperty("numActive", Integer.toString(super.getNumActive()));
    props.setProperty("numIdle", Integer.toString(super.getNumIdle()));
    return props;
  }
  
  public void init() throws Exception {
    if(_factory == null){
      throw new IllegalStateException("TopicConnectionFactory not set");
    }

    _connection = _factory.createTopicConnection();
    PooledTopicPublisherFactory pubFac = 
      new PooledTopicPublisherFactory(_connection, _topic);
    pubFac.setAckMode(_ackMode);
    pubFac.setTransacted(_transacted);
    super.setFactory(pubFac);
  }
  
  public void start() throws Exception {
    _connection.start();
  }
  
  public void dispose() {
    try{
      _connection.stop();
      _connection.close();
    }catch(JMSException e){
      //noop
    }
    try{
      super.close();
    }catch(Exception e){
      //noop
    }
  }

}
