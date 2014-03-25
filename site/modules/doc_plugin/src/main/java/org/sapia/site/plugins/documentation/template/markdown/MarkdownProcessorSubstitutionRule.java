package org.sapia.site.plugins.documentation.template.markdown;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.text.StrSubstitutor;
import org.markdown4j.Markdown4jProcessor;
import org.markdown4j.Plugin;
import org.sapia.site.plugins.documentation.template.SubstitutionRule;
import org.sapia.site.plugins.documentation.template.TemplateContext;

public class MarkdownProcessorSubstitutionRule implements SubstitutionRule {
  
  private Markdown4jProcessor processor = new Markdown4jProcessor();
  
  public MarkdownProcessorSubstitutionRule() {
    registerPlugin(new ParaPlugin());
  }
  
  @Override
  public String substitute(String content) {
    Map<String, String> vars = TemplateContext.get().getVars();
    try {
      if (!vars.isEmpty()) {
        StrSubstitutor ss = new StrSubstitutor(vars);
        String generated = ss.replace(content);
        return processor.process(generated);
      }      
      return processor.process(content);
    } catch (IOException e) {
      throw new IllegalStateException("Could not process markdown content", e);
    }
  }

  private void registerPlugin(Plugin p) {
    if (p instanceof ProcessorAware) {
      ((ProcessorAware) p).setProcessor(processor);
    }
    processor.registerPlugins(p);
  }
  
}
