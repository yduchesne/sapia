package org.sapia.soto.activemq.space;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.codehaus.activespace.jms.Marshaller;
import org.sapia.soto.activemq.space.ActiveSpaceServiceImpl.PolymorphicEntry;

public class MarshallerImpl implements Marshaller {
  
  private SpaceUtils _utils = new SpaceUtils();

  public Message marshall(Session session, Object value) throws JMSException {
    Message msg;
    if (value instanceof String) {
      msg = session.createTextMessage((String) value);
    }
    else{
      Class type;
      if(value instanceof PolymorphicEntry){
        PolymorphicEntry entry = (PolymorphicEntry)value;
        type = entry.asType;
        value = entry.o;
      }
      else{
        type = value.getClass();
      }
      if(value instanceof Serializable){
        msg = session.createObjectMessage((Serializable) value);
      }
      else{
        throw new IllegalStateException("Object not serializable: " + value);
      }
      try{
        _utils.setProperties(session, msg, value, type);
      }catch(Exception e){
        e.printStackTrace();
        throw new JMSException("Could not set entry properties", e.getMessage());
      }
    }
    return msg;
  }

  public Object unmarshall(Message message) throws JMSException {
    if (message instanceof ObjectMessage) {
      ObjectMessage objectMsg = (ObjectMessage) message;
      Object obj = objectMsg.getObject();
      if(obj instanceof Request){
        Request req = (Request)obj;
        req.setMessageId(message.getJMSMessageID());
        req.setReplyTo(message.getJMSReplyTo());
      }
      return obj;
    } else if (message instanceof TextMessage) {
      TextMessage textMsg = (TextMessage) message;
      return textMsg.getText();
    }
    return null;
  }

}
