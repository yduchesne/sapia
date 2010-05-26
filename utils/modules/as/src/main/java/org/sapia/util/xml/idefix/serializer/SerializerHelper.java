package org.sapia.util.xml.idefix.serializer;

import org.sapia.util.xml.Namespace;
import org.sapia.util.xml.idefix.XmlBuffer;


/**
 * This class contains some static utility methods.
 *
 * @author <a href="mailto:jc@sapia-oss.org">Jean-Cedric Desrochers</a>
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html" target="sapia-license">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class SerializerHelper {
  /**
   * Returns the local name of the class of the object passed in.
   *
   * @param anObject The object fom which to get the local name class.
   * @return The local name of the class or empty string if the object is <code>null</code>.
   */
  public static String getLocalClassName(Object anObject) {
    if (anObject == null) {
      return "";
    }

    Class  aClass = anObject.getClass();

    String aQualifiedClassName = aClass.getName();
    String aPackageName        = aClass.getPackage().getName();
    String aLocalClassName     = aQualifiedClassName.substring(aPackageName.length() +
        1);

    return aLocalClassName;
  }

  /**
   * This utility method will return a string with the first character beign
   * lower case. The string return will be a substring of the passed in string
   * starting at the specified position.
   *
   * @param aString The string to convert.
   * @param aStartingIndex The starting index of the passed in string.
   * @return The converted string.
   * @exception IllegalArgumentException If the starting index is lower than zero.
   */
  public static String firstToLowerFromIndex(String aString, int aStartingIndex) {
    if (aStartingIndex < 0) {
      throw new IllegalArgumentException(
        "The starting position is lower than zero");
    }

    char[] newChars = new char[aString.length() - aStartingIndex];
    newChars[0] = Character.toLowerCase(aString.charAt(aStartingIndex));
    aString.getChars(aStartingIndex + 1, aString.length(), newChars, 1);

    return new String(newChars);
  }

  /**
   * This utility method will return a string with the first character beign
   * upper case. The string return will be a substring of the passed in string
   * starting at the specified position.
   *
   * @param aString The string to convert.
   * @param aStartingIndex The starting index of the passed in string.
   * @return The converted string.
   * @exception IllegalArgumentException If the starting index is lower than zero.
   */
  public static String firstToUpperFromIndex(String aString, int aStartingIndex) {
    if (aStartingIndex < 0) {
      throw new IllegalArgumentException(
        "The starting position is lower than zero");
    }

    char[] newChars = new char[aString.length() - aStartingIndex];
    newChars[0] = Character.toUpperCase(aString.charAt(aStartingIndex));
    aString.getChars(aStartingIndex + 1, aString.length(), newChars, 1);

    return new String(newChars);
  }

  /**
   * Utility method that encodes the attribute passed in using an inline
   * encoding style which will add an XML attribute on the current XML buffer.
   * 
   * @param aBuffer The XML buffer to write the attribute.
   * @param aNamespace The namespace of the attribute.
   * @param aName The name of the attribute.
   * @param aValue The value of the attribute.
   */
  public static void attributeEncodeInline(XmlBuffer aBuffer, Namespace aNamespace, String aName, String aValue) {
    aBuffer.addNamespace(aNamespace.getURI(), aNamespace.getPrefix());
    aBuffer.addAttribute(aNamespace.getURI(), aName, aValue);
    aBuffer.removeNamespace(aNamespace.getURI());
  }

  /**
   * Utility method that encodes the attribute passed in using the soap
   * encoding style which will create a new XML element with the name of
   * the attribute and put the value as the content of the new XML element.
   * 
   * @param aBuffer The XML buffer to write the attribute.
   * @param aNamespace The namespace of the attribute.
   * @param aName The name of the attribute.
   * @param aValue The value of the attribute.
   */
  public static void attributeEncodeSoap(XmlBuffer aBuffer, Namespace aNamespace, String aName, String aValue) {
    aBuffer.addNamespace(aNamespace.getURI(), aNamespace.getPrefix());
    aBuffer.startElement(aNamespace.getURI(), aName);
    aBuffer.addContent(aValue);
    aBuffer.endElement(aNamespace.getURI(), aName);
    aBuffer.removeNamespace(aNamespace.getURI());
  }
}
