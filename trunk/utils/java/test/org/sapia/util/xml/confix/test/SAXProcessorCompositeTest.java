package org.sapia.util.xml.confix.test;

// Import of Sun's JDK classes
// ---------------------------
import java.io.ByteArrayInputStream;
import java.util.Iterator;

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
public class SAXProcessorCompositeTest extends TestCase {

  private static final String XML_SIMPLE_CONFIG =
    "<Config name=\"config1\">" +
    "    <NamedValue name=\"param1\" value=\"value1\"/>" +
    "</Config>";

  private static final String XML_CONFIG_WITH_ELEMENT =
    "<Config>" +
    "    <Name>config2</Name>" +
    "    <NamedValue name=\"param1\" value=\"value1\"/>" +
    "</Config>";

  private static final String XML_COMPOSITE_CONFIG_WITH_ELEMENT =
    "<Config>" +
    "    <Name>config2</Name>" +
    "    <NamedValue name=\"param1\" value=\"value1\"/>" +
    "    <NamedValue>" +
    "        <Name>param2</Name>" +
    "        <Value>value2</Value>" +
    "    </NamedValue>" +
    "    <NamedValue name=\"param3\">" +
    "        <Value>value3</Value>" +
    "    </NamedValue>" +
    "    <NamedValue name=\"attrib-param4\" value=\"attrib-value4\">" +
    "        <Name>elem-param4</Name>" +
    "        <Value>elem-value4</Value>" +
    "    </NamedValue>" +
    "</Config>";

  private static final String XML_WRAPPED_NAMED_VALUE =
    "<Config name=\"config3\">" +
    "    <WrappedNamedValue name=\"param1\" value=\"value1\"/>" +
    "    <WrappedNamedValue>" +
    "        <Name>param2</Name>" +
    "        <Value>value2</Value>" +
    "    </WrappedNamedValue>" +
    "</Config>";

  private static final String XML_WRAPPED_CONFIG =
    "<WrappedConfig name=\"config4\">" +
    "    <NamedValue name=\"param1\" value=\"value1\"/>" +
    "    <WrappedNamedValue>" +
    "        <Name>param2</Name>" +
    "        <Value>value2</Value>" +
    "    </WrappedNamedValue>" +
    "</WrappedConfig>";

  private static final String XML_TEXTUAL_CONFIG =
    "<TextualConfig name=\"config5\">" +
    "    <NamedValue name=\"param1\" value=\"value1\"/>" +
    "    <WrappedNamedValue>" +
    "        <Name>param2</Name>" +
    "        <Value>value2</Value>" +
    "    </WrappedNamedValue>" +
    "    THIS IS A FINAL CONFIG CONTENT" +
    "</TextualConfig>";

  private static final String XML_TEXTUAL_WRAPPED_CONFIG =
    "<WrappedTextualConfig name=\"config6\">" +
    "    <NamedValue name=\"param1\" value=\"value1\"/>" +
    "    <WrappedTextualNamedValue>" +
    "        TEXT:TextualNamedValue" +
    "        <Name>param2</Name>" +
    "        <Value>value2</Value>" +
    "    </WrappedTextualNamedValue>" +
    "    THIS IS A FINAL CONFIG CONTENT" +
    "</WrappedTextualConfig>";


  private static final String XML_JDOM_HANDLER =
    "<WrappedTextualConfig name=\"config7\">" +
    "    <NamedValue name=\"param1\" value=\"value1\"/>" +
    "    <CustomConfig type=\"ant\">" +
    "        <build default=\"build\"><target name=\"build\"><javac/></target></build>" +
    "    </CustomConfig>" +
    "    THIS IS A FINAL CONFIG CONTENT" +
    "</WrappedTextualConfig>";

  private static final String XML_CREATOR =
    "<Config name=\"config8\">" +
    "    <Param name=\"param5\" value=\"value5\"/>" +
    "</Config>";

  private static final String XML_OBJECT_HANDLER =
    "<config name=\"config9\">" +
    "    <stringBuffer length=\"5\"/>" +
    "</config>";

  private static final String XML_CDATA_CONFIG =
    "<TextualConfig name=\"config10\">" +
    "    <![CDATA[" +
    "        THIS IS A CDATA CONFIG CONTENT" +
    "    ]]>" +
    "</TextualConfig>";

//  private static final String XML_IGNORED_CONFIG =
//    "<Settings name=\"nullConfig\">" +
//    "    <Config>" +
//    "        <Name>config2</Name>" +
//    "        <NamedValue name=\"param1\" value=\"value1\"/>" +
//    "    </Config>" +
//    "    <Gui />" +
//    "</Null>";

  static {
    BasicConfigurator.configure();
    LogManager.getLogger("org.sapia.util.xml.parser.StatefullSAXHandler").setLevel(Level.WARN);
  }

  private SAXProcessor _theProcessor;

  public static void main(String[] args) {
    TestRunner.run(SAXProcessorCompositeTest.class);
  }

  private static void disableLog() {
    LogManager.getRootLogger().setLevel(Level.OFF);
  }

  private static void enableLog() {
    LogManager.getRootLogger().setLevel(Level.DEBUG);
  }

  public SAXProcessorCompositeTest(String aName) {
    super(aName);
  }

  public void setUp() {
    ReflectionFactory anObjectFactory = new ReflectionFactory(
            new String[] { "org.sapia.util.xml.confix.test", "java.lang" } );
    _theProcessor = new SAXProcessor(anObjectFactory);
  }

  public void testSimpleConfig() throws Exception {
    ByteArrayInputStream anInput =
            new ByteArrayInputStream(XML_SIMPLE_CONFIG.getBytes());
    Object anObject = _theProcessor.process(anInput);
    assertTrue("The returned object is not a Config", anObject instanceof Config);

    Config aConfig = (Config) anObject;
    assertEquals("The name of the config is invalid", "config1", aConfig.getName());
    assertEquals("The size of the named value list is invalid", 1, aConfig.getNamedValues().size());
    Iterator someNamedValues = aConfig.getNamedValues().iterator();

    NamedValue aNamedValue = (NamedValue) someNamedValues.next();
    assertEquals("The name of the named value is invalid", "param1", aNamedValue.getName());
    assertEquals("The value of the named value is invalid", "value1", aNamedValue.getValue());
  }

  public void testSimpleConfigWithElement() throws Exception {
    ByteArrayInputStream anInput =
            new ByteArrayInputStream(XML_CONFIG_WITH_ELEMENT.getBytes());
    Object anObject = _theProcessor.process(anInput);
    assertTrue("The returned object is not a Config", anObject instanceof Config);

    Config aConfig = (Config) anObject;
    assertEquals("The name of the config is invalid", "config2", aConfig.getName());
    assertEquals("The size of the named value list is invalid", 1, aConfig.getNamedValues().size());
    Iterator someNamedValues = aConfig.getNamedValues().iterator();

    NamedValue aNamedValue = (NamedValue) someNamedValues.next();
    assertEquals("The name of the named value is invalid", "param1", aNamedValue.getName());
    assertEquals("The value of the named value is invalid", "value1", aNamedValue.getValue());
  }

  public void testCompositeConfigWithElement() throws Exception {
    ByteArrayInputStream anInput =
            new ByteArrayInputStream(XML_COMPOSITE_CONFIG_WITH_ELEMENT.getBytes());
    Object anObject = _theProcessor.process(anInput);
    assertTrue("The returned object is not a Config", anObject instanceof Config);

    Config aConfig = (Config) anObject;
    assertEquals("The name of the config is invalid", "config2", aConfig.getName());
    assertEquals("The size of the named value list is invalid", 4, aConfig.getNamedValues().size());
    Iterator someNamedValues = aConfig.getNamedValues().iterator();

    NamedValue aNamedValue = (NamedValue) someNamedValues.next();
    assertEquals("The name of the named value is invalid", "param1", aNamedValue.getName());
    assertEquals("The value of the named value is invalid", "value1", aNamedValue.getValue());

    aNamedValue = (NamedValue) someNamedValues.next();
    assertEquals("The name of the named value is invalid", "param2", aNamedValue.getName());
    assertEquals("The value of the named value is invalid", "value2", aNamedValue.getValue());

    aNamedValue = (NamedValue) someNamedValues.next();
    assertEquals("The name of the named value is invalid", "param3", aNamedValue.getName());
    assertEquals("The value of the named value is invalid", "value3", aNamedValue.getValue());

    aNamedValue = (NamedValue) someNamedValues.next();
    assertEquals("The name of the named value is invalid", "elem-param4", aNamedValue.getName());
    assertEquals("The value of the named value is invalid", "elem-value4", aNamedValue.getValue());
  }

  public void testWrappedNamedValue() throws Exception {
    ByteArrayInputStream anInput =
            new ByteArrayInputStream(XML_WRAPPED_NAMED_VALUE.getBytes());
    Object anObject = _theProcessor.process(anInput);
    assertTrue("The returned object is not a Config", anObject instanceof Config);

    Config aConfig = (Config) anObject;
    assertEquals("The name of the config is invalid", "config3", aConfig.getName());
    assertEquals("The size of the named value list is invalid", 2, aConfig.getNamedValues().size());
    Iterator someNamedValues = aConfig.getNamedValues().iterator();

    NamedValue aNamedValue = (NamedValue) someNamedValues.next();
    assertEquals("The name of the named value is invalid", "param1", aNamedValue.getName());
    assertEquals("The value of the named value is invalid", "value1", aNamedValue.getValue());

    aNamedValue = (NamedValue) someNamedValues.next();
    assertEquals("The name of the named value is invalid", "param2", aNamedValue.getName());
    assertEquals("The value of the named value is invalid", "value2", aNamedValue.getValue());
  }

  public void testTextualConfig() throws Exception {
    ByteArrayInputStream anInput =
            new ByteArrayInputStream(XML_TEXTUAL_CONFIG.getBytes());
    Object anObject = _theProcessor.process(anInput);
    assertTrue("The returned object is not a TextualConfig", anObject instanceof TextualConfig);

    TextualConfig aTextualConfig = (TextualConfig) anObject;
    assertEquals("The name of the config is invalid", "config5", aTextualConfig.getName());
    assertEquals("The content of the config is invalid", "THIS IS A FINAL CONFIG CONTENT", aTextualConfig.getText());
    assertEquals("The size of the named value list is invalid", 2, aTextualConfig.getNamedValues().size());
    Iterator someNamedValues = aTextualConfig.getNamedValues().iterator();

    NamedValue aNamedValue = (NamedValue) someNamedValues.next();
    assertEquals("The name of the named value is invalid", "param1", aNamedValue.getName());
    assertEquals("The value of the named value is invalid", "value1", aNamedValue.getValue());

    aNamedValue = (NamedValue) someNamedValues.next();
    assertEquals("The name of the named value is invalid", "param2", aNamedValue.getName());
    assertEquals("The value of the named value is invalid", "value2", aNamedValue.getValue());
  }

  public void testCdataConfig() throws Exception {
    ByteArrayInputStream anInput =
            new ByteArrayInputStream(XML_CDATA_CONFIG.getBytes());
    Object anObject = _theProcessor.process(anInput);
    assertTrue("The returned object is not a TextualConfig", anObject instanceof TextualConfig);

    TextualConfig aTextualConfig = (TextualConfig) anObject;
    assertEquals("The name of the config is invalid", "config10", aTextualConfig.getName());
    assertEquals("The content of the config is invalid", "THIS IS A CDATA CONFIG CONTENT", aTextualConfig.getText());
    assertEquals("The size of the named value list is invalid", 0, aTextualConfig.getNamedValues().size());
  }

  public void testWrappedTextualConfig() throws Exception {
    ByteArrayInputStream anInput =
            new ByteArrayInputStream(XML_TEXTUAL_WRAPPED_CONFIG.getBytes());
    Object anObject = _theProcessor.process(anInput);
    assertTrue("The returned object is not a TextualConfig", anObject instanceof WrappedTextualConfig);

    WrappedTextualConfig aWrapper = (WrappedTextualConfig) anObject;
    assertEquals("The name of the config is invalid", "config6", aWrapper.getTextualConfig().getName());
    assertEquals("The content of the config is invalid", "THIS IS A FINAL CONFIG CONTENT", aWrapper.getTextualConfig().getText());
    assertEquals("The size of the named value list is invalid", 2, aWrapper.getTextualConfig().getNamedValues().size());
    Iterator someNamedValues = aWrapper.getTextualConfig().getNamedValues().iterator();

    NamedValue aNamedValue = (NamedValue) someNamedValues.next();
    assertEquals("The name of the named value is invalid", "param1", aNamedValue.getName());
    assertEquals("The value of the named value is invalid", "value1", aNamedValue.getValue());

    TextualNamedValue aTextualNamedValue = (TextualNamedValue) someNamedValues.next();
    assertEquals("The name of the named value is invalid", "param2", aTextualNamedValue.getName());
    assertEquals("The value of the named value is invalid", "value2", aTextualNamedValue.getValue());
    assertEquals("The text of the named value is invalid", "TEXT:TextualNamedValue", aTextualNamedValue.getText());
  }

  public void testJdomHandler() throws Exception {
//    ByteArrayInputStream anInput =
//            new ByteArrayInputStream(XML_JDOM_HANDLER.getBytes());
//    Object anObject = _theProcessor.process(anInput);
//    assertTrue("The returned object is not a TextualConfig", anObject instanceof WrappedTextualConfig);
//
//    WrappedTextualConfig aWrapper = (WrappedTextualConfig) anObject;
//    assertEquals("The name of the config is invalid", "config7", aWrapper.getTextualConfig().getName());
//    assertEquals("The content of the config is invalid", "THIS IS A FINAL CONFIG CONTENT", aWrapper.getTextualConfig().getText());
//    assertEquals("The size of the named value list is invalid", 1, aWrapper.getTextualConfig().getNamedValues().size());
//    Iterator someNamedValues = aWrapper.getTextualConfig().getNamedValues().iterator();
//
//    NamedValue aNamedValue = (NamedValue) someNamedValues.next();
//    assertEquals("The name of the named value is invalid", "param1", aNamedValue.getName());
//    assertEquals("The value of the named value is invalid", "value1", aNamedValue.getValue());
//
//    assertEquals("The size of the custom config list is invalid", 1, aWrapper.getTextualConfig().getCustomConfigs().size());
//    Iterator someCustomCongigs = aWrapper.getTextualConfig().getCustomConfigs().iterator();
//
//    CustomConfig aCustomConfig = (CustomConfig) someCustomCongigs.next();
//    assertEquals("The type of the custom config is invalid", "ant", aCustomConfig.getType());
//
//    Element anElement = aCustomConfig.getConfigElement();
//    assertEquals("The name of the element is invalid", "build", anElement.getName());
//    assertEquals("The size of the attribute list is invalid", 1, anElement.getAttributes().size());
//
//    Attribute anAttribute = (Attribute) anElement.getAttributes().iterator().next();
//    assertEquals("The name of the attribute is invalid", "default", anAttribute.getName());
//    assertEquals("The value of the attribute is invalid", "build", anAttribute.getValue());
//    assertEquals("The size of the child list is invalid", 1, anElement.getChildren().size());
//
//    anElement = (Element) anElement.getChildren().iterator().next();
//    assertEquals("The name of the element is invalid", "target", anElement.getName());
//    assertEquals("The size of the attribute list is invalid", 1, anElement.getAttributes().size());
//
//    anAttribute = (Attribute) anElement.getAttributes().iterator().next();
//    assertEquals("The name of the attribute is invalid", "name", anAttribute.getName());
//    assertEquals("The value of the attribute is invalid", "build", anAttribute.getValue());
//    assertEquals("The size of the child list is invalid", 1, anElement.getChildren().size());
//
//    anElement = (Element) anElement.getChildren().iterator().next();
//    assertEquals("The name of the element is invalid", "javac", anElement.getName());
//    assertEquals("The size of the attribute list is invalid", 0, anElement.getAttributes().size());
  }

  public void testCreatorConfig() throws Exception {
    ByteArrayInputStream anInput =
            new ByteArrayInputStream(XML_CREATOR.getBytes());
    Object anObject = _theProcessor.process(anInput);
    assertTrue("The returned object is not a Config", anObject instanceof Config);

    Config aConfig = (Config) anObject;
    assertEquals("The name of the config is invalid", "config8", aConfig.getName());
    assertEquals("The size of the named value list is invalid", 1, aConfig.getNamedValues().size());
    Iterator someNamedValues = aConfig.getNamedValues().iterator();

    NamedValue aNamedValue = (NamedValue) someNamedValues.next();
    assertEquals("The name of the named value is invalid", "param5", aNamedValue.getName());
    assertEquals("The value of the named value is invalid", "value5", aNamedValue.getValue());
  }

  public void testObjectHandler() throws Exception {
    ByteArrayInputStream anInput =
            new ByteArrayInputStream(XML_OBJECT_HANDLER.getBytes());
    Object anObject = _theProcessor.process(anInput);
    assertTrue("The returned object is not a Config", anObject instanceof Config);

    Config aConfig = (Config) anObject;
    assertEquals("The name of the config is invalid", "config9", aConfig.getName());
    assertEquals("The size of the named value list is invalid", 0, aConfig.getNamedValues().size());

    assertEquals("The length of the custom object array is invalid", 2, aConfig.getCustomObject().length);
    assertEquals("The first custom object is invalid", "stringBuffer", aConfig.getCustomObject()[0]);
    assertEquals("The second custom object is invalid", 5, ((StringBuffer) aConfig.getCustomObject()[1]).length());
  }
  
//  public void testIgnoredObjects() throws Exception {
//    ByteArrayInputStream anInput =
//      new ByteArrayInputStream(XML_IGNORED_CONFIG.getBytes());
//    Object anObject = _theProcessor.process(anInput);
//    assertTrue("The returned object is not a Config", anObject instanceof Config);
//  }
}
