package org.sapia.qool;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;

import org.sapia.qool.Constants.Type;
import org.sapia.qool.queue.PooledQueueSessionFactory;
import org.sapia.qool.topic.PooledTopicSessionFactory;

/**
 * This class is meant to wrap a vendor-specific {@link Connection} in order to provide
 * session pooling behavior.
 * 
 * @author yduchesne
 *
 */
public class PooledJmsConnection implements Connection, TopicConnection, QueueConnection{

  private Map<Type, JmsSessionPool> pools = new HashMap<Type, JmsSessionPool>();

  private Type type;
  private PooledJmsConnectionFactory owner;
  private Connection delegate;
  private volatile boolean started;
  
  public PooledJmsConnection(Type type, Connection delegate) {
    pools.put(Type.QUEUE,   new JmsSessionPool(delegate, new PooledQueueSessionFactory()));
    pools.put(Type.TOPIC,   new JmsSessionPool(delegate, new PooledTopicSessionFactory()));
    pools.put(Type.GENERIC, new JmsSessionPool(delegate, new GenericPooledSessionFactory()));
    this.type = type;
    this.delegate = delegate;
  }  
  
  public void setConfig(Config conf){
    pool(Type.GENERIC).setConfig(conf);
    pool(Type.QUEUE).setConfig(conf);
    pool(Type.TOPIC).setConfig(conf);
  }
  
  /////////////////////// Connection interface

  public ConnectionConsumer createConnectionConsumer(Destination arg0,
      String arg1, ServerSessionPool arg2, int arg3) throws JMSException {
    return delegate.createConnectionConsumer(arg0, arg1, arg2, arg3);
  }
  
  public ConnectionConsumer createDurableConnectionConsumer(Topic arg0,
      String arg1, String arg2, ServerSessionPool arg3, int arg4)
      throws JMSException {
    return delegate.createDurableConnectionConsumer(arg0, arg1, arg2, arg3, arg4);
  }
  
  public Session createSession(boolean transacted, int ackMode) throws JMSException {
    return doAcquire(Type.GENERIC, transacted, ackMode);
  }
  
  public String getClientID() throws JMSException {
    return delegate.getClientID();
  }
  
  public ExceptionListener getExceptionListener() throws JMSException {
    return delegate.getExceptionListener();
  }
  
  public ConnectionMetaData getMetaData() throws JMSException {
    return delegate.getMetaData();
  }
  
  public void setClientID(String arg0) throws JMSException {
    delegate.setClientID(arg0);
  }
  
  public void setExceptionListener(ExceptionListener arg0) throws JMSException {
    delegate.setExceptionListener(arg0);
  }
  
  public synchronized void start() throws JMSException {
    if(!started){
      delegate.start();
      started = true;
    }
  }
  
  public synchronized void stop() throws JMSException {
    if(started){
      delegate.stop();
      started = false;
    }
  }

  public synchronized void close() throws JMSException {
    this.owner.release(this);
  }
  
  /////////////////////// TopicConnection interface
  
  public TopicSession createTopicSession(boolean transacted, int ackMode)
      throws JMSException {
    return (TopicSession)doAcquire(Type.TOPIC, transacted, ackMode);
  }
  
  public ConnectionConsumer createConnectionConsumer(Topic arg0, String arg1,
      ServerSessionPool arg2, int arg3) throws JMSException {
    return topics().createConnectionConsumer(arg0, arg1, arg2, arg3);
  }
  
  /////////////////////// QueueConnection interface

  public ConnectionConsumer createConnectionConsumer(Queue arg0, String arg1,
      ServerSessionPool arg2, int arg3) throws JMSException {
    return queues().createConnectionConsumer(arg0, arg1, arg2, arg3);
  }

  public QueueSession createQueueSession(boolean transacted, int ackMode)
      throws JMSException {
    return (QueueSession)doAcquire(Type.QUEUE, transacted, ackMode);
  }
  
  /////////////////////// Restricted methods
  
  void setOwner(PooledJmsConnectionFactory owner) {
    this.owner = owner;
  }
  
  Type getType() {
    return type;
  }
  
  void release(PooledJmsSession session){
    if(session.getOwner() != this){
      throw new IllegalArgumentException("Session not managed by this Connection: " +  session);
    }
    pool(session.getType()).release(session);
  }
  
  Connection getDelegate(){
    return delegate;
  }
  
  boolean recycle(){
    // TODO provide better implementation
    // i.e: detect if connection is still valid
    return true;
  }

  synchronized void shutdown() throws JMSException {
    for(JmsSessionPool p:this.pools.values()){
      p.close();
    }
    delegate.close();
  }
  
  private QueueConnection queues(){
    if(delegate instanceof QueueConnection){
      return (QueueConnection)delegate;
    }
    else{
      throw illegalConnectionType(QueueConnection.class);
    }
    
  }
  
  private TopicConnection topics(){
    if(delegate instanceof TopicConnection){
      return (TopicConnection)delegate;
    }
    else{
      throw illegalConnectionType(TopicConnection.class);
    }
  }
 
  private IllegalStateException illegalConnectionType(Class<?> expected){
    throw new IllegalStateException("Internal connection not instance of " + expected.getName() );
  }
  
  private JmsSessionPool pool(Type t){
    JmsSessionPool pool = this.pools.get(t);
    if(pool == null){
      throw new IllegalStateException("No session pool for " + t);
    }
    return pool;
  }
  
  private PooledJmsSession doAcquire(Type t, boolean transacted, int ackMode)
    throws JMSException{
    try{
      PooledJmsSession s = pool(t).acquire(transacted, ackMode);
      s.setOwner(this);
      return s;
    }catch(InterruptedException e){
      JMSException jex = new JMSException("Could not acquire JMS session");
      jex.setLinkedException(e);
      throw jex;
    }    
  }

}
