package org.sapia.site.plugins.documentation.template;

import java.util.HashMap;
import java.util.Map;

public class TemplateContext {
  
  private Map<String, String> vars;
  private static final ThreadLocal<TemplateContext> CONTEXT = new ThreadLocal<>();
  
  TemplateContext(Map<String, String> vars) {
    this.vars = vars;
  }
  
  public Map<String, String> getVars() {
    return vars;
  }
  
  public static void set(Map<String, String> vars) {
    CONTEXT.set(new TemplateContext(vars));
  }
  
  public static TemplateContext get() {
    TemplateContext context = CONTEXT.get();
    if (context == null) {
      context = new TemplateContext(new HashMap<String, String>());
    }
    return context;
  }

}
