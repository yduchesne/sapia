package org.sapia.site.plugins.documentation.template;


public class Templater {

  private static SubstitutionRule PROCESSOR = SubstitutionRuleFactory.getDefault();
  
  private Templater() {
  }
  
  public static String render(String content) {
    return PROCESSOR.substitute(content);
  }

}
