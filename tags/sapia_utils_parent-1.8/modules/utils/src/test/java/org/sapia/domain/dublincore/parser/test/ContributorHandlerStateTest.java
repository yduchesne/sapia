package org.sapia.domain.dublincore.parser.test;

// Import of Sun's JDK classes
// ---------------------------
import java.io.ByteArrayInputStream;
import javax.xml.parsers.SAXParserFactory;

// Import of Apache's Log4j classes
// --------------------------------
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

// Import of Junit classes
// ---------------------------
import junit.framework.TestCase;
import junit.textui.TestRunner;

// Import of Sapia's Utility classes
// ---------------------------------
import org.sapia.util.xml.parser.DefaultHandlerContext;
import org.sapia.util.xml.parser.DelegateHandlerContext;
import org.sapia.util.xml.parser.HandlerContextIF;
import org.sapia.util.xml.parser.StatefullSAXHandler;

// Import of Sapia's domain classes
// --------------------------------
import org.sapia.domain.dublincore.parser.ContributorHandlerState;

/**
 *
 *
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */
public class ContributorHandlerStateTest extends TestCase {

  /** Initialization of the SAX parser factory instance. */
  private static SAXParserFactory _theSAXParserFactory;
  static {
    _theSAXParserFactory = SAXParserFactory.newInstance();
    _theSAXParserFactory.setNamespaceAware(true);
    _theSAXParserFactory.setValidating(false);
    BasicConfigurator.configure();
    org.apache.log4j.LogManager.getRootLogger().setLevel(org.apache.log4j.Level.WARN);
  }

  /** Defines the value of the Dublin Core element beign tested. */
  private static final String ELEMENT_VALUE = "Jean-Cedric Desrochers";

  /** Defines the unqualified valid test case. */
  private static final String UNQUALIFIED_VALID_ELEMENT =
      "<contributor xmlns=\"http://purl.org/dc/elements/1.1/\">" +
          ELEMENT_VALUE +
      "</contributor>";

  /** Defines the qualified valid test case. */
  private static final String QUALIFIED_VALID_ELEMENT =
      "<dc:contributor xmlns:dc=\"http://purl.org/dc/elements/1.1/\">" +
          ELEMENT_VALUE +
      "</dc:contributor>";

  /** Defines the an invalid namespace test case. */
  private static final String INVALID_NAMESPACE_ELEMENT =
      "<dc:contributor xmlns:dc=\"http://another.org/dc/elements/1.1/\">" +
          ELEMENT_VALUE +
      "</dc:contributor>";

  /** Defines the a unqualified xml:lang valid test case. */
  private static final String UNQUALIFIED_XMLLANG_VALID_ELEMENT =
      "<dc:contributor xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xml:lang=\"en-US\">" +
          ELEMENT_VALUE +
      "</dc:contributor>";

  /** Defines the a qualified http://www.w3.org/XML/1998/namespace:lang valid test case. */
  private static final String QUALIFIED_XMLLANG_VALID_ELEMENT =
      "<dc:contributor xmlns:x=\"http://www.w3.org/XML/1998/namespace\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" x:lang=\"en-US\">" +
          ELEMENT_VALUE +
      "</dc:contributor>";

  /**
   * Main method to executes the test cases of this class.
   */
  public static void main(String[] args) {
    TestRunner.run(ContributorHandlerStateTest.class);
  }

  private ContributorHandlerState _theHandlerState;
  private StatefullSAXHandler _theSAXHandler;

  /**
   * Creates a new ContributorHandlerStateTest instance for the
   * test case name passed in.
   */
  public ContributorHandlerStateTest(String aName) {
    super(aName);
  }

  /**
   *
   */
  public void setUp() {
    // Create and initialize the SAX handler
    _theHandlerState = new ContributorHandlerState();
    HandlerContextIF aHandlerContext = new DefaultHandlerContext(new DelegateHandlerContext(_theHandlerState));
    _theSAXHandler = new StatefullSAXHandler(aHandlerContext);
  }

  /**
   *
   */
  public void testUnqualifiedValidElement() throws Exception {
    try {
      // Parse the test element
      _theSAXParserFactory.newSAXParser().parse(
              new ByteArrayInputStream(UNQUALIFIED_VALID_ELEMENT.getBytes()), _theSAXHandler);

      assertNotNull("The result of the ContributorHandlerState should not be null", _theHandlerState.getResult());
      assertEquals("The value of the Contributor object is invalid", ELEMENT_VALUE, _theHandlerState.getResult().getValue());
      assertNull("The value of the xml:lang is invalid", _theHandlerState.getResult().getLanguageCode());
    } catch (Exception e) {
      org.apache.log4j.LogManager.getLogger(ContributorHandlerState.class).error("Error", e);
      throw new org.sapia.util.CompositeException("Error", e);
    }
  }

  /**
   *
   */
  public void testQualifiedValidElement() throws Exception {
    // Parse the test element
    _theSAXParserFactory.newSAXParser().parse(
            new ByteArrayInputStream(QUALIFIED_VALID_ELEMENT.getBytes()), _theSAXHandler);

    assertNotNull("The result of the ContributorHandlerState should not be null", _theHandlerState.getResult());
    assertEquals("The value of the Contributor object is invalid", ELEMENT_VALUE, _theHandlerState.getResult().getValue());
    assertNull("The value of the xml:lang is invalid", _theHandlerState.getResult().getLanguageCode());
  }

  /**
   *
   */
  public void testInvalidNamespaceElement() throws Exception {
    try {
      Logger.getLogger("org.sapia.util.xml.parser.DefaultHandlerContext").
              setLevel(Level.OFF);

      // Parse the test element
      _theSAXParserFactory.newSAXParser().parse(
              new ByteArrayInputStream(INVALID_NAMESPACE_ELEMENT.getBytes()), _theSAXHandler);
      fail("The invalid namespace element test case failed throwing an exception");
    } catch (Exception e) {
    } finally {
      Logger.getLogger("org.sapia.util.xml.parser.DefaultHandlerContext").
              setLevel(Level.WARN);
    }
  }

  /**
   *
   */
  public void testUnqualifiedXmlLangValidElement() throws Exception {
    // Parse the test element
    _theSAXParserFactory.newSAXParser().parse(
            new ByteArrayInputStream(UNQUALIFIED_XMLLANG_VALID_ELEMENT.getBytes()), _theSAXHandler);

    assertNotNull("The result of the ContributorHandlerState should not be null", _theHandlerState.getResult());
    assertEquals("The value of the Contributor object is invalid", ELEMENT_VALUE, _theHandlerState.getResult().getValue());
    assertEquals("The value of the xml:lang attribute is invalid", "en-US", _theHandlerState.getResult().getLanguageCode());
  }

  /**
   *
   */
  public void testQualifiedXmlLangValidElement() throws Exception {
    // Parse the test element
    _theSAXParserFactory.newSAXParser().parse(
            new ByteArrayInputStream(QUALIFIED_XMLLANG_VALID_ELEMENT.getBytes()), _theSAXHandler);

    assertNotNull("The result of the ContributorHandlerState should not be null", _theHandlerState.getResult());
    assertEquals("The value of the Contributor object is invalid", ELEMENT_VALUE, _theHandlerState.getResult().getValue());
    assertEquals("The value of the xml:lang attribute is invalid", "en-US", _theHandlerState.getResult().getLanguageCode());
  }
}