package org.sapia.soto.activemq.util;

import javax.jms.QueueSender;
import javax.jms.QueueSession;

public class QueueSenderRef {
  
  private QueueSender _sender;
  private QueueSession _session;
  
  public QueueSenderRef(QueueSender sender, QueueSession session){
    _sender = sender;
    _session = session;
  }
  
  public QueueSender getSender(){
    return _sender;
  }
  
  public QueueSession getSession(){
    return _session;
  }  

}
