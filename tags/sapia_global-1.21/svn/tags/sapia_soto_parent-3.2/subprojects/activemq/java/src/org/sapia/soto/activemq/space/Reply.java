package org.sapia.soto.activemq.space;

import java.io.Serializable;

import javax.jms.Destination;

public class Reply implements Serializable{
  
  static final long serialVersionUID = 1;
  
  private String _msgId;
  private Object data;
  private Destination replyTo;
  
  String getMessageId(){
    return _msgId;
  }
  
  void setMessageId(String messageId){
    _msgId = messageId;
  }
  
  public Object getData() {
    return data;
  }

  void setData(Object data) {
    this.data = data;
  }
  
  Destination getReplyTo() {
    return replyTo;
  }

  void setReplyTo(Destination replyTo) {
    this.replyTo = replyTo;
  }
  
  public static Reply create(Request req, Object data){
    Reply res = new Reply();
    res.setData(data);
    res.setMessageId(req.getMessageId());
    res.setReplyTo(req.getReplyTo());
    return res;
  }
}
