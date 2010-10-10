package org.sapia.soto.activemq.util;

import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

public class TopicPublisherRef {
  
  private TopicPublisher _pub;
  private TopicSession _session;
  
  public TopicPublisherRef(TopicPublisher pub, TopicSession session){
    _pub = pub;
    _session = session;
  }
  
  public TopicPublisher getPublisher(){
    return _pub;
  }
  
  public TopicSession getSession(){
    return _session;
  }  

}
