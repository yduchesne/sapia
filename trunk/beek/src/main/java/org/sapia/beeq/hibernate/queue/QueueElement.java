package org.sapia.beeq.hibernate.queue;

import java.util.Date;

import org.sapia.beeq.Message;

public class QueueElement {
  
  private long id;
  private Message message;
  private Date creationDate = new Date();
  private QueueElementStatus status = QueueElementStatus.CREATED;
   
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }
  
  public void setMessage(Message message) {
    this.message = message;
  }

  public Message getMessage() {
    return message;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public QueueElementStatus getStatus() {
    return status;
  }

  public void setStatus(QueueElementStatus status) {
    this.status = status;
  }
}
