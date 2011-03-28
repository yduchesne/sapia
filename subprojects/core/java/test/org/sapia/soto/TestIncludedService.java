/*
 * TestIncludedService.java
 *
 * Created on May 2, 2005, 8:09 PM
 */

package org.sapia.soto;

/**
 *
 * @author yduchesne
 */
public class TestIncludedService implements EnvAware, Service{
  
  private Env _env;
  
  /** Creates a new instance of TestIncludedService */
  public TestIncludedService() {
  }
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public void init() throws Exception{
    _env.resolveResource("resource.txt");
  }
  
  public void start(){}  
  
  public void dispose(){}
 
}
