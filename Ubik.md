Welcome to Ubik's wiki.

Web site: http://www.sapia-oss.org/projects/ubik/

# History #

We've been using Ubik in production for a couple of years now. In fact, the project has been started in 2002. What a long way! EJBs were all the rage at that time (but slowly loosing ground to more lightweight approaches).

Anyway, Ubik has been designed for ease of use in mind. It's great for quickly implementing remote services, in a manner that is completely compatible with the "lightweight" philosophy (in fact, with exactly that philosophy in mind).

Ubik offers scalability and reliability without the complexity of JINI and the bloat of an appserver.



# Tips #

## How to start a JNDI Server Programmatically ##

```
package org.sapia.ubik.rmi.examples;

import org.sapia.ubik.rmi.naming.remote.EmbeddableJNDIServer;

public class JndiRunner {
  
  public static void main(String[] args) throws Exception{
    
    EmbeddableJNDIServer jndi = new EmbeddableJNDIServer("default", 1099);
    jndi.start(true);
    
    while(true){
      Thread.sleep(100000);
    }
  }
}
```