package org.sapia.util.xml.idefix.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.sapia.util.xml.Attribute;
import org.sapia.util.xml.idefix.XmlScribe;

/**
 *
 *
 * @author Jean-Cedric Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class XmlScribeTest extends TestCase {

  private XmlScribe _theScribe; 

  public static void main(String[] args) {
    TestRunner.run(XmlScribeTest.class);
  }

  public XmlScribeTest(String aName) {
    super(aName);
    _theScribe = new XmlScribe("UTF-8");
  }

  public void testInvalidStreamEncoding() throws Exception {
    try {
      _theScribe.setStreamEncoding("UTF-256");
      _theScribe.composeXmlDeclaration(null, new ByteArrayOutputStream());
      fail("XMLBuffer should not encode using the invalid UTF-256 encoding!");
    } catch (IOException ioe) {
      // success
    } finally {
      _theScribe.setStreamEncoding("UTF-8");
    }
  }


  public void testXmlDeclaration_Buffer() throws Exception {
    StringBuffer aBuffer = new StringBuffer();
    _theScribe.composeXmlDeclaration(null, aBuffer);
    assertEquals("<?xml version=\"1.0\" ?>\n", aBuffer.toString());

    aBuffer = new StringBuffer();
    _theScribe.composeXmlDeclaration("", aBuffer);
    assertEquals("<?xml version=\"1.0\" ?>\n", aBuffer.toString());

    aBuffer = new StringBuffer();
    _theScribe.composeXmlDeclaration("UTF-8", aBuffer);
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n", aBuffer.toString());
  }


  public void testXmlDeclaration_Streaming() throws Exception {
    ByteArrayOutputStream aStream = new ByteArrayOutputStream();
    _theScribe.composeXmlDeclaration(null, aStream);
    assertEquals("<?xml version=\"1.0\" ?>\n", new String(aStream.toByteArray(), "UTF-8"));

    aStream = new ByteArrayOutputStream();
    _theScribe.composeXmlDeclaration("", aStream);
    assertEquals("<?xml version=\"1.0\" ?>\n", new String(aStream.toByteArray(), "UTF-8"));

    aStream = new ByteArrayOutputStream();
    _theScribe.composeXmlDeclaration("UTF-8", aStream);
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n", new String(aStream.toByteArray(), "UTF-8"));
  }


  public void testUnqualifiedEmptyElement_Buffer() throws Exception {
    StringBuffer aBuffer = new StringBuffer();
    _theScribe.composeStartingElement(null, "foo", true, aBuffer);
    assertEquals("<foo />", aBuffer.toString());

    ArrayList aList = new ArrayList(1);
    aList.add(new Attribute("bar", "value"));
    aBuffer = new StringBuffer();
    _theScribe.composeStartingElement(null, "foo", aList, true, aBuffer, 0);
    assertEquals("<foo bar=\"value\" />", aBuffer.toString());
  }


  public void testUnqualifiedEmptyElement_Streaming() throws Exception {
    ByteArrayOutputStream aStream = new ByteArrayOutputStream();
    _theScribe.composeStartingElement(null, "foo", true, aStream);
    assertEquals("<foo />", new String(aStream.toByteArray(), "UTF-8"));

    ArrayList aList = new ArrayList(1);
    aList.add(new Attribute("bar", "value"));
    aStream = new ByteArrayOutputStream();
    _theScribe.composeStartingElement(null, "foo", aList, true, aStream);
    assertEquals("<foo bar=\"value\" />", new String(aStream.toByteArray(), "UTF-8"));
  }


  public void testQualifiedEmptyElement_Buffer() throws Exception {
    StringBuffer aBuffer = new StringBuffer();
    _theScribe.composeStartingElement("A", "foo", true, aBuffer);
    assertEquals("<A:foo />", aBuffer.toString());

    ArrayList aList = new ArrayList(1);
    aList.add(new Attribute("bar", "value"));
    aBuffer = new StringBuffer();
    _theScribe.composeStartingElement("B", "foo", aList, true, aBuffer, 0);
    assertEquals("<B:foo bar=\"value\" />", aBuffer.toString());
  }


  public void testQualifiedEmptyElement_Streaming() throws Exception {
    ByteArrayOutputStream aStream = new ByteArrayOutputStream();
    _theScribe.composeStartingElement("A", "foo", true, aStream);
    assertEquals("<A:foo />", new String(aStream.toByteArray(), "UTF-8"));

    ArrayList aList = new ArrayList(1);
    aList.add(new Attribute("bar", "value"));
    aStream = new ByteArrayOutputStream();
    _theScribe.composeStartingElement("B", "foo", aList, true, aStream);
    assertEquals("<B:foo bar=\"value\" />", new String(aStream.toByteArray(), "UTF-8"));
  }


  public void testUnqualifiedStartingElement_Buffer() throws Exception {
    StringBuffer aBuffer = new StringBuffer();
    _theScribe.composeStartingElement(null, "foo", false, aBuffer);
    assertEquals("<foo>", aBuffer.toString());

    ArrayList aList = new ArrayList(1);
    aList.add(new Attribute("bar", "value"));
    aBuffer = new StringBuffer();
    _theScribe.composeStartingElement(null, "foo", aList, false, aBuffer, 0);
    assertEquals("<foo bar=\"value\">", aBuffer.toString());
  }


  public void testUnqualifiedStartingElement_Streaming() throws Exception {
    ByteArrayOutputStream aStream = new ByteArrayOutputStream();
    _theScribe.composeStartingElement(null, "foo", false, aStream);
    assertEquals("<foo>", new String(aStream.toByteArray(), "UTF-8"));

    ArrayList aList = new ArrayList(1);
    aList.add(new Attribute("bar", "value"));
    aStream = new ByteArrayOutputStream();
    _theScribe.composeStartingElement(null, "foo", aList, false, aStream);
    assertEquals("<foo bar=\"value\">", new String(aStream.toByteArray(), "UTF-8"));
  }


  public void testQualifiedStartingElement_Buffer() throws Exception {
    StringBuffer aBuffer = new StringBuffer();
    _theScribe.composeStartingElement("A", "foo", false, aBuffer);
    assertEquals("<A:foo>", aBuffer.toString());

    ArrayList aList = new ArrayList(1);
    aList.add(new Attribute("bar", "value"));
    aBuffer = new StringBuffer();
    _theScribe.composeStartingElement("B", "foo", aList, false, aBuffer, 0);
    assertEquals("<B:foo bar=\"value\">", aBuffer.toString());
  }


  public void testQualifiedStartingElement_Streaming() throws Exception {
    ByteArrayOutputStream aStream = new ByteArrayOutputStream();
    _theScribe.composeStartingElement("A", "foo", false, aStream);
    assertEquals("<A:foo>", new String(aStream.toByteArray(), "UTF-8"));

    ArrayList aList = new ArrayList(1);
    aList.add(new Attribute("bar", "value"));
    aStream = new ByteArrayOutputStream();
    _theScribe.composeStartingElement("B", "foo", aList, false, aStream);
    assertEquals("<B:foo bar=\"value\">", new String(aStream.toByteArray(), "UTF-8"));
  }


  public void testUnqualifiedEndingElement_Buffer() throws Exception {
    StringBuffer aBuffer = new StringBuffer();
    _theScribe.composeEndingElement("", "foo", aBuffer, 0);
    assertEquals("</foo>", aBuffer.toString());

    aBuffer = new StringBuffer();
    _theScribe.composeEndingElement(null, "foo", aBuffer, 0);
    assertEquals("</foo>", aBuffer.toString());
  }


  public void testUnqualifiedEndingElement_Streaming() throws Exception {
    ByteArrayOutputStream aStream = new ByteArrayOutputStream();
    _theScribe.composeEndingElement("", "foo", aStream);
    assertEquals("</foo>", new String(aStream.toByteArray(), "UTF-8"));

    aStream = new ByteArrayOutputStream();
    _theScribe.composeEndingElement(null, "foo", aStream);
    assertEquals("</foo>", new String(aStream.toByteArray(), "UTF-8"));
  }


  public void testQualifiedEndingElement_Buffer() throws Exception {
    StringBuffer aBuffer = new StringBuffer();
    _theScribe.composeEndingElement("A", "foo", aBuffer, 0);
    assertEquals("</A:foo>", aBuffer.toString());
  }


  public void testQualifiedEndingElement_Streaming() throws Exception {
    ByteArrayOutputStream aStream = new ByteArrayOutputStream();
    _theScribe.composeEndingElement("A", "foo", aStream);
    assertEquals("</A:foo>", new String(aStream.toByteArray(), "UTF-8"));
  }


  public void testQualifiedAttribute_Buffer() throws Exception {
    ArrayList aList = new ArrayList(1);
    aList.add(new Attribute("Z", "bar","value"));
    StringBuffer aBuffer = new StringBuffer();
    _theScribe.composeStartingElement("C", "foo", aList, true, aBuffer, 0);
    assertEquals("<C:foo Z:bar=\"value\" />", aBuffer.toString());
  }


  public void testQualifiedAttribute_Streaming() throws Exception {
    ArrayList aList = new ArrayList(1);
    aList.add(new Attribute("Z", "bar","value"));
    ByteArrayOutputStream aStream = new ByteArrayOutputStream();
    _theScribe.composeStartingElement("C", "foo", aList, true, aStream);
    assertEquals("<C:foo Z:bar=\"value\" />", new String(aStream.toByteArray(), "UTF-8"));
  }


  public void testXMLReservedCharacters_Buffer() throws Exception {
    ArrayList aList = new ArrayList(1);
    aList.add(new Attribute("bar","<\"value\" & \'value\'>"));
    StringBuffer aBuffer = new StringBuffer();
    _theScribe.composeStartingElement("C", "foo", aList, true, aBuffer, 0);
    assertEquals("<C:foo bar=\"&lt;&quot;value&quot; &amp; &apos;value&apos;&gt;\" />", aBuffer.toString());
  }


  public void testXMLReservedCharacters_Streaming() throws Exception {
    ArrayList aList = new ArrayList(1);
    aList.add(new Attribute("bar","<\"value\" & \'value\'>"));
    ByteArrayOutputStream aStream = new ByteArrayOutputStream();
    _theScribe.composeStartingElement("C", "foo", aList, true, aStream);
    assertEquals("<C:foo bar=\"&lt;&quot;value&quot; &amp; &apos;value&apos;&gt;\" />", new String(aStream.toByteArray(), "UTF-8"));
  }
}