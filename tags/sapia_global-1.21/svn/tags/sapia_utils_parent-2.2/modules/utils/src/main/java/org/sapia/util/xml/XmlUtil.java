package org.sapia.util.xml;


/**
 * This class contains some utility methods use by the Sapia XML tools.
 *
 * @author Jean-Cedric Desrochers
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class XmlUtil {
  /** Defines the ":" delimiter that separates local name and prefix */
  private static final char PREFIX_SEPARATOR = ':';

  /**
   * Extract the namespace prefix from the qualified element name passed in.
   * If the the qualified name passed in is null, it returns null.
   *
   * @param aQualifiedName The qualified name from which to extract the prefix.
   * @return The extracted prefix or null if the qualified name is null;
   */
  public static String extractPrefix(String aQualifiedName) {
    String aPrefix = null;

    if (aQualifiedName != null) {
      int anIndex = aQualifiedName.indexOf(PREFIX_SEPARATOR);

      if (anIndex > -1) {
        aPrefix = aQualifiedName.substring(0, anIndex);
      }
    }

    return aPrefix;
  }
}
