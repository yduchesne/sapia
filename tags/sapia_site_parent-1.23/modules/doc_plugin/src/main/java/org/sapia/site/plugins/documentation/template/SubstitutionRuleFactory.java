package org.sapia.site.plugins.documentation.template;

import org.sapia.site.plugins.documentation.template.jmte.JmteSubstitutionRule;
import org.sapia.site.plugins.documentation.template.markdown.MarkdownSubstitutionRule;

public class SubstitutionRuleFactory {

  private SubstitutionRuleFactory() {
  }

  /**
   * @return the default {@link SubstitutionRule} to use.
   */
  public static SubstitutionRule getDefault() {
    CompositeSubstitutionRule r = new CompositeSubstitutionRule();
    r.add(new MarkdownSubstitutionRule());
    r.add(new JmteSubstitutionRule());
    return r;
  }
}
