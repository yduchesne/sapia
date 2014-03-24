package org.sapia.site.plugins.documentation.template.markdown;

import org.sapia.site.plugins.documentation.template.CompositeSubstitutionRule;

public class MarkdownSubstitutionRule extends CompositeSubstitutionRule {
  
  public MarkdownSubstitutionRule() {
    super.add(new MarkdownProcessorSubstitutionRule());
    super.add(new CodeStyleSubstitutionRule());
  }

}
