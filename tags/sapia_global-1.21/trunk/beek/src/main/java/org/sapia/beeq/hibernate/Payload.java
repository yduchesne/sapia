package org.sapia.beeq.hibernate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Payload {
  
  private long id, version;
  private byte[] content;
  private PayloadType type;
  
  public byte[] getContent() {
    return content;
  }
  public void setContent(byte[] content) {
    this.content = content;
  }
  public long getId() {
    return id;
  }
  public void setId(long id) {
    this.id = id;
  }
  public PayloadType getType() {
    return type;
  }
  public void setType(PayloadType type) {
    this.type = type;
  }
  
  public static Payload create(Object data){
    Payload payload = new Payload();    
    if(data instanceof String){
      payload.setType(PayloadType.STRING);
      payload.setContent(((String)data).getBytes());
    }
    else if(data instanceof byte[]){
      payload.setType(PayloadType.BYTES);
      payload.setContent((byte[])data);
    }
    else{
      payload.setType(PayloadType.OBJECT);
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      try{
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(data);
        oos.flush();
        payload.setContent(bos.toByteArray());
      }catch(IOException e){
        throw new IllegalArgumentException("Could not serialize payload data", e);
      }
    }
    return payload;
  }
  
  public Object getData(){
    if(getContent() == null){
      return null;
    }
    if(getType() == PayloadType.STRING){
      return new String(getContent());
    }
    else if(getType() == PayloadType.BYTES){
      return getContent();
    }
    else{
      ByteArrayInputStream bis = new ByteArrayInputStream(getContent());
      try{
        ObjectInputStream ois = new ObjectInputStream(bis);
        return ois.readObject();
      }catch(Exception e){
        throw new IllegalArgumentException("Could not deserialize payload data", e);
      }
    }
  }
  public long getVersion() {
    return version;
  }
  public void setVersion(long version) {
    this.version = version;
  }

}
