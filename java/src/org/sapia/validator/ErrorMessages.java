package org.sapia.validator;

import java.util.Locale;

/**
 * Holds a hierarchy of error messages (organized by "locale").
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ErrorMessages {
  private Hierarchy _root = new Hierarchy(null);

  /**
   * Constructor for ErrorMessages.
   */
  public ErrorMessages() {
  }

  /**
   * Adds an <code>ErrorMessage</code> to this instance.
   *
   * @param msg an <code>ErrorMessage</code>.
   */
  public void addErrorMessage(ErrorMessage msg) {
    _root.bind(msg);
  }

  /**
   * Returns the <code>ErrorMessage<code> corresponding to the given locale,
   * represented by a path.
   * 
   * @return an <code>ErrorMessage</code>.
   * 
   * @param path a locale, represented by a path of the form: 
   * <code>language/country/variant</code>.
   */
  public ErrorMessage getErrorMessageFor(String path) {
    return _root.reverseLookup(path);
  }

  /**
   * Returns the <code>ErrorMessage<code> corresponding to the given locale,
   * represented by a path.
   * 
   * @return an <code>ErrorMessage</code>.
   * 
   * @param localel a <code>Locale</code>.
   */
  public ErrorMessage getErrorMessageFor(Locale locale) {
    StringBuffer buf = new StringBuffer();

    if ((locale.getLanguage() != null) && (locale.getLanguage().length() > 0)) {
      buf.append(locale.getLanguage());
    }

    if ((locale.getCountry() != null) && (locale.getCountry().length() > 0)) {
      buf.append('/').append(locale.getCountry());
    }

    if ((locale.getVariant() != null) && (locale.getVariant().length() > 0)) {
      buf.append('/').append(locale.getVariant());
    }
    return getErrorMessageFor(buf.toString());
  }
}
