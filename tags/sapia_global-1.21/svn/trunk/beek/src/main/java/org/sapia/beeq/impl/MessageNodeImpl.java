package org.sapia.beeq.impl;

import java.util.UUID;

import org.sapia.beeq.Message;
import org.sapia.beeq.MessageID;
import org.sapia.beeq.MessageNode;
import org.sapia.beeq.queue.Queue;

public class MessageNodeImpl implements MessageNode{
  
  private String nodeID;
  private Queue queue;
  
  public MessageNodeImpl(String nodeID, Queue queue){
    this.nodeID = nodeID;
    this.queue = queue;
  }
  
  public Message create(String clientGeneratedId) {
    MessageImpl msg = new MessageImpl();
    msg.setClientGeneratedId(clientGeneratedId);
    return msg;
  }
  
  public MessageID put(Message msg) {
    if(msg.getContentType() == null){
      throw new IllegalArgumentException("content type not set");
    }
    if(msg.getPayload() == null){
      throw new IllegalArgumentException("payload not set");
    }
    MessageID msgID = new MessageID(this.nodeID, UUID.randomUUID().toString());
    ((MessageImpl)msg).setID(msgID);
    queue.add(msg);
    return msgID;
  }
  
  public Message get(MessageID id) {
    return null;
  }

}
