package org.sapia.util.xml.idefix;


// Import of Sapia's utility classes
// ---------------------------------
import org.sapia.util.xml.Attribute;

// Import of Sun's JDK classes
// ---------------------------
import java.io.IOException;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * The <CODE>XmlScribe</CODE> class contain utility methods that generates
 * XML string. It is usefull to generate string for starting or ending element
 * with or without namespace prefixes, or to encode a string using the XML
 * encoding rule that applies to the characters '<', '>', '"', ''' and '&'.
 *
 * @author Jean-Cedric Desrochers
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class XmlScribe {

  /** Defines an empty map. */
  private static final List EMPTY_LIST = new ArrayList(0);

  /** Defines the character encoding to use to generate the streaming methods. */
  private String _theStreamEncoding;
  
  /** Indicates if the output will be indented or not (default to false). */
  private boolean _isIndentingOuput;
  
  /**
   * Creates a new XmlScribe instance using the default UTF-8 as character encoding
   * for the streaming methods.
   */
  public XmlScribe() {
    _theStreamEncoding = "UTF-8";
    _isIndentingOuput = false;
  }

  /**
   * Creates a new XmlScribe instance with the passed in parameter.
   *
   * @param aStreamEncoding The character encoding of the streaming methods.
   */
  public XmlScribe(String aStreamEncoding) {
    _theStreamEncoding = aStreamEncoding;
  }

  /**
   * Returns the value of the indenting output indicator of this xml scribe.
   * 
   * @return True if this scribe indents xml output, false otherwise.
   */
  public boolean isIndentingOuput() {
    return _isIndentingOuput;
  }

  /**
   * Changes the value of the indenting output indicator of this xml scribe.
   * 
   * @param aValue The new value of the indicator.
   */
  public void setIndentingOutput(boolean aValue) {
    _isIndentingOuput = aValue;
  }
  
  /**
   * Changes the character encoding used in the streaming methods of the XMl scribe.
   *
   * @param anEncoding The new character encofing.
   */
  public void setStreamEncoding(String anEncoding) {
    _theStreamEncoding = anEncoding;
  }

  /**
   * Returns the character encoding used in the streaming methods of the XMl scribe.
   *
   * @return The character encoding used in the streaming methods of the XMl scribe.
   */
  public String getStreamEncoding() {
    return _theStreamEncoding;
  }

  /**
   * Builds the XML declaration and appends it into the buffer.
   *
   * @param aCharacterEncoding The character encoding of the xml declaration or
   *        null if it's not defined.
   * @param aBuffer The string buffer to which add the created string.
   */
  public void composeXmlDeclaration(String aCharacterEncoding,
    StringBuffer aBuffer) {
    if ((aCharacterEncoding != null) && (aCharacterEncoding.length() > 0)) {
      aBuffer.append("<?xml version=\"1.0\" encoding=\"")
             .append(aCharacterEncoding).append("\" ?>\n");
    } else {
      aBuffer.append("<?xml version=\"1.0\" ?>\n");
    }
  }

  /**
   * Builds the XML declaration and appends it into the output stream.
   *
   * @param aCharacterEncoding The character encoding of the xml declaration or
   *        null if it's not defined.
   * @param aStream The output stream to which add the created string.
   * @exception IOException If an error occurs while writing to the outpt stream.
   */
  public void composeXmlDeclaration(String aCharacterEncoding,
    OutputStream aStream) throws IOException {
    StringBuffer aBuffer = new StringBuffer();
    composeXmlDeclaration(aCharacterEncoding, aBuffer);
    aStream.write(aBuffer.toString().getBytes(_theStreamEncoding));
  }
  
  /**
   * Builds an XML starting element with the arguments passed in and appends the string to the buffer.
   *
   * @param aNamespacePrefix The namespace prefix of the element.
   * @param anElementName The name of the element to create.
   * @param someAttributes A list of the attribtues of the element.
   * @param isEmptyElement Tells wether to create an empty element or not.
   * @param aBuffer The string buffer to which add the created string.
   * @param aNestedLevel The nested level of the xml element to generate (for indentation).
   */
  public void composeStartingElement(String aNamespacePrefix, String anElementName,
      		List someAttributes, boolean isEmptyElement, StringBuffer aBuffer, int aNestedLevel) {
    // Starting the element
    if (_isIndentingOuput) {
      if (aNestedLevel > 0) {
        aBuffer.append("\n");
        for (int i = 0; i < aNestedLevel; i++) {
          aBuffer.append("  ");
        }
      }
    }
    aBuffer.append("<");
    composeQualifiedName(aNamespacePrefix, anElementName, aBuffer);

    // Adding any attributes
    for (Iterator it = someAttributes.iterator(); it.hasNext();) {
      Attribute anAttribute = (Attribute) it.next();

      // Add the attribute only if there's a value
      if ((anAttribute.getValue() != null) &&
            (anAttribute.getValue().length() > 0)) {
        aBuffer.append(" ");
        composeQualifiedName(anAttribute.getNamespacePrefix(),
          anAttribute.getName(), aBuffer);
        aBuffer.append("=\"");
        xmlEncode(anAttribute.getValue(), aBuffer);
        aBuffer.append("\"");
      }
    }

    // Closing the element
    if (isEmptyElement) {
      aBuffer.append(" />");
    } else {
      aBuffer.append(">");
    }
  }

  /**
   * Builds an XML starting element with the arguments passed in and appends the string to the output stream.
   *
   * @param aNamespacePrefix The namespace prefix of the element.
   * @param anElementName The name of the element to create.
   * @param someAttributes A list of the attribtues of the element.
   * @param isEmptyElement Tells wether to create an empty element or not.
   * @param aStream The output stream to which add the created string.
   * @exception IOException If an error occurs while writing to the output stream.
   */
  public void composeStartingElement(String aNamespacePrefix,
    String anElementName, List someAttributes, boolean isEmptyElement,
    OutputStream aStream) throws IOException {
    StringBuffer aBuffer = new StringBuffer();
    composeStartingElement(aNamespacePrefix, anElementName, someAttributes,
      isEmptyElement, aBuffer, 0);
    aStream.write(aBuffer.toString().getBytes(_theStreamEncoding));
  }

  /**
   * Builds and returns a string of an XML starting element with the arguments passed in without
   * any attributes and appends the string to the buffer.
   *
   * @param aNamespacePrefix The namespace prefix of the element.
   * @param anElementName The name of the element to create.
   * @param isEmptyElement Tells wether to create an empty element or not.
   * @param aBuffer The string buffer to which add the created string.
   */
  public void composeStartingElement(String aNamespacePrefix,
    String anElementName, boolean isEmptyElement, StringBuffer aBuffer) {
    composeStartingElement(aNamespacePrefix, anElementName, EMPTY_LIST,
      isEmptyElement, aBuffer, 0);
  }

  /**
   * Builds and returns a string of an XML starting element with the arguments passed in without
   * any attributes and appends the string to the output stream.
   *
   * @param aNamespacePrefix The namespace prefix of the element.
   * @param anElementName The name of the element to create.
   * @param isEmptyElement Tells wether to create an empty element or not.
   * @param aStream The output stream to which add the created string.
   * @exception IOException If an error occurs while writing to the output stream.
   */
  public void composeStartingElement(String aNamespacePrefix,
    String anElementName, boolean isEmptyElement, OutputStream aStream)
    throws IOException {
    StringBuffer aBuffer = new StringBuffer();
    composeStartingElement(aNamespacePrefix, anElementName, EMPTY_LIST,
      isEmptyElement, aBuffer, 0);
    aStream.write(aBuffer.toString().getBytes(_theStreamEncoding));
  }

  /**
   * Builds an XML ending element with the arguments passed in and appends the string to the buffer.
   *
   * @param aNamespacePrefix The namespace prefix of the element.
   * @param anElementName The name of the element to create.
   * @param aBuffer The string buffer to which add the created string.
   * @param aNestedLevel The nested level of the xml element to close (for indentation).
   */
  public void composeEndingElement(String aNamespacePrefix, String anElementName, StringBuffer aBuffer, int aNestedLevel) {
    if (_isIndentingOuput) {
      aBuffer.append("\n");
      for (int i = 0; i < aNestedLevel; i++) {
        aBuffer.append("  ");
      }
    }
    
    aBuffer.append("</");
    composeQualifiedName(aNamespacePrefix, anElementName, aBuffer);
    aBuffer.append(">");
  }

  /**
   * Builds an XML ending element with the arguments passed in and appends the string to the buffer.
   *
   * @param aNamespacePrefix The namespace prefix of the element.
   * @param anElementName The name of the element to create.
   * @param aStream The output stream to which add the created string.
   * @exception IOException If an error occurs while writing ti the output stream.
   */
  public void composeEndingElement(String aNamespacePrefix,
    String anElementName, OutputStream aStream) throws IOException {
    StringBuffer aBuffer = new StringBuffer();
    composeEndingElement(aNamespacePrefix, anElementName, aBuffer, 0);
    aStream.write(aBuffer.toString().getBytes(_theStreamEncoding));
  }

  /**
   * Builds a CData section with the content passed in.
   *
   * @param aContent the content of the CData.
   * @param aBuffer The buffer into which to add the generated CData.
   */
  public void composeCData(String aContent, StringBuffer aBuffer) {
    aBuffer.append("<![CDATA[").append(aContent).append("]]>");
  }

  /**
   * Builds a CData section with the content passed in and add it to the output stream.
   *
   * @param aContent the content of the CData.
   * @param aStream The output stream into which to add the generated CData.
   * @exception IOException If an error occurs while writing to the output stream.
   */
  public void composeCData(String aContent, OutputStream aStream)
    throws IOException {
    StringBuffer aBuffer = new StringBuffer();
    composeCData(aContent, aBuffer);
    aStream.write(aBuffer.toString().getBytes(_theStreamEncoding));
  }

  /**
   * Parses the passed in string and replaces the predefined entity of the XML spec.
   *
   * @param aString The string to encode.
   * @return The XML encoded string.
   */
  public String xmlEncode(String aString) {
    StringBuffer anXmlString = new StringBuffer();
    xmlEncode(aString, anXmlString);

    return anXmlString.toString();
  }

  /**
   * Parses the passed in string and replaces the predefined entity of the XML spec.
   *
   * @param aString The string to encode.
   * @param aStream The output stream into which add the XML encoded string.
   * @exception IOException If an error occurs while writing to the output stream.
   */
  public void xmlEncode(String aString, OutputStream aStream)
    throws IOException {
    StringBuffer aBuffer = new StringBuffer();
    xmlEncode(aString, aBuffer);
    aStream.write(aBuffer.toString().getBytes(_theStreamEncoding));
  }

  /**
   * Parses the passed in string and replaces the predefined entity of the XML spec.
   *
   * @param aString The string to encode.
   * @param anXmlString The string buffer into which to add the XML encoded string.
   */
  public void xmlEncode(String aString, StringBuffer anXmlString) {
    for (int i = 0; i < aString.length(); ++i) {
      int aChar = (int) aString.charAt(i);

      if (((aChar & 0xfc00) == 0xd800) && ((i + 1) < aString.length())) {
        int lowch = (int) aString.charAt(i + 1);

        if ((lowch & 0xfc00) == 0xdc00) {
          aChar = (0x10000 + ((aChar - 0xd800) << 10) + lowch) - 0xdc00;
          i++;
        }
      }

      String anEntityRef = getEntityReference(aChar);

      if (anEntityRef != null) {
        anXmlString.append('&').append(anEntityRef).append(';');
      } else if (((aChar >= ' ') && (aChar <= 0xF7)) || (aChar == '\n') ||
            (aChar == '\r') || (aChar == '\t')) {
        // If the character is not printable, print as character reference.
        // Non printables are below ASCII space but not tab or line
        // terminator, ASCII delete, or above a certain Unicode threshold.
        if (aChar < 0x10000) {
          anXmlString.append((char) aChar);
        } else {
          anXmlString.append((char) (((aChar - 0x10000) >> 10) + 0xd800));
          anXmlString.append((char) (((aChar - 0x10000) & 0x3ff) + 0xdc00));
        }
      } else {
        anXmlString.append("&#x").append(Integer.toHexString(aChar)).append(';');
      }
    }
  }

  /**
   * Builds a qualified name of the name passed in with the namspace prefix.
   *
   * @param aNamespacePrefix The namespace prefix of the qualification
   * @param aName The name to qualify.
   * @param aBuffer The buffer into which to add the generated name.
   */
  protected void composeQualifiedName(String aNamespacePrefix, String aName,
    StringBuffer aBuffer) {
    if ((aNamespacePrefix != null) && (aNamespacePrefix.length() > 0)) {
      aBuffer.append(aNamespacePrefix).append(":");
    }

    aBuffer.append(aName);
  }

  /**
   * Returns the entity reference defined in the XML specification for the character passed in,
   * or null if the character passed in is not one of the five defined characters (<, >, ", ', &)
   * in the XML spec.
   *
   * @param aChar The character for which to return the entity
   * @return The entity reference associated or null.
   */
  protected String getEntityReference(int aChar) {
    switch (aChar) {
    case '<':
      return "lt";

    case '>':
      return "gt";

    case '"':
      return "quot";

    case '\'':
      return "apos";

    case '&':
      return "amp";
    }

    return null;
  }
}
