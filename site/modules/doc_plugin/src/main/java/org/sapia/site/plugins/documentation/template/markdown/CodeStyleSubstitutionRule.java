package org.sapia.site.plugins.documentation.template.markdown;

import org.sapia.site.plugins.documentation.template.SubstitutionRule;

/**
 * Generates a <code>div</code> element with the <code>snippet</code> css class
 * for every sequence of <code>pre</code> and <code>code</code> elements.
 * @author yduchesne
 *
 */
public class CodeStyleSubstitutionRule implements SubstitutionRule {
  
  @Override
  public String substitute(String content) {
    String generated = content.replace("<pre><code>", "<div class=\"snippet\"><pre>");
    generated = generated.replace("</code></pre>", "</pre></div>");  
    return generated;
  }
  

}
