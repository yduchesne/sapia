package org.sapia.soto.activemq;

import org.apache.activemq.command.ActiveMQQueue;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

public class ActiveMQQueueTag implements ObjectCreationCallback{
  
  private String _name;
  private String _physicalName;
  
  public void setName(String name){
    _name = name;
  }
  
  public void setPhysicalName(String name){
    _physicalName = name;
  }  
  
  public Object onCreate() throws ConfigurationException {
    if(_name == null){
      throw new ConfigurationException("Queue name not set");
    }
    ActiveMQQueue queue = new ActiveMQQueue(_name);
    if(_physicalName != null){
      queue.setPhysicalName(_physicalName);
    }
    return queue;
  }

}
