package org.sapia.soto.spring.resource;

import java.io.IOException;

import org.sapia.resource.ResourceNotFoundException;
import org.sapia.soto.Env;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * Implements Spring's {@link ResourceLoader} interface.
 * 
 * @author yduchesne
 *
 */
public class SpringResourceLoader implements ResourceLoader{
  
  private ClassLoader _loader;
  private Env _env;
  
  public SpringResourceLoader(Env env, ClassLoader loader) {
    _env = env;
    _loader = loader;
  }
  
  public ClassLoader getClassLoader() {
    return _loader;
  }
  
  public Resource getResource(String uri) {
    try{
      return new SpringResource(_env.resolveResource(uri));
    }catch(ResourceNotFoundException e){
      return new NullSpringResource(uri);
    }catch(IOException e){
      return new NullSpringResource(uri);
    }
    
  }

}
