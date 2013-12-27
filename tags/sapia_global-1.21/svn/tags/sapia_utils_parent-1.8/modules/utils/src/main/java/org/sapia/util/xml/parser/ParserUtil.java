package org.sapia.util.xml.parser;


// Imports of David Meggison's SAX classes
// ---------------------------------------
import org.xml.sax.Attributes;


/**
 * Utility methods used by this parser package.
 *
 * @author Jean-Cedric Desrochers
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ParserUtil {
  /**
   * Utility method that formats the SAX attributes passed in into a string form.
   *
   * @return The string form of the SAX attributes passed in.
   */
  public static String toStringAtributes(Attributes someAttributes) {
    StringBuffer aBuffer = new StringBuffer();

    for (int i = 0; i < someAttributes.getLength(); i++) {
      aBuffer.append("Attribute ").append(i).append(":")
             .append(" qualifiedName=").append(someAttributes.getQName(i))
             .append(" namespaceURI=").append(someAttributes.getURI(i))
             .append(" localName=").append(someAttributes.getLocalName(i))
             .append(" type=").append(someAttributes.getType(i))
             .append(" value=").append(someAttributes.getValue(i)).append("\n");
    }

    return aBuffer.toString();
  }

  /**
   * Parses the attributes passed in and extract the xml:base attribute's value.
   *
   * @return The value of the xml:base attribute or null if the attribute is not defined.
   */
  public static String extractXmlBaseURI(Attributes someAttributes) {
    String aValue = someAttributes.getValue(XMLDictionnaryIF.QUALIFIED_ELEMENT_XML_BASE);

    if (aValue == null) {
      aValue = someAttributes.getValue(XMLDictionnaryIF.XML_NAMESPACE_URI,
          XMLDictionnaryIF.LOCAL_ELEMENT_XML_BASE);
    }

    return aValue;
  }

  /**
   * Parses the attributes passed in and extract the xml:lang attribute's value.
   *
   * @return The value of the xml:lang attribute or null if the attribute is not defined.
   */
  public static String extractXmlLanguageCode(Attributes someAttributes) {
    String aValue = someAttributes.getValue(XMLDictionnaryIF.QUALIFIED_ELEMENT_XML_LANG);

    if (aValue == null) {
      aValue = someAttributes.getValue(XMLDictionnaryIF.XML_NAMESPACE_URI,
          XMLDictionnaryIF.LOCAL_ELEMENT_XML_LANG);
    }

    return aValue;
  }

  /**
   * Parses the attributes passed in and extract the xml:space attribute's value.
   *
   * @return The value of the xml:space attribute or null if the attribute is not defined.
   */
  public static String extractXmlSpaceValue(Attributes someAttributes) {
    String aValue = someAttributes.getValue(XMLDictionnaryIF.QUALIFIED_ELEMENT_XML_SPACE);

    if (aValue == null) {
      aValue = someAttributes.getValue(XMLDictionnaryIF.XML_NAMESPACE_URI,
          XMLDictionnaryIF.LOCAL_ELEMENT_XML_SPACE);
    }

    return aValue;
  }
}
