package org.sapia.soto.activemq.space;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.codehaus.activespace.Space;
import org.codehaus.activespace.SpaceException;
import org.codehaus.activespace.SpaceListener;
import org.codehaus.activespace.jms.JmsSpace;
import org.codehaus.activespace.jms.JmsSpaceFactory;
import org.sapia.soto.Service;

/**
 * This class implements the {@link org.codehaus.activespace.SpaceFactory} interface over JMS.
 * It expects a JMS {@link javax.jms.ConnectionFactory} to be set at configuration time.
 * <p>
 * This instance internally uses the {@link org.sapia.soto.activemq.space.MarshallerImpl} class,
 * which introspects object fields in order to set the corresponding property (name/value) on sent JMS messages.
 * All primitive, not-null, public fields are set as properties.
 * <p>
 * 
 * @see org.sapia.soto.activemq.space.ActiveSpaceFactory#createSpace(String, int, Object)
 *  
 * @author yduchesne
 *
 */
public class ActiveSpaceServiceImpl implements Service, ActiveSpaceFactory{

  private JmsSpaceFactory _spaceFac;
  private ConnectionFactory _connFac;
  private SpaceUtils _utils = new SpaceUtils();
  
  
  // Service methods //////  
  public void init() throws Exception {
    if(_connFac == null){
      throw new IllegalStateException("ConnectionFactory not set");
    }
    _spaceFac = new JmsSpaceFactory(_connFac);
    _spaceFac.setMarshaller(new MarshallerImpl());
  }
  
  public void start() throws Exception {
  }
  
  public void dispose() {
  }

  // instance methods //////
  
  /**
   * @param factory the <code>ConnectionFactory</code> to use to
   */
  public void setConnectionFactory(ConnectionFactory factory){
    _connFac = factory;
  }

  // ActiveSpace methods //////
  public Space createSpace(String destination, int deliveryMode, String selector) throws SpaceException {
    return new SpaceImpl((JmsSpace)_spaceFac.createSpace(destination, deliveryMode, selector), null);
  }
  
  public Space createSpace(String destination, int deliveryMode, Object template) throws SpaceException {
    try{
      if(template == null){
        return new SpaceImpl((JmsSpace)_spaceFac.createSpace(destination, deliveryMode, null), null);      
      }
      else{
        return new SpaceImpl((JmsSpace)_spaceFac.createSpace(destination, deliveryMode, _utils.createSelectorStringFor(template)), template.getClass());
      }
    }catch(Exception e){
      throw new SpaceException("Could not create selector from template object", e);
    }
  }
  
  public class SpaceImpl implements ActiveSpace{
    JmsSpace _space;
    Class _type;
    
    
    SpaceImpl(JmsSpace space, Class type){
      _space = space;
      _type = type;
    }
    public void addSpaceListener(SpaceListener arg0) {
      _space.addSpaceListener(arg0);
    }
    public void removeSpaceListener(SpaceListener arg0) {
      _space.removeSpaceListener(arg0);
    }
    public void close() throws SpaceException {
      _space.close();
    }
    public Space createChildSpace(String arg0) throws SpaceException {
      return _space.createChildSpace(arg0);
    }
    public int getDispatchMode() {
      return _space.getDispatchMode();
    }
    public String getName() {
      return _space.getName();
    }
    public void put(Object arg0) {
      if(_type != null){
        if(_type.isAssignableFrom(arg0.getClass())){
          putAs(arg0, _type);
        }
        else{
          _space.put(arg0);
        }
      }
      else{
        _space.put(arg0);
      }
    }
    public void put(Object arg0, long arg1) {
      if(_type != null){
        if(_type.isAssignableFrom(arg0.getClass())){
          putAs(arg0, arg1, _type);
        }
        else{
          _space.put(arg0, arg1);
        }
      }
      else{
        _space.put(arg0, arg1);
      }
    }
    public void putAs(Object entry, Class type) {
      _space.put(new PolymorphicEntry(type, entry));
    }
    public void putAs(Object entry, long lease, Class type) {
      _space.put(new PolymorphicEntry(type, entry), lease);
    }
    public Space request(Request request, long timeout, Class type) {
      _space.put(new PolymorphicEntry(type, request), timeout);
      try{
        Destination dest = request.getReplyTo();
        Session s = _space.getAsyncSession();
        MessageConsumer consumer = s.createConsumer(dest);
        return new TempSpace(this, consumer, _space.getMarshaller());
      }catch(RuntimeException e){
        throw e;
      }catch(Exception e){
        throw new RuntimeException("Caught exception while sending response", e);
      }      
    }
    public void reply(Reply reply, long lease) {
      MessageProducer prod = null;
      try{
        Session s = ((JmsSpace)_space).getAsyncSession();
        ObjectMessage msg = s.createObjectMessage();
        Destination dest = reply.getReplyTo();
        msg.setObject(reply);
        _utils.setProperties(s, msg, reply, reply.getClass());
        prod = s.createProducer(dest);
        prod.setDeliveryMode(Session.AUTO_ACKNOWLEDGE);
        prod.setTimeToLive(lease);
        prod.send(msg);
      }catch(RuntimeException e){
        throw e;
      }catch(Exception e){
        throw new RuntimeException("Caught exception while sending response", e);
      }finally{
        if(prod != null){
          try{
            prod.close();
          }catch(Exception e){
            // noop;
          }
        }
      }
       
    }    
    public Object take() {
      return _space.take();
    }
    public Object take(long arg0) {
      return _space.take(arg0);
    }
    public Object takeNoWait() {
      return _space.takeNoWait();
    }
    
  }
  
  static final class PolymorphicEntry{
    Class asType;
    Object o;
    
    PolymorphicEntry(Class asType, Object o){
      Object instance;
      if(o instanceof Request){
        instance = ((Request)o).getData();
      }
      else{
        instance = o;
      }
      this.asType = asType != null ? asType : instance.getClass();
      if(!this.asType.isAssignableFrom(instance.getClass())){
        this.asType = instance.getClass();
      }
      this.o = o;
    }
  }
  
  static final class ResponseQueue implements Runnable{
    
    Space input;
    
    ResponseQueue(Space responses){
      this.input = responses;
    }
    

    public void run() {
      
    }
  }
  
}
