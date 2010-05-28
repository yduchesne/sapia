package org.sapia.corus.db.persistence;

import org.sapia.corus.db.Matcher;

/**
 * This class implements the {@link Matcher} interface over the {@link Template} class.
 *  
 * @author yduchesne
 *
 */
public class TemplateMatcher<T> implements Matcher<T>{
  
  private Template<T> template;

  public TemplateMatcher(Template<T> template) {
    this.template = template;
  }
  
  /**
   * This method delegates matching to its internal {@link Template}.
   * 
   * @see Template
   */
  public boolean matches(T toMatch) {
    return template.matches(toMatch);
  }

}
