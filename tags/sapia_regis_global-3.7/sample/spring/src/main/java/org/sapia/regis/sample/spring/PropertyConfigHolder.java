package org.sapia.regis.sample.spring;

import org.sapia.config.users.management.Account3;
import org.sapia.regis.spring.NodeType;
import org.sapia.regis.spring.Prop;
import org.springframework.beans.factory.InitializingBean;

@NodeType(type=Account3.class)
public class PropertyConfigHolder implements InitializingBean{
  
  @Prop
  private String firstName;
  
  @Prop
  private String lastName;
  
  @Prop
  private String username;
  
  @Prop  
  private String password;
  
  public String getFirstName() {
    return firstName;
  }
  
  public String getLastName() {
    return lastName;
  }
  
  public String getUsername() {
    return username;
  }
  
  public String getPassword() {
    return password;
  }
  
  @Override
  public void afterPropertiesSet() throws Exception {
    System.out.println("**** " + getFirstName());
    System.out.println("**** " + getLastName());
    System.out.println("**** " + getUsername());
    System.out.println("**** " + getPassword());    
    
  }

}
