package org.sapia.soto.activemq.space;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;

import org.codehaus.activespace.Space;
import org.codehaus.activespace.SpaceException;
import org.codehaus.activespace.SpaceListener;
import org.codehaus.activespace.jms.Marshaller;

public class TempSpace implements ActiveSpace{
  
  private ActiveSpaceServiceImpl.SpaceImpl delegate;
  private MessageConsumer consumer;
  private Marshaller marshaller;

  TempSpace(ActiveSpaceServiceImpl.SpaceImpl delegate, 
      MessageConsumer consumer, Marshaller marshaller){
    this.delegate = delegate;
    this.consumer = consumer;
    this.marshaller = marshaller;
  }
  
  public Space createChildSpace(String arg0) throws SpaceException {
    throw new UnsupportedOperationException();
  }
  
  public int getDispatchMode() {
    return delegate.getDispatchMode();
  }
  
  public String getName() {
    return delegate.getName();
  }
  
  public void put(Object arg0) {
    delegate.put(arg0);
  }
  
  public void put(Object arg0, long arg1) {
    delegate.put(arg0, arg1);
  }
  
  public void putAs(Object entry, Class type) {
    delegate.putAs(entry, type);
  }
  
  public void putAs(Object entry, long lease, Class type) {
    delegate.putAs(entry, lease, type);
  }
  
  public Object take(){
    try{
      return marshaller.unmarshall(consumer.receive());
    }catch(JMSException e){
      throw new RuntimeException(e);
    }
  }
  
  public Object take(long arg0) {
    try{
      return marshaller.unmarshall(consumer.receive(arg0));
    }catch(JMSException e){
      throw new RuntimeException(e);
    }
  }
  
  public Object takeNoWait() {
    try{
      return marshaller.unmarshall(consumer.receiveNoWait());
    }catch(JMSException e){
      throw new RuntimeException(e);
    }
  }
  
  public Space request(Request entry, long timeout, Class type) {
    return delegate.request(entry, timeout, type);
  }
  
  public void reply(Reply reply, long lease) {
    delegate.reply(reply, lease);
  }
  
  public void removeSpaceListener(SpaceListener arg0) {
    throw new UnsupportedOperationException();    
  }
  
  public void addSpaceListener(SpaceListener listener) {
    throw new UnsupportedOperationException();    
  }
  
  public void close() throws SpaceException {
    try{
      consumer.close();
    }catch(JMSException e){
      throw new SpaceException(e);
    }
  }
  
  
}
