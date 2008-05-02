package org.sapia.beeq.sender;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sapia.beeq.Message;

public class LoggingSender implements Sender{
  
  private Log log = LogFactory.getLog(getClass());
  
  public void send(Message message) throws Exception {
    if(log.isDebugEnabled()){
      log.debug("Sending message " + message.getID() + " to: " + message.getDestination());
      log.debug("payload:");
      log.debug(message.getPayload());
    }
  }
  
  public boolean accepts(Message msg) {
    return true;
  }
  

}
