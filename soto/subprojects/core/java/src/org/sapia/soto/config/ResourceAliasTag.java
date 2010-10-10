package org.sapia.soto.config;

import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.util.ResourceAlias;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

public class ResourceAliasTag extends ResourceAlias 
  implements EnvAware, ObjectCreationCallback{

  private Env _env;
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public Object onCreate() throws ConfigurationException {
    _env.getResourceHandlers().addResourceAlias(this);
    return new NullObjectImpl();
  }
  
}
