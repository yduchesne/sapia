package org.sapia.soto.config;

import org.sapia.resource.include.IncludeContext;
import org.sapia.resource.include.IncludeContextFactory;
import org.sapia.soto.Env;
import org.sapia.util.text.TemplateContextIF;

public class SotoIncludeContextFactory implements IncludeContextFactory{
  
  private Env _env;
  private TemplateContextIF _ctx;
  
  public SotoIncludeContextFactory(Env env, TemplateContextIF ctx){
    _env = env;
    _ctx = ctx;
  }
  
  public IncludeContext createInstance() {
    return new SotoIncludeContext(_ctx, _env);
  }

}
