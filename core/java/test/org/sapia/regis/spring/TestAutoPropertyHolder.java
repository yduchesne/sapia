package org.sapia.regis.spring;

import java.util.Date;

@NodeType(type=TestNodePropertyCapable.class, auto=true)
public class TestAutoPropertyHolder {
  
  private Date birthDate;
  
  private String username;
  
  private int age;
  
  private boolean isValid;
  
  private boolean admin;
  
  private long timestamp;
  
  public String getUsername() {
    return username;
  }
  
  public int getAge() {
    return age;
  }
  
  public boolean isAdmin() {
    return admin;
  } 
  
  public boolean isValid() {
    return isValid;
  }
  
  void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
  
  public long getTimestamp() {
    return timestamp;
  }

  public Date getBirthDate() {
    return birthDate;
  }
}
