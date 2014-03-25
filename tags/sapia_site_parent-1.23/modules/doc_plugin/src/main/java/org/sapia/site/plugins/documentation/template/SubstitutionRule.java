package org.sapia.site.plugins.documentation.template;

/**
 * Specifies the behavior for substituting content.
 * 
 * @author yduchesne
 *
 */
public interface SubstitutionRule {
  
  /**
   * @param content some content.
   * @return the actual content to use from now on.
   */
  public String substitute(String content);

}
