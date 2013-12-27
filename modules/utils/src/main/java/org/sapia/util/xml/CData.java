package org.sapia.util.xml;


// Import of Sun's JDK classes
// ---------------------------
import java.io.Serializable;


/**
 * The <CODE>CData</CODE> class is an object representation of a XML CDATA section and
 * it has only one attribute which is the content of the CDATA section.
 *
 * @author Jean-Cedric Desrochers
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class CData implements Serializable {
  /** The string content of this cdata. */
  private String _theContent;

  /**
   * Creates a new CData instance with the string content passed in.
   */
  public CData(String aContent) {
    _theContent = aContent;
  }

  /**
   * Returns the string content of this string buffer.
   *
   * @return The string content of this string buffer.
   */
  public String toString() {
    return _theContent;
  }
}
