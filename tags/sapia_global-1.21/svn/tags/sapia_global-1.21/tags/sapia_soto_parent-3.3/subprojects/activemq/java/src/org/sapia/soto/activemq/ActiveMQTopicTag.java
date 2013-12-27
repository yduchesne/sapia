package org.sapia.soto.activemq;

import org.apache.activemq.command.ActiveMQTopic;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

public class ActiveMQTopicTag implements ObjectCreationCallback{
  
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
      throw new ConfigurationException("Topic name not set");
    }
    ActiveMQTopic topic = new ActiveMQTopic(_name);
    if(_physicalName != null){
      topic.setPhysicalName(_physicalName);
    }
    return topic;
  }

}
