package org.sapia.regis.spring;

import java.util.Date;

@NodeType(type=TestNodePropertyCapable.class)
public class TestPropertyHolder {
  
  @Prop
  private String username;
  
  @Prop(name="age")
  private int age;
  
  @Prop
  private boolean isValid;
  
  @Prop
  private boolean admin;
  
  private long timestamp;
  
  @Prop(defaultTo="Welcome")
  private String greeting;

  @Prop
  private Date birthDate;
  
  @Prop
  private TestGender gender;
  
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
  
  @Prop
  void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
  
  public long getTimestamp() {
    return timestamp;
  }
  
  public String getGreeting() {
    return greeting;
  }
  
  public Date getBirthDate() {
    return birthDate;
  }
  
  public TestGender getGender() {
    return gender;
  }
}
