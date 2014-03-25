package org.sapia.site.plugins.documentation.template;

import java.util.ArrayList;
import java.util.List;

public class CompositeSubstitutionRule implements SubstitutionRule {

  private List<SubstitutionRule> rules = new ArrayList<>();
  
  public CompositeSubstitutionRule add(SubstitutionRule rule) {
    rules.add(rule);
    return this;
  }
  
  @Override
  public String substitute(String content) {
    String newContent = content;
    for (SubstitutionRule r : rules) {
      newContent = r.substitute(newContent);
    }
    return newContent;
  }
}
