package org.sapia.soto.activemq.space;

import java.lang.reflect.Field;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.Session;

public class SpaceUtils {
  

  private static final String JMSType = "JMSType";
  
  private static final Set PRIMITIVES = new HashSet();
  
  private Map _typeCache = Collections.synchronizedMap(new HashMap());
  
  static{
    PRIMITIVES.add(byte.class);
    PRIMITIVES.add(short.class);
    PRIMITIVES.add(int.class);
    PRIMITIVES.add(long.class);
    PRIMITIVES.add(float.class);
    PRIMITIVES.add(double.class);
    PRIMITIVES.add(Byte.class);
    PRIMITIVES.add(Short.class);
    PRIMITIVES.add(Integer.class);
    PRIMITIVES.add(Long.class);
    PRIMITIVES.add(Float.class);
    PRIMITIVES.add(Double.class);
    PRIMITIVES.add(String.class);        
  }

  public String createSelectorStringFor(Object template)
    throws Exception{
    if(template == null) return null;
    Field[] fields = template.getClass().getFields();
    StringBuffer buf = new StringBuffer();
    buf.append(getTypePredicates(template));
    
    for(int i = 0; i < fields.length; i++){
      Field f = fields[i];
      if((f.getModifiers() & Field.PUBLIC) == 0 && PRIMITIVES.contains(f.getType())){
        Object val = f.get(template);
        if(val != null){
          buf.append(" AND ");                  
          buf.append(f.getName());          
          buf.append("='").append(val.toString()).append("'");
        }
      }
    }
    return buf.toString();
  }
  public void setProperties(Message msg, Object entry, Class type) throws Exception{
    setProperties(null, msg, entry, type);
  }  
  
  public void setProperties(Session sess, Message msg, Object entry, Class type)
    throws Exception{
    if(entry == null) return;

    Object obj = entry;
    if(entry instanceof Request){
      Request req = (Request)entry;
      obj = req.getData();
      Destination dest = sess.createTemporaryQueue();
      req.setReplyTo(dest);
      msg.setJMSReplyTo(dest);
    }
    else if(entry instanceof Reply){
      Reply res = (Reply)entry;
      obj = res.getData();
      res.setMessageId(msg.getJMSMessageID());
      msg.setJMSCorrelationID(res.getMessageId());
    }
    
    msg.setJMSType(type.getName());
    if(obj != null){
      Field[] fields = obj.getClass().getFields();    
      for(int i = 0; i < fields.length; i++){
        Field f = fields[i];
        if((f.getModifiers() & Field.PUBLIC) == 0 && PRIMITIVES.contains(f.getType())){
          Object val = f.get(obj);
          if(val != null){
            msg.setStringProperty(f.getName(), val.toString());
          }
        }
      }
    }
    return;
  }
  
  private synchronized String getTypePredicates(Object o){
    synchronized(_typeCache){
      String pred = (String)_typeCache.get(o.getClass().getName());
      if(pred == null){
        pred = generateTypePredicates(o);
        _typeCache.put(o.getClass().getName(), pred);
      }
      return pred;
    }
    
  }
  
  private String generateTypePredicates(Object o){
    return new StringBuffer(JMSType).append("='")
      .append(o.getClass().getName()).append("'").toString();
    /*List lst = new ArrayList();
    Class clazz = o.getClass();
    lst.add(clazz);
    while(clazz != null){
      lst.add(clazz);
      clazz = clazz.getSuperclass();
    }
    clazz = o.getClass();
    appendInterfaces(clazz, lst);
    StringBuffer buf = new StringBuffer();
    buf.append("JMSType IN (");
    for(int i = 0; i < lst.size(); i++){
      Class type = (Class)lst.get(i);
      if(i > 0)buf.append(",");
      buf.append("'").append(type.getName()).append("'");
    }
    buf.append(")");
    return buf.toString();*/
  }
  
  private void appendInterfaces(Class clazz, List types){
    
    while(clazz != null){
      Class[] interfaces = clazz.getInterfaces();
      if(interfaces != null && interfaces.length > 0){
        for(int i = 0; i < interfaces.length; i++){
          if(!types.contains(interfaces[i])){
            types.add(interfaces[i]);
            appendInterfaces(interfaces[i], types);
          }
        }
      }
      clazz = clazz.getSuperclass();
    }
  }  
}
