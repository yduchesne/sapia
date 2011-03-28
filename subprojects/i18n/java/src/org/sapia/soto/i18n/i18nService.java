/*
 * i18nService.java
 *
 * Created on June 3, 2005, 7:48 PM
 */

package org.sapia.soto.i18n;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * This interface specifies the behavior of a service that performs lookup of 
 * localized text (strings).
 * <p>
 * Internalized text is categorized by ID and group: an ID identifies a specific
 * type of text. The type is arbitatry; it is just an identifier whose significance
 * is understood by client applications. For example, a given salutation 
 * (such as "hello world") could be kept under a given identifier. The different
 * corresponding localized salutations would all be kept under that identifier.
 * <p>
 * Furthermore, the localized text is in addition further categorized by group.
 * For example, imagine that in a given user interface navigation is made 
 * possible through a menu; each item in a menu consists of a named link leading
 * to a specific section. In this case, the menu could be localized in the 
 * following way: it could be found under an arbitratry identifier (for 
 * example, "navigation"), with each item corresponding to a group (each
 * group having its own identifier: "home", "products", etc.). Then, each
 * localized text (corresponding to the actual label of a menu item) could
 * be found under a given group. Conceptualley, we would have the following 
 * structure:
 * <ul>
 *  <li>navigation
 *    <ul>
 *      <li>home
 *        <li>home (en)</li>
 *        <li>accueil (fr)</li>
 *        <li>...</li> 
 *      </li>
 *      <li>...
 *    </ul>
 *  </li>
 * </ul>
 * <p>
 * Thus, to retrieve the label of the home page in french, for the navigation
 * menu, we would pass in the "nagivation" and "home" identifiers (and get
 * "accueil" in return).
 *
 * @author yduchesne
 */
public interface i18nService {

  /**
   * @param id the identifier of the set of localized resources in which to look
   * for the desired text.
   * @param groupId the identifier of the group within the set of localized
   * resources.
   * @param locale the <code>Locale</code>
   *
   * @return String the localized string that was found, or a default message 
   *  if no such string exists.
   */
  public String getText(String id, String groupId, Locale locale);

  /**
   * This method returns localized text and in addition takes a map of
   * parameters intended to be used as variables within the returned text -
   * this method's implementation is expected to perform variable interpolation.
   *
   * @see #getText(String, String, Locale)
   */
  public String getText(String id, String groupId, Map parameters, Locale locale);

  /**
   * @return the <code>Collection</code> of <code>Group</code>s corresponding to
   * the given resource identifier.
   *
   * @see Group
   */ 
  public Collection getGroups(String id);
  
}
