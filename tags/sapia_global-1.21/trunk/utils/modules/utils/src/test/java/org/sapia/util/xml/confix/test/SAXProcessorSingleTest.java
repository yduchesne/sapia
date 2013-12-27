package org.sapia.util.xml.confix.test;

// Import of Sun's JDK classes
// ---------------------------
import java.io.ByteArrayInputStream;
import java.net.URL;

// Import of Apache's log4j classes
// --------------------------------
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

// Import of Junit classes
// ---------------------------
import junit.framework.TestCase;
import junit.textui.TestRunner;

// Import of Sapia's utility classes
// ---------------------------------
import org.sapia.util.xml.ProcessingException;
import org.sapia.util.xml.confix.ReflectionFactory;
import org.sapia.util.xml.confix.SAXProcessor;


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
public class SAXProcessorSingleTest extends TestCase {

  private static final String XML_NOT_WELL_FORMED =
    "<ThisIsBrokenXML>";

  private static final String XML_INVALID_CLASS_NAME =
    "<InvalidClassName />";

  private static final String XML_EMPTY_NAMED_VALUE =
    "<namedValue />";
    
	private static final String XML_CALLBACK =
		"<Url link=\"http://www.yahoo.com\"/>";
		
	private static final String XML_NULL_OBJECT  =
		"<Null />";      

  private static final String XML_GENERIC_VALUE =
    "<GenericObject value=\"a generic value\" />";

  private static final String XML_SIMPLE_NAMED_VALUE =
    "<NamedValue name=\"param1\" value=\"value1\"/>";

  private static final String XML_WRAPPED_NAMED_VALUE =
    "<wrappedNamedValue name=\"param1\" value=\"value1\"/>";

  private static final String XML_NAMED_VALUE_WITH_TEXT =
    "<NamedValue name=\"param1\" value=\"value1\">THIS IS SOME TEXT</NamedValue>";

  private static final String XML_TEXTUAL_NAMED_VALUE =
    "<textualNamedValue name=\"param1\" value=\"value1\">THIS IS SOME TEXT</textualNamedValue>";

  private static final String XML_WRAPPED_TEXTUAL_NAMED_VALUE =
    "<WrappedTextualNamedValue name=\"param1\" value=\"value1\">   THIS IS SOME TEXT   </WrappedTextualNamedValue>";

  static {
    BasicConfigurator.configure();
  }

  private SAXProcessor _theProcessor;

  public static void main(String[] args) {
    TestRunner.run(SAXProcessorSingleTest.class);
  }

  private static void disableLog() {
    LogManager.getRootLogger().setLevel(Level.OFF);
  }

  private static void enableLog() {
    LogManager.getRootLogger().setLevel(Level.DEBUG);
  }

  public SAXProcessorSingleTest(String aName) {
    super(aName);
  }

  public void setUp() {
    ReflectionFactory anObjectFactory = new ReflectionFactory(
            new String[] { "org.sapia.util.xml.confix.test" } );
    _theProcessor = new SAXProcessor(anObjectFactory);
  }

  public void testBrokenXml() throws Exception {
    disableLog();
    try {
      ByteArrayInputStream anInput =
              new ByteArrayInputStream(XML_NOT_WELL_FORMED.getBytes());
      Object anObject = _theProcessor.process(anInput);
      fail("The broken XML should not have been parsed correctly");
    } catch (ProcessingException pe) {
    } finally {
      enableLog();
    }
  }

  public void testInvalidClassName() throws Exception {
    disableLog();
    try {
      ByteArrayInputStream anInput =
              new ByteArrayInputStream(XML_INVALID_CLASS_NAME.getBytes());
      Object anObject = _theProcessor.process(anInput);
      if (anObject != null) {
        fail("The process should have fail because the class InvalidClassNamed doesn't exist");
      }
    } catch (ProcessingException pe) {
    } finally {
      enableLog();
    }
  }

  public void testEmptyNamedValue() throws Exception {
    ByteArrayInputStream anInput =
            new ByteArrayInputStream(XML_EMPTY_NAMED_VALUE.getBytes());
    Object anObject = _theProcessor.process(anInput);
    assertTrue("The returned object is not a NamedValue", anObject instanceof NamedValue);

    NamedValue aNamedValue = (NamedValue) anObject;
    assertNull("The name of the named value is invalid", aNamedValue.getName());
    assertNull("The value of the named value is invalid", aNamedValue.getValue());
  }

  public void testGenericValue() throws Exception {
    ByteArrayInputStream anInput =
            new ByteArrayInputStream(XML_GENERIC_VALUE.getBytes());
    Object anObject = _theProcessor.process(anInput);
    assertTrue("The returned object is not a GenericObject", anObject instanceof GenericObject);

    GenericObject aGenericObject = (GenericObject) anObject;
    assertEquals("The value of the generic object is invalid", "a generic value", aGenericObject.getValue());
  }

  public void testSimpleNamedValue() throws Exception {
    ByteArrayInputStream anInput =
            new ByteArrayInputStream(XML_SIMPLE_NAMED_VALUE.getBytes());
    Object anObject = _theProcessor.process(anInput);
    assertTrue("The returned object is not a NamedValue", anObject instanceof NamedValue);

    NamedValue aNamedValue = (NamedValue) anObject;
    assertEquals("The name of the named value is invalid", "param1", aNamedValue.getName());
    assertEquals("The value of the named value is invalid", "value1", aNamedValue.getValue());
  }

  public void testWrappedNamedValue() throws Exception {
    ByteArrayInputStream anInput =
            new ByteArrayInputStream(XML_WRAPPED_NAMED_VALUE.getBytes());
    Object anObject = _theProcessor.process(anInput);
    assertTrue("The returned object is not a WrappedNamedValue", anObject instanceof WrappedNamedValue);

    WrappedNamedValue aWrapper = (WrappedNamedValue) anObject;
    assertEquals("The name of the wrapped named value is invalid", "param1", aWrapper.getNamedValue().getName());
    assertEquals("The value of the wrapped named value is invalid", "value1", aWrapper.getNamedValue().getValue());
  }

  public void testNamedValueWithText() throws Exception {
    disableLog();
    try {
      ByteArrayInputStream anInput =
              new ByteArrayInputStream(XML_NAMED_VALUE_WITH_TEXT.getBytes());
      Object anObject = _theProcessor.process(anInput);
      fail("The NamedValue doesn't have setText()... it should have failed.");
    } catch (ProcessingException pe) {
    } finally {
      enableLog();
    }
  }

  public void testTextualNamedValue() throws Exception {
    ByteArrayInputStream anInput =
            new ByteArrayInputStream(XML_TEXTUAL_NAMED_VALUE.getBytes());
    Object anObject = _theProcessor.process(anInput);
    assertTrue("The returned object is not a TextualNamedValue", anObject instanceof TextualNamedValue);

    TextualNamedValue aTextualNamedValue = (TextualNamedValue) anObject;
    assertEquals("The name of the textual named value is invalid", "param1", aTextualNamedValue.getName());
    assertEquals("The value of the textual named value is invalid", "value1", aTextualNamedValue.getValue());
    assertEquals("The text of the textual named value is invalid", "THIS IS SOME TEXT", aTextualNamedValue.getText());
  }

  public void testWrappedTextualNamedValue() throws Exception {
    ByteArrayInputStream anInput =
            new ByteArrayInputStream(XML_WRAPPED_TEXTUAL_NAMED_VALUE.getBytes());
    Object anObject = _theProcessor.process(anInput);
    assertTrue("The returned object is not a WrappedTextualNamedValue", anObject instanceof WrappedTextualNamedValue);

    WrappedTextualNamedValue aWrapper = (WrappedTextualNamedValue) anObject;
    assertEquals("The name of the textual named value is invalid", "param1", aWrapper.getTextualNamedValue().getName());
    assertEquals("The value of the textual named value is invalid", "value1", aWrapper.getTextualNamedValue().getValue());
    assertEquals("The text of the textual named value is invalid", "THIS IS SOME TEXT", aWrapper.getTextualNamedValue().getText());
  }

	public void testCallback() throws Exception{
		ByteArrayInputStream anInput =
						new ByteArrayInputStream(XML_CALLBACK.getBytes());  	
		URL url = (URL)_theProcessor.process(anInput);
	}
  
	public void testNullObject() throws Exception{
		ByteArrayInputStream anInput =
						new ByteArrayInputStream(XML_NULL_OBJECT.getBytes());  	
		Object nullObject = _theProcessor.process(anInput);
		super.assertTrue(nullObject == null);
	}  

  public void testBasicTypes() throws Exception {
    String anXml = "<BasicTypes" +
        " byte=\"5\" byteObject=\"32\" " +
        " short=\"825\" short-object=\"25432\" " +
        " int=\"75832\" integer-Object=\"1500000\" " +
        " long=\"3500000000\" long.object=\"919293949596979899\" " +
        " float=\"52.54\" float.Object=\"899.2535\" " +
        " Double=\"3.14154853\" DoubleObject=\"832.123456789\" " +
        " Char=\"a\" Character-Object=\"b\" " +
        " Boolean=\"true\" Boolean.Object=\"yes\" " +
        " />";
    ByteArrayInputStream anInput =
            new ByteArrayInputStream(anXml.getBytes());
    Object anObject = _theProcessor.process(anInput);
    assertTrue("The returned object is not a BasicTypes", anObject instanceof BasicTypes);

    BasicTypes aBasicType = (BasicTypes) anObject;
    assertEquals("The byte of the basic types is invalid", 5, aBasicType.getByte());
    assertEquals("The Byte object of the basic types is invalid", 32, aBasicType.getByteObject().byteValue());
    assertEquals("The short of the basic types is invalid", 825, aBasicType.getShort());
    assertEquals("The Short object of the basic types is invalid", 25432, aBasicType.getShortObject().shortValue());
    assertEquals("The int of the basic types is invalid", 75832, aBasicType.getInt());
    assertEquals("The Integer object of the basic types is invalid", 1500000, aBasicType.getIntegerObject().intValue());
    assertEquals("The long of the basic types is invalid", 3500000000L, aBasicType.getLong());
    assertEquals("The Integer object of the basic types is invalid", 919293949596979899L, aBasicType.getLongObject().longValue());
    assertEquals("The float of the basic types is invalid", 52.54, aBasicType.getFloat(), 0.01);
    assertEquals("The Float object of the basic types is invalid", 899.2535, aBasicType.getFloatObject().floatValue(), 0.0001);
    assertEquals("The double of the basic types is invalid", 3.14154853, aBasicType.getDouble(), 0);
    assertEquals("The Double object of the basic types is invalid", 832.123456789, aBasicType.getDoubleObject().doubleValue(), 0);
    assertEquals("The char of the basic types is invalid", 'a', aBasicType.getChar());
    assertEquals("The Character object of the basic types is invalid", 'b', aBasicType.getCharacterObject().charValue());
    assertEquals("The boolean of the basic types is invalid", true, aBasicType.getBoolean());
    assertEquals("The Character object of the basic types is invalid", true, aBasicType.getBooleanObject().booleanValue());
  }
}
