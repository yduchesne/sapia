package org.sapia.soto.jmx;

public class TestBean {
  
  private String firstName, lastName;
  private int  age;
  
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  
  public void incrementAge(){
    age++;
  }
  
  

}
