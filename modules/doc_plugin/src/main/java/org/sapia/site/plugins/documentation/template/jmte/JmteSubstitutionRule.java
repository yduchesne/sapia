package org.sapia.site.plugins.documentation.template.jmte;

import java.util.HashMap;
import java.util.Map;

import org.sapia.site.plugins.documentation.template.SubstitutionRule;
import org.sapia.site.plugins.documentation.template.TemplateContext;

import com.floreysoft.jmte.Engine;

public class JmteSubstitutionRule implements SubstitutionRule {
  
  private Engine engine = new Engine();
  
  public JmteSubstitutionRule() {
    engine.registerNamedRenderer(new ClassLinkRenderer());
    engine.registerNamedRenderer(new ClassNameRenderer());
  }
  
  @Override
  public String substitute(String content) {
    Map<String, Object> model = new HashMap<String, Object>();
    model.putAll(TemplateContext.get().getVars());
    model.put("functions", new NullObject());
    return engine.transform(content, model);
  }

}
