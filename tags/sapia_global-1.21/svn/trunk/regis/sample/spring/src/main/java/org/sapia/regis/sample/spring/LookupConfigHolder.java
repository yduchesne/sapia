package org.sapia.regis.sample.spring;

import org.sapia.config.users.Management;
import org.sapia.config.users.ManagementNode;
import org.sapia.regis.Registry;
import org.sapia.regis.spring.Lookup;
import org.sapia.regis.util.Work;
import org.springframework.beans.factory.InitializingBean;

public class LookupConfigHolder implements InitializingBean{
  
  private Registry  registry;
  
  @Lookup
  private Management management;
  
  @Override
  public void afterPropertiesSet() throws Exception {
    
    // being zealous: using the Registry in the context of a session is currently
    // necessary only in the context of the Hibernate implementation.
    // see the Work class for more details.
    Work.with(registry).run(new Runnable(){
      @Override
      public void run() {
        for(ManagementNode n : management.getManagementNodes()){
          System.out.println("**** got: " + n.getFirstName() + " " + n.getLastName());
        }
      }
    });
  }

}
