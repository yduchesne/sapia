package org.sapia.soto.activemq.space;

import java.io.Serializable;

import javax.jms.Destination;

public class Request implements Serializable{
  
  static final long serialVersionUID = 1;
  
  private String _msgId;
  private Object data;
  private Destination replyTo;

  void setMessageId(String messageId){
    _msgId = messageId;
  }

  public Object getData() {
    return data;
  }
  
  String getMessageId(){
    return _msgId;
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
  
  public static Request create(Object data){
    Request req = new Request();
    req.setData(data);
    return req;
  }
  
  public String toString(){
    return new StringBuffer("[")
      .append("messageId=").append(_msgId)
      .append(", replyTo=").append(replyTo)
      .append(", data=").append(data).toString();
  }

}
