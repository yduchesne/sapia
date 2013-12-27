package org.sapia.util.xml.idefix.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.sapia.util.xml.CData;
import org.sapia.util.xml.idefix.XmlBuffer;

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
public class XmlBufferTest extends TestCase {

  public static void main(String[] args) {
    TestRunner.run(XmlBufferTest.class);
  }

  public XmlBufferTest(String aName) {
    super(aName);
  }

  public void testClosingInvalidElement() throws Exception {
    String aNamespaceURI = "http://schemas.sapia.org/idefix";
    String anotherNamespaceURI = "http://schemas.sapia.org/another";

    XmlBuffer aBuffer = new XmlBuffer();
    aBuffer.startElement("A").endElement("A");

    /////////////////////////////////////////////////////////////////////////////
    try {
      aBuffer = new XmlBuffer();
      aBuffer.startElement("A").endElement("B");
      fail();
    } catch (IllegalArgumentException iae) {
    }

    /////////////////////////////////////////////////////////////////////////////
    try {
      aBuffer = new XmlBuffer();
      aBuffer.addNamespace(aNamespaceURI, "P");
      aBuffer.startElement(aNamespaceURI, "A").endElement("A");
      fail();
    } catch (IllegalArgumentException iae) {
    }

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer();
    aBuffer.addNamespace(aNamespaceURI, "P");
    aBuffer.startElement(aNamespaceURI, "A").endElement(aNamespaceURI, "A");

    /////////////////////////////////////////////////////////////////////////////
    try {
      aBuffer = new XmlBuffer();
      aBuffer.addNamespace(aNamespaceURI, "P");
      aBuffer.startElement(aNamespaceURI, "A").endElement("B");
      fail();
    } catch (IllegalArgumentException iae) {
    }

    /////////////////////////////////////////////////////////////////////////////
    try {
      aBuffer = new XmlBuffer();
      aBuffer.addNamespace(aNamespaceURI, "P");
      aBuffer.startElement(aNamespaceURI, "A").endElement(aNamespaceURI, "B");
      fail();
    } catch (IllegalArgumentException iae) {
    }

    /////////////////////////////////////////////////////////////////////////////
    try {
      aBuffer = new XmlBuffer();
      aBuffer.addNamespace(aNamespaceURI, "P");
      aBuffer.startElement(aNamespaceURI, "A").endElement(anotherNamespaceURI, "B");
      fail();
    } catch (IllegalArgumentException iae) {
    }

    /////////////////////////////////////////////////////////////////////////////
    try {
      aBuffer = new XmlBuffer();
      aBuffer.addNamespace(aNamespaceURI, "P");
      aBuffer.addNamespace(anotherNamespaceURI, "Q");
      aBuffer.startElement(aNamespaceURI, "A").endElement(anotherNamespaceURI, "B");
      fail();
    } catch (IllegalArgumentException iae) {
    }

    /////////////////////////////////////////////////////////////////////////////
    try {
      aBuffer = new XmlBuffer();
      aBuffer.addNamespace(aNamespaceURI, "P");
      aBuffer.addNamespace(anotherNamespaceURI, "Q");
      aBuffer.startElement(aNamespaceURI, "A").endElement(anotherNamespaceURI, "A");
      fail();
    } catch (IllegalArgumentException iae) {
    }
  }

  public void testEmptyElementNoNSWithoutAttribute() throws Exception {
    XmlBuffer aBuffer = new XmlBuffer();
    aBuffer.startElement("foo").endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo />", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    XmlBuffer anotherBuffer = new XmlBuffer(true);
    anotherBuffer.startElement("foo").endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<foo />", anotherBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    anotherBuffer = new XmlBuffer("UTF-16");
    anotherBuffer.startElement("foo").endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<?xml version=\"1.0\" encoding=\"UTF-16\" ?>\n<foo />", anotherBuffer.toString());
  }

  public void testInvalidEndAttribute() throws Exception {
    try {
      XmlBuffer aBuffer = new XmlBuffer(false);
      aBuffer.endAttribute();
      fail();
    } catch (IllegalStateException ise) {
    }

    /////////////////////////////////////////////////////////////////////////////
    try {
      XmlBuffer aBuffer = new XmlBuffer(false);
      aBuffer.startElement("foo").
              endElement("foo").
              endAttribute();
      fail();
    } catch (IllegalStateException ise) {
    }

    /////////////////////////////////////////////////////////////////////////////
    try {
      XmlBuffer aBuffer = new XmlBuffer(false);
      aBuffer.startElement("foo").
              addAttribute("bar", "bar").
              endAttribute().
              addAttribute("bar2", "bar").
              endElement("foo");
      fail();
    } catch (IllegalStateException ise) {
    }
  }

  public void testEmptyElementNoNSWithAttribute() throws Exception {
    XmlBuffer aBuffer = new XmlBuffer(false);
    aBuffer.startElement("foo").
            addAttribute("bar1", "value1").
            addAttribute("bar2", "value2").
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo bar1=\"value1\" bar2=\"value2\" />", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer(false);
    aBuffer.startElement("foo").
            addAttribute("bar1", "value1").
            addAttribute("bar2", "value2").
            endAttribute().
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo bar1=\"value1\" bar2=\"value2\" />", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    XmlBuffer anotherBuffer = new XmlBuffer(true);
    anotherBuffer.startElement("foo").
            addAttribute("bar1", "value1").
            addAttribute("bar2", "value2").
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                 "<foo bar1=\"value1\" bar2=\"value2\" />", anotherBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    anotherBuffer = new XmlBuffer(true);
    anotherBuffer.startElement("foo").
            addAttribute("bar1", "value1").
            addAttribute("bar2", "value2").
            endAttribute().
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                 "<foo bar1=\"value1\" bar2=\"value2\" />", anotherBuffer.toString());
  }


  public void testEmptyElementWithNSWithoutAttribute() throws Exception {
    XmlBuffer aBuffer = new XmlBuffer(false);
    aBuffer.addNamespace("http://schemas.sapia.org/idefix", "IDEFIX").
            startElement("http://schemas.sapia.org/idefix", "foo").
            endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<IDEFIX:foo xmlns:IDEFIX=\"http://schemas.sapia.org/idefix\" />", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer(false);
    aBuffer.addNamespace("http://schemas.sapia.org/idefix", "IDEFIX").
            startElement("http://schemas.sapia.org/idefix", "foo").
            endAttribute().
            endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<IDEFIX:foo xmlns:IDEFIX=\"http://schemas.sapia.org/idefix\" />", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    XmlBuffer anotherBuffer = new XmlBuffer(true);
    anotherBuffer.addNamespace("http://schemas.sapia.org/idefix", "IDEFIX").
            startElement("http://schemas.sapia.org/idefix", "foo").
            endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                 "<IDEFIX:foo xmlns:IDEFIX=\"http://schemas.sapia.org/idefix\" />", anotherBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    anotherBuffer = new XmlBuffer(true);
    anotherBuffer.addNamespace("http://schemas.sapia.org/idefix", "IDEFIX").
            startElement("http://schemas.sapia.org/idefix", "foo").
            endAttribute().
            endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                 "<IDEFIX:foo xmlns:IDEFIX=\"http://schemas.sapia.org/idefix\" />", anotherBuffer.toString());
  }


  public void testEmptyElementWithNSWithAttribute() throws Exception {
    XmlBuffer aBuffer = new XmlBuffer(false);
    aBuffer.addNamespace("http://schemas.sapia.org/idefix", "IDEFIX").
            addNamespace("http://another.namespace.uri/m", "M").
            startElement("http://schemas.sapia.org/idefix", "foo").
            addAttribute("bar1", "value1").
            addAttribute("http://schemas.sapia.org/idefix", "bar2", "value2").
            addAttribute("http://another.namespace.uri/m", "bar3", "value3").
            endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<IDEFIX:foo xmlns:IDEFIX=\"http://schemas.sapia.org/idefix\"" +
                 " xmlns:M=\"http://another.namespace.uri/m\" bar1=\"value1\""+
                 " IDEFIX:bar2=\"value2\" M:bar3=\"value3\" />", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer(false);
    aBuffer.addNamespace("http://schemas.sapia.org/idefix", "IDEFIX").
            addNamespace("http://another.namespace.uri/m", "M").
            startElement("http://schemas.sapia.org/idefix", "foo").
            addAttribute("bar1", "value1").
            addAttribute("http://schemas.sapia.org/idefix", "bar2", "value2").
            addAttribute("http://another.namespace.uri/m", "bar3", "value3").
            endAttribute().
            endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<IDEFIX:foo xmlns:IDEFIX=\"http://schemas.sapia.org/idefix\"" +
                 " xmlns:M=\"http://another.namespace.uri/m\" bar1=\"value1\""+
                 " IDEFIX:bar2=\"value2\" M:bar3=\"value3\" />", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    XmlBuffer anotherBuffer = new XmlBuffer(true);
    anotherBuffer.addNamespace("http://schemas.sapia.org/idefix", "IDEFIX").
            addNamespace("http://another.namespace.uri/m", "M").
            startElement("http://schemas.sapia.org/idefix", "foo").
            addAttribute("bar1", "value1").
            addAttribute("http://schemas.sapia.org/idefix", "bar2", "value2").
            addAttribute("http://another.namespace.uri/m", "bar3", "value3").
            endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                 "<IDEFIX:foo xmlns:IDEFIX=\"http://schemas.sapia.org/idefix\"" +
                 " xmlns:M=\"http://another.namespace.uri/m\" bar1=\"value1\""+
                 " IDEFIX:bar2=\"value2\" M:bar3=\"value3\" />", anotherBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    anotherBuffer = new XmlBuffer(true);
    anotherBuffer.addNamespace("http://schemas.sapia.org/idefix", "IDEFIX").
            addNamespace("http://another.namespace.uri/m", "M").
            startElement("http://schemas.sapia.org/idefix", "foo").
            addAttribute("bar1", "value1").
            addAttribute("http://schemas.sapia.org/idefix", "bar2", "value2").
            addAttribute("http://another.namespace.uri/m", "bar3", "value3").
            endAttribute().
            endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                 "<IDEFIX:foo xmlns:IDEFIX=\"http://schemas.sapia.org/idefix\"" +
                 " xmlns:M=\"http://another.namespace.uri/m\" bar1=\"value1\""+
                 " IDEFIX:bar2=\"value2\" M:bar3=\"value3\" />", anotherBuffer.toString());
  }


  public void testElementsWithDefaultNSWithAttribute() throws Exception {
    XmlBuffer aBuffer = new XmlBuffer(false);
    aBuffer.addNamespace("http://schemas.sapia.org/idefix", "").
            addNamespace("http://another.namespace.uri/m", "M").
            startElement("http://schemas.sapia.org/idefix", "foo").
            addAttribute("bar1", "value1").
            addAttribute("http://schemas.sapia.org/idefix", "bar2", "value2").
            addAttribute("http://another.namespace.uri/m", "bar3", "value3").
            endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo xmlns=\"http://schemas.sapia.org/idefix\"" +
                 " xmlns:M=\"http://another.namespace.uri/m\" bar1=\"value1\""+
                 " bar2=\"value2\" M:bar3=\"value3\" />", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer(false);
    aBuffer.addNamespace("http://schemas.sapia.org/idefix", "").
            addNamespace("http://another.namespace.uri/m", "M").
            startElement("http://schemas.sapia.org/idefix", "foo").
            addAttribute("bar1", "value1").
            addAttribute("http://schemas.sapia.org/idefix", "bar2", "value2").
            addAttribute("http://another.namespace.uri/m", "bar3", "value3").
            endAttribute().
            endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo xmlns=\"http://schemas.sapia.org/idefix\"" +
                 " xmlns:M=\"http://another.namespace.uri/m\" bar1=\"value1\""+
                 " bar2=\"value2\" M:bar3=\"value3\" />", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    XmlBuffer anotherBuffer = new XmlBuffer(true);
    anotherBuffer.addNamespace("http://schemas.sapia.org/idefix", "").
            addNamespace("http://another.namespace.uri/m", "M").
            startElement("http://schemas.sapia.org/idefix", "foo").
            addAttribute("bar1", "value1").
            addContent("foobar").
            addAttribute("http://schemas.sapia.org/idefix", "bar2", "value2").
            addAttribute("http://another.namespace.uri/m", "bar3", "value3").
            endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                 "<foo xmlns=\"http://schemas.sapia.org/idefix\"" +
                 " xmlns:M=\"http://another.namespace.uri/m\" bar1=\"value1\""+
                 " bar2=\"value2\" M:bar3=\"value3\">foobar</foo>", anotherBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    anotherBuffer = new XmlBuffer(true);
    anotherBuffer.addNamespace("http://schemas.sapia.org/idefix", "").
            addNamespace("http://another.namespace.uri/m", "M").
            startElement("http://schemas.sapia.org/idefix", "foo").
            addAttribute("bar1", "value1").
            addContent("foobar").
            addAttribute("http://schemas.sapia.org/idefix", "bar2", "value2").
            addAttribute("http://another.namespace.uri/m", "bar3", "value3").
            endAttribute().
            endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                 "<foo xmlns=\"http://schemas.sapia.org/idefix\"" +
                 " xmlns:M=\"http://another.namespace.uri/m\" bar1=\"value1\""+
                 " bar2=\"value2\" M:bar3=\"value3\">foobar</foo>", anotherBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    XmlBuffer yetAnotherBuffer = new XmlBuffer(true);
    yetAnotherBuffer.addNamespace("http://schemas.sapia.org/idefix", "").
            addNamespace("http://another.namespace.uri/m", "M").
            startElement("http://schemas.sapia.org/idefix", "foo").
            addAttribute("bar1", "value1").
            addAttribute("http://schemas.sapia.org/idefix", "bar2", "value2").
            addAttribute("http://another.namespace.uri/m", "bar3", "value3").
            addContent("foobar").
            endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                 "<foo xmlns=\"http://schemas.sapia.org/idefix\"" +
                 " xmlns:M=\"http://another.namespace.uri/m\" bar1=\"value1\""+
                 " bar2=\"value2\" M:bar3=\"value3\">foobar</foo>", yetAnotherBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    yetAnotherBuffer = new XmlBuffer(true);
    yetAnotherBuffer.addNamespace("http://schemas.sapia.org/idefix", "").
            addNamespace("http://another.namespace.uri/m", "M").
            startElement("http://schemas.sapia.org/idefix", "foo").
            addAttribute("bar1", "value1").
            addAttribute("http://schemas.sapia.org/idefix", "bar2", "value2").
            addAttribute("http://another.namespace.uri/m", "bar3", "value3").
            endAttribute().
            addContent("foobar").
            endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                 "<foo xmlns=\"http://schemas.sapia.org/idefix\"" +
                 " xmlns:M=\"http://another.namespace.uri/m\" bar1=\"value1\""+
                 " bar2=\"value2\" M:bar3=\"value3\">foobar</foo>", yetAnotherBuffer.toString());
  }


  public void testSimpleElementNoNSWithoutAttribute() throws Exception {
    XmlBuffer aBuffer = new XmlBuffer();
    aBuffer.startElement("foo").addContent("bar").endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo>bar</foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer();
    aBuffer.startElement("foo").endAttribute().addContent("bar").endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo>bar</foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    XmlBuffer anotherBuffer = new XmlBuffer(true);
    anotherBuffer.startElement("foo").addContent("bar").endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                 "<foo>bar</foo>", anotherBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    anotherBuffer = new XmlBuffer(true);
    anotherBuffer.startElement("foo").addContent("bar").endAttribute().endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                 "<foo>bar</foo>", anotherBuffer.toString());
  }


  public void testSimpleElementNoNSWithAttribute() throws Exception {
    XmlBuffer aBuffer = new XmlBuffer(false);
    aBuffer.startElement("foo").
            addAttribute("bar1", "value1").
            addContent("foobar").
            addAttribute("bar2", "value2").
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo bar1=\"value1\" bar2=\"value2\">foobar</foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer(false);
    aBuffer.startElement("foo").
            addAttribute("bar1", "value1").
            addContent("foobar").
            addAttribute("bar2", "value2").
            endAttribute().
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo bar1=\"value1\" bar2=\"value2\">foobar</foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    XmlBuffer anotherBuffer = new XmlBuffer(true);
    anotherBuffer.startElement("foo").
            addContent("foobar").
            addAttribute("bar1", "value1").
            addAttribute("bar2", "value2").
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                 "<foo bar1=\"value1\" bar2=\"value2\">foobar</foo>", anotherBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    anotherBuffer = new XmlBuffer(true);
    anotherBuffer.startElement("foo").
            addAttribute("bar1", "value1").
            addAttribute("bar2", "value2").
            endAttribute().
            addContent("foobar").
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                 "<foo bar1=\"value1\" bar2=\"value2\">foobar</foo>", anotherBuffer.toString());
  }

  public void testSimpleElementWithNSWithAttribute() throws Exception {
    XmlBuffer aBuffer = new XmlBuffer(false);
    aBuffer.addNamespace("http://schemas.sapia.org/idefix", "IDEFIX").
            addNamespace("http://another.namespace.uri/m", "M").
            startElement("http://schemas.sapia.org/idefix", "foo").
            addAttribute("bar1", "value1").
            addAttribute("http://schemas.sapia.org/idefix", "bar2", "value2").
            addAttribute("http://another.namespace.uri/m", "bar3", "value3").
            addContent("foobar").
            endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<IDEFIX:foo xmlns:IDEFIX=\"http://schemas.sapia.org/idefix\"" +
                 " xmlns:M=\"http://another.namespace.uri/m\" bar1=\"value1\""+
                 " IDEFIX:bar2=\"value2\" M:bar3=\"value3\">foobar</IDEFIX:foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer(false);
    aBuffer.addNamespace("http://schemas.sapia.org/idefix", "IDEFIX").
            addNamespace("http://another.namespace.uri/m", "M").
            startElement("http://schemas.sapia.org/idefix", "foo").
            addAttribute("bar1", "value1").
            addAttribute("http://schemas.sapia.org/idefix", "bar2", "value2").
            addAttribute("http://another.namespace.uri/m", "bar3", "value3").
            endAttribute().
            addContent("foobar").
            endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<IDEFIX:foo xmlns:IDEFIX=\"http://schemas.sapia.org/idefix\"" +
                 " xmlns:M=\"http://another.namespace.uri/m\" bar1=\"value1\""+
                 " IDEFIX:bar2=\"value2\" M:bar3=\"value3\">foobar</IDEFIX:foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    XmlBuffer anotherBuffer = new XmlBuffer(true);
    anotherBuffer.addNamespace("http://schemas.sapia.org/idefix", "IDEFIX").
            addNamespace("http://another.namespace.uri/m", "M").
            startElement("http://schemas.sapia.org/idefix", "foo").
            addContent("foobar").
            addAttribute("bar1", "value1").
            addAttribute("http://schemas.sapia.org/idefix", "bar2", "value2").
            addAttribute("http://another.namespace.uri/m", "bar3", "value3").
            endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                 "<IDEFIX:foo xmlns:IDEFIX=\"http://schemas.sapia.org/idefix\"" +
                 " xmlns:M=\"http://another.namespace.uri/m\" bar1=\"value1\""+
                 " IDEFIX:bar2=\"value2\" M:bar3=\"value3\">foobar</IDEFIX:foo>", anotherBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    anotherBuffer = new XmlBuffer(true);
    anotherBuffer.addNamespace("http://schemas.sapia.org/idefix", "IDEFIX").
            addNamespace("http://another.namespace.uri/m", "M").
            startElement("http://schemas.sapia.org/idefix", "foo").
            addContent("foobar").
            addAttribute("bar1", "value1").
            addAttribute("http://schemas.sapia.org/idefix", "bar2", "value2").
            addAttribute("http://another.namespace.uri/m", "bar3", "value3").
            endAttribute().
            endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                 "<IDEFIX:foo xmlns:IDEFIX=\"http://schemas.sapia.org/idefix\"" +
                 " xmlns:M=\"http://another.namespace.uri/m\" bar1=\"value1\""+
                 " IDEFIX:bar2=\"value2\" M:bar3=\"value3\">foobar</IDEFIX:foo>", anotherBuffer.toString());
  }

  public void testNestedElementNoNSNoAttribute() throws Exception {
    XmlBuffer aBuffer = new XmlBuffer(false);
    aBuffer.startElement("foo").
            startElement("bar").
            endElement("bar").
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo><bar /></foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer(false);
    aBuffer.startElement("foo").
            endAttribute().  
            startElement("bar").
            endAttribute().  
            endElement("bar").
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo><bar /></foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    XmlBuffer anotherBuffer = new XmlBuffer(false);
    anotherBuffer.startElement("foo").
            startElement("bar").
            startElement("too").
            endElement("too").
            endElement("bar").
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo><bar><too /></bar></foo>", anotherBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    anotherBuffer = new XmlBuffer(false);
    anotherBuffer.
            startElement("foo").
            startElement("bar").
            startElement("too").
            endAttribute().
            endElement("too").
            endElement("bar").
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo><bar><too /></bar></foo>", anotherBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    anotherBuffer = new XmlBuffer(false);
    anotherBuffer.
            startElement("foo").
            startElement("bar").
            endAttribute().
            startElement("too").
            endElement("too").
            endElement("bar").
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo><bar><too /></bar></foo>", anotherBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    anotherBuffer = new XmlBuffer(false);
    anotherBuffer.
            startElement("foo").
            startElement("bar").
            startElement("too").
            endElement("too").
            endAttribute().
            endElement("bar").
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo><bar><too /></bar></foo>", anotherBuffer.toString());
  }

  public void testNestedElementNoNSWithAttribute() throws Exception {
    XmlBuffer aBuffer = new XmlBuffer();
    aBuffer.startElement("foo").
            addAttribute("bar1", "value1").
            addAttribute("bar2", "value2").
            startElement("bar").
            endElement("bar").
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo bar1=\"value1\" bar2=\"value2\"><bar /></foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer();
    aBuffer.startElement("foo").
            addAttribute("bar1", "value1").
            addAttribute("bar2", "value2").
            endAttribute().
            startElement("bar").
            endElement("bar").
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo bar1=\"value1\" bar2=\"value2\"><bar /></foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer();
    aBuffer.startElement("foo").
            addAttribute("bar1", "value1").
            startElement("bar").
            endElement("bar").
            addAttribute("bar2", "value2").
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo bar1=\"value1\" bar2=\"value2\"><bar /></foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer();
    aBuffer.startElement("foo").
            addAttribute("bar1", "value1").
            startElement("bar").
            endElement("bar").
            addAttribute("bar2", "value2").
            endAttribute().
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo bar1=\"value1\" bar2=\"value2\"><bar /></foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer();
    aBuffer.startElement("foo").
            startElement("bar").
            endElement("bar").
            addAttribute("bar1", "value1").
            addAttribute("bar2", "value2").
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo bar1=\"value1\" bar2=\"value2\"><bar /></foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer();
    aBuffer.startElement("foo").
            startElement("bar").
            endElement("bar").
            addAttribute("bar1", "value1").
            addAttribute("bar2", "value2").
            endAttribute().
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo bar1=\"value1\" bar2=\"value2\"><bar /></foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    XmlBuffer anotherBuffer = new XmlBuffer(true);
    anotherBuffer.startElement("foo").
            addAttribute("bar1", "value1").
            startElement("bar").
            addAttribute("bar2", "value2").
            endElement("bar").
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                 "<foo bar1=\"value1\"><bar bar2=\"value2\" /></foo>", anotherBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    anotherBuffer = new XmlBuffer(true);
    anotherBuffer.startElement("foo").
            addAttribute("bar1", "value1").
            startElement("bar").
            addAttribute("bar2", "value2").
            endAttribute().
            endElement("bar").
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                 "<foo bar1=\"value1\"><bar bar2=\"value2\" /></foo>", anotherBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    anotherBuffer = new XmlBuffer(true);
    anotherBuffer.startElement("foo").
            startElement("bar").
            addAttribute("bar2", "value2").
            endElement("bar").
            addAttribute("bar1", "value1").
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                 "<foo bar1=\"value1\"><bar bar2=\"value2\" /></foo>", anotherBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    anotherBuffer = new XmlBuffer(true);
    anotherBuffer.startElement("foo").
            startElement("bar").
            addAttribute("bar2", "value2").
            endElement("bar").
            addAttribute("bar1", "value1").
            endAttribute().
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                 "<foo bar1=\"value1\"><bar bar2=\"value2\" /></foo>", anotherBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    anotherBuffer = new XmlBuffer("ISO-8859-1");
    anotherBuffer.startElement("foo").
            startElement("bar").
            addAttribute("bar1", "value1").
            addAttribute("bar2", "value2").
            endElement("bar").
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n" +
                 "<foo><bar bar1=\"value1\" bar2=\"value2\" /></foo>", anotherBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    anotherBuffer = new XmlBuffer("ISO-8859-1");
    anotherBuffer.startElement("foo").
            startElement("bar").
            addAttribute("bar1", "value1").
            addAttribute("bar2", "value2").
            endElement("bar").
            endAttribute().
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n" +
                 "<foo><bar bar1=\"value1\" bar2=\"value2\" /></foo>", anotherBuffer.toString());
  }

  public void testSimpleNestedElementNoNSWithAttribute() throws Exception {
    XmlBuffer aBuffer = new XmlBuffer();
    aBuffer.startElement("foo").
                addContent("bonjour").
                addAttribute("bar1", "value1").
                addAttribute("bar2", "value2").
                startElement("bar").
                    addContent("ola!").
                endElement("bar").
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo bar1=\"value1\" bar2=\"value2\"><bar>ola!</bar>bonjour</foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer();
    aBuffer.startElement("foo").
                addContent("bonjour").
                addAttribute("bar1", "value1").
                addAttribute("bar2", "value2").
                startElement("bar").
                    endAttribute().
                    addContent("ola!").
                endElement("bar").
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo bar1=\"value1\" bar2=\"value2\"><bar>ola!</bar>bonjour</foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer();
    aBuffer.startElement("foo").
                startElement("bar").
                    addContent("ola!").
                    addAttribute("bar2", "value2").
                endElement("bar").
                addContent("bonjour").
                addAttribute("bar1", "value1").
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo bar1=\"value1\"><bar bar2=\"value2\">ola!</bar>bonjour</foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer();
    aBuffer.startElement("foo").
                startElement("bar").
                    addContent("ola!").
                    addAttribute("bar2", "value2").
                    endAttribute().
                endElement("bar").
                addContent("bonjour").
                addAttribute("bar1", "value1").
                endAttribute().
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo bar1=\"value1\"><bar bar2=\"value2\">ola!</bar>bonjour</foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer();
    aBuffer.startElement("foo").
                startElement("bar").
                    addContent("ola!").
                    addAttribute("bar2", "value2").
                    addContent("bonjour").
                    addAttribute("bar1", "value1").
                endElement("bar").
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo><bar bar2=\"value2\" bar1=\"value1\">ola!bonjour</bar></foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer();
    aBuffer.startElement("foo").
                startElement("bar").
                    addContent("ola!").
                    addAttribute("bar2", "value2").
                    addContent("bonjour").
                    addAttribute("bar1", "value1").
                    endAttribute().
                endElement("bar").
                endAttribute().
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo><bar bar2=\"value2\" bar1=\"value1\">ola!bonjour</bar></foo>", aBuffer.toString());
  }


  public void testNestedElementWithNSNoAttributes() throws Exception {
    XmlBuffer aBuffer = new XmlBuffer().
            addNamespace("http://schemas.sapia.org/idefix", "IDEFIX").
            addNamespace("http://another.namespace.uri/m", "M");

    aBuffer.startElement("http://schemas.sapia.org/idefix", "foo").
                startElement("http://schemas.sapia.org/idefix", "bar").
                    addContent("bonjour").
                endElement("http://schemas.sapia.org/idefix", "bar").
            endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<IDEFIX:foo xmlns:IDEFIX=\"http://schemas.sapia.org/idefix\">" +
                 "<IDEFIX:bar>bonjour</IDEFIX:bar></IDEFIX:foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer().
            addNamespace("http://schemas.sapia.org/idefix", "IDEFIX").
            addNamespace("http://another.namespace.uri/m", "M");

    aBuffer.startElement("http://schemas.sapia.org/idefix", "foo").
                startElement("http://schemas.sapia.org/idefix", "bar").
                    endAttribute().
                    addContent("bonjour").
                endElement("http://schemas.sapia.org/idefix", "bar").
            endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<IDEFIX:foo xmlns:IDEFIX=\"http://schemas.sapia.org/idefix\">" +
                 "<IDEFIX:bar>bonjour</IDEFIX:bar></IDEFIX:foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer().
            addNamespace("http://schemas.sapia.org/idefix", "IDEFIX").
            addNamespace("http://another.namespace.uri/m", "M");

    aBuffer.startElement("http://schemas.sapia.org/idefix", "foo").
                startElement("http://another.namespace.uri/m", "bar").
                    addContent("bonjour").
                endElement("http://another.namespace.uri/m", "bar").
            endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<IDEFIX:foo xmlns:IDEFIX=\"http://schemas.sapia.org/idefix\">" +
                 "<M:bar xmlns:M=\"http://another.namespace.uri/m\">bonjour</M:bar></IDEFIX:foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer().
            addNamespace("http://schemas.sapia.org/idefix", "IDEFIX").
            addNamespace("http://another.namespace.uri/m", "M");

    aBuffer.startElement("http://schemas.sapia.org/idefix", "foo").
                startElement("http://another.namespace.uri/m", "bar").
                    addContent("bonjour").
                endElement("http://another.namespace.uri/m", "bar").
                endAttribute().
            endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<IDEFIX:foo xmlns:IDEFIX=\"http://schemas.sapia.org/idefix\">" +
                 "<M:bar xmlns:M=\"http://another.namespace.uri/m\">bonjour</M:bar></IDEFIX:foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer().
            addNamespace("http://schemas.sapia.org/idefix", "IDEFIX").
            addNamespace("http://another.namespace.uri/m", "M");

    aBuffer.startElement("http://schemas.sapia.org/idefix", "foo").
                startElement("http://another.namespace.uri/m", "bar").
                    addContent("bonjour").
                endElement("http://another.namespace.uri/m", "bar").
                startElement("http://another.namespace.uri/m", "too").
                    addContent("ola!").
                endElement("http://another.namespace.uri/m", "too").
            endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<IDEFIX:foo xmlns:IDEFIX=\"http://schemas.sapia.org/idefix\">" +
                 "<M:bar xmlns:M=\"http://another.namespace.uri/m\">bonjour</M:bar>" +
                 "<M:too xmlns:M=\"http://another.namespace.uri/m\">ola!</M:too></IDEFIX:foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer().
            addNamespace("http://schemas.sapia.org/idefix", "IDEFIX").
            addNamespace("http://another.namespace.uri/m", "M");

    aBuffer.startElement("http://schemas.sapia.org/idefix", "foo").
                endAttribute().
                startElement("http://another.namespace.uri/m", "bar").
                    endAttribute().
                    addContent("bonjour").
                endElement("http://another.namespace.uri/m", "bar").
                startElement("http://another.namespace.uri/m", "too").
                    endAttribute().
                    addContent("ola!").
                endElement("http://another.namespace.uri/m", "too").
            endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<IDEFIX:foo xmlns:IDEFIX=\"http://schemas.sapia.org/idefix\">" +
                 "<M:bar xmlns:M=\"http://another.namespace.uri/m\">bonjour</M:bar>" +
                 "<M:too xmlns:M=\"http://another.namespace.uri/m\">ola!</M:too></IDEFIX:foo>", aBuffer.toString());
  }


  public void testOverridingNSPrefix() throws Exception {
    /////////////////////////////////////////////////////////////////////////////
    XmlBuffer aBuffer = new XmlBuffer().
            addNamespace("http://schemas.sapia.org/idefix", "IDEFIX");

    aBuffer.startElement("http://schemas.sapia.org/idefix", "foo");
    aBuffer.addNamespace("http://schemas.sapia.org", "IDEFIX");
    aBuffer.startElement("http://schemas.sapia.org", "bar").
                addContent("bonjour").
            endElement("http://schemas.sapia.org", "bar");
    aBuffer.removeNamespace("http://schemas.sapia.org");
    aBuffer.endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<IDEFIX:foo xmlns:IDEFIX=\"http://schemas.sapia.org/idefix\">" +
                 "<IDEFIX:bar xmlns:IDEFIX=\"http://schemas.sapia.org\">bonjour</IDEFIX:bar></IDEFIX:foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer().
            addNamespace("http://schemas.sapia.org/idefix", "IDEFIX");

    aBuffer.startElement("http://schemas.sapia.org/idefix", "foo");
    aBuffer.addNamespace("http://schemas.sapia.org", "IDEFIX");
    aBuffer.startElement("http://schemas.sapia.org", "bar").
                endAttribute().
                addContent("bonjour").
            endElement("http://schemas.sapia.org", "bar");
    aBuffer.removeNamespace("http://schemas.sapia.org");
    aBuffer.endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<IDEFIX:foo xmlns:IDEFIX=\"http://schemas.sapia.org/idefix\">" +
                 "<IDEFIX:bar xmlns:IDEFIX=\"http://schemas.sapia.org\">bonjour</IDEFIX:bar></IDEFIX:foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer().
            addNamespace("http://schemas.sapia.org/idefix", "");

    aBuffer.startElement("http://schemas.sapia.org/idefix", "foo");
    aBuffer.addNamespace("http://schemas.sapia.org", "");
    aBuffer.startElement("http://schemas.sapia.org", "bar").
                addContent("bonjour").
            endElement("http://schemas.sapia.org", "bar");
    aBuffer.removeNamespace("http://schemas.sapia.org");
    aBuffer.endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo xmlns=\"http://schemas.sapia.org/idefix\">" +
                 "<bar xmlns=\"http://schemas.sapia.org\">bonjour</bar></foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer().
            addNamespace("http://schemas.sapia.org/idefix", "");

    aBuffer.startElement("http://schemas.sapia.org/idefix", "foo").
            endAttribute();
    aBuffer.addNamespace("http://schemas.sapia.org", "");
    aBuffer.startElement("http://schemas.sapia.org", "bar").
                addContent("bonjour").
            endElement("http://schemas.sapia.org", "bar");
    aBuffer.removeNamespace("http://schemas.sapia.org");
    aBuffer.endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo xmlns=\"http://schemas.sapia.org/idefix\">" +
                 "<bar xmlns=\"http://schemas.sapia.org\">bonjour</bar></foo>", aBuffer.toString());
  }

  public void testOverridingNSURI() throws Exception {
    XmlBuffer aBuffer = new XmlBuffer().
            addNamespace("http://schemas.sapia.org/idefix", "IDEFIX");

    aBuffer.startElement("http://schemas.sapia.org/idefix", "foo");
    aBuffer.addNamespace("http://schemas.sapia.org/idefix", "SAPIA");
    aBuffer.startElement("http://schemas.sapia.org/idefix", "bar").
                addContent("bonjour").
            endElement("http://schemas.sapia.org/idefix", "bar");
    aBuffer.removeNamespace("http://schemas.sapia.org/idefix");
    aBuffer.endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<IDEFIX:foo xmlns:IDEFIX=\"http://schemas.sapia.org/idefix\">" +
                 "<SAPIA:bar xmlns:SAPIA=\"http://schemas.sapia.org/idefix\">bonjour</SAPIA:bar></IDEFIX:foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer().
            addNamespace("http://schemas.sapia.org/idefix", "IDEFIX");

    aBuffer.startElement("http://schemas.sapia.org/idefix", "foo").endAttribute();
    aBuffer.addNamespace("http://schemas.sapia.org/idefix", "SAPIA");
    aBuffer.startElement("http://schemas.sapia.org/idefix", "bar").
              endAttribute().
              addContent("bonjour").
            endElement("http://schemas.sapia.org/idefix", "bar");
    aBuffer.removeNamespace("http://schemas.sapia.org/idefix");
    aBuffer.endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<IDEFIX:foo xmlns:IDEFIX=\"http://schemas.sapia.org/idefix\">" +
                 "<SAPIA:bar xmlns:SAPIA=\"http://schemas.sapia.org/idefix\">bonjour</SAPIA:bar></IDEFIX:foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer().
            addNamespace("http://schemas.sapia.org/idefix", "IDEFIX");

    aBuffer.startElement("http://schemas.sapia.org/idefix", "foo");
    aBuffer.addNamespace("http://schemas.sapia.org/idefix", "SAPIA");
    aBuffer.startElement("http://schemas.sapia.org/idefix", "bar").
                addContent("bonjour").
            endElement("http://schemas.sapia.org/idefix", "bar");
    aBuffer.removeNamespace("http://schemas.sapia.org/idefix");
    aBuffer.startElement("http://schemas.sapia.org/idefix", "bar").
                addContent("ola!").
            endElement("http://schemas.sapia.org/idefix", "bar").
          endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<IDEFIX:foo xmlns:IDEFIX=\"http://schemas.sapia.org/idefix\">" +
                 "<SAPIA:bar xmlns:SAPIA=\"http://schemas.sapia.org/idefix\">bonjour</SAPIA:bar>" +
                 "<IDEFIX:bar>ola!</IDEFIX:bar></IDEFIX:foo>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer().
            addNamespace("http://schemas.sapia.org/idefix", "IDEFIX");

    aBuffer.startElement("http://schemas.sapia.org/idefix", "foo");
    aBuffer.addNamespace("http://schemas.sapia.org/idefix", "SAPIA");
    aBuffer.startElement("http://schemas.sapia.org/idefix", "bar").
                endAttribute().
                addContent("bonjour").
            endElement("http://schemas.sapia.org/idefix", "bar");
    aBuffer.removeNamespace("http://schemas.sapia.org/idefix");
    aBuffer.startElement("http://schemas.sapia.org/idefix", "bar").
                endAttribute().
                addContent("ola!").
            endElement("http://schemas.sapia.org/idefix", "bar").
          endElement("http://schemas.sapia.org/idefix", "foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<IDEFIX:foo xmlns:IDEFIX=\"http://schemas.sapia.org/idefix\">" +
                 "<SAPIA:bar xmlns:SAPIA=\"http://schemas.sapia.org/idefix\">bonjour</SAPIA:bar>" +
                 "<IDEFIX:bar>ola!</IDEFIX:bar></IDEFIX:foo>", aBuffer.toString());
  }


  public void testEndAttributeScenarios() throws Exception {
    XmlBuffer aBuffer = new XmlBuffer();
    aBuffer.startElement("A").
              startElement("B").
                addAttribute("b", "b").
                startElement("C").
                  addAttribute("c", "c").
                endElement("C").
                startElement("D").
                  addAttribute("d", "d").
                  startElement("E").
                    addAttribute("e", "e").
                  endElement("E").
                endElement("D").
              endElement("B").
            endElement("A");
    
    assertEquals("The string content of the xml buffer is invalid",
                 "<A><B b=\"b\"><C c=\"c\" /><D d=\"d\"><E e=\"e\" /></D></B></A>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer();
    aBuffer.startElement("A").
              endAttribute().
              startElement("B").
                addAttribute("b", "b").
                endAttribute().
                startElement("C").
                  addAttribute("c", "c").
                  endAttribute().
                endElement("C").
                startElement("D").
                  addAttribute("d", "d").
                  endAttribute().
                  startElement("E").
                    addAttribute("e", "e").
                    endAttribute().
                  endElement("E").
                endElement("D").
              endElement("B").
            endElement("A");

    assertEquals("The string content of the xml buffer is invalid",
                 "<A><B b=\"b\"><C c=\"c\" /><D d=\"d\"><E e=\"e\" /></D></B></A>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer();
    aBuffer.startElement("A").
              startElement("B").
                addAttribute("b", "b").
                endAttribute().
                startElement("C").
                  addAttribute("c", "c").
                endElement("C").
                startElement("D").
                  addAttribute("d", "d").
                  startElement("E").
                    addAttribute("e", "e").
                    endAttribute().
                  endElement("E").
                endElement("D").
              endElement("B").
            endElement("A");

    assertEquals("The string content of the xml buffer is invalid",
                 "<A><B b=\"b\"><C c=\"c\" /><D d=\"d\"><E e=\"e\" /></D></B></A>", aBuffer.toString());

    /////////////////////////////////////////////////////////////////////////////
    aBuffer = new XmlBuffer();
    aBuffer.startElement("A").
              endAttribute().
              startElement("B").
                addAttribute("b", "b").
                startElement("C").
                  addAttribute("c", "c").
                  endAttribute().
                endElement("C").
                startElement("D").
                  addAttribute("d", "d").
                  endAttribute().
                  startElement("E").
                    addAttribute("e", "e").
                  endElement("E").
                endElement("D").
              endElement("B").
            endElement("A");

    assertEquals("The string content of the xml buffer is invalid",
                 "<A><B b=\"b\"><C c=\"c\" /><D d=\"d\"><E e=\"e\" /></D></B></A>", aBuffer.toString());
  }
  

  public void testXMLReservedCharacters() throws Exception {
    XmlBuffer aBuffer = new XmlBuffer();
    aBuffer.startElement("foo").
                addContent("<\"o\" & \'la!\'>").
                addAttribute("bar1", "<\"o\" & \'la!\'>").
                startElement("bar").
                    addContent("<\"bon\" & \'jour\'>").
                    addAttribute("bar2", "<\"bon\" & \'jour\'>").
                endElement("bar").
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo bar1=\"&lt;&quot;o&quot; &amp; &apos;la!&apos;&gt;\">" +
                 "<bar bar2=\"&lt;&quot;bon&quot; &amp; &apos;jour&apos;&gt;\">" +
                 "&lt;&quot;bon&quot; &amp; &apos;jour&apos;&gt;</bar>" +
                 "&lt;&quot;o&quot; &amp; &apos;la!&apos;&gt;</foo>", aBuffer.toString());
  }

  public void testSimpleElementWithCData() throws Exception {
    XmlBuffer aBuffer = new XmlBuffer();
    aBuffer.startElement("foo").
              addAttribute("bar1", "value1").
              addContent(new CData("bonjour")).
            endElement("foo");

    assertEquals("The string content of the xml buffer is invalid",
                 "<foo bar1=\"value1\">" +
                 "<![CDATA[bonjour]]>" +
                 "</foo>", aBuffer.toString());
  }
}
