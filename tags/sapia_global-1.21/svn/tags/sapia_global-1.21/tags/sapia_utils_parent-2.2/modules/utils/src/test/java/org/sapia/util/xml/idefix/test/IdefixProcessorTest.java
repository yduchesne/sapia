package org.sapia.util.xml.idefix.test;

import org.apache.log4j.BasicConfigurator;
import org.sapia.util.xml.Namespace;
import org.sapia.util.xml.idefix.CompositeNamespaceFactory;
import org.sapia.util.xml.idefix.DefaultNamespaceFactory;
import org.sapia.util.xml.idefix.DefaultSerializerFactory;
import org.sapia.util.xml.idefix.IdefixProcessorFactory;
import org.sapia.util.xml.idefix.IdefixProcessorIF;
import org.sapia.util.xml.idefix.NamespaceFactoryIF;
import org.sapia.util.xml.idefix.PatternNamespaceFactory;
import org.sapia.util.xml.idefix.SerializerFactoryIF;

import junit.framework.TestCase;
import junit.textui.TestRunner;


/**
 * Class documentation
 *
 * @author <a href="mailto:jc@sapia-oss.org">Jean-Cedric Desrochers</a>
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html" target="sapia-license">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class IdefixProcessorTest extends TestCase {

  static {
    BasicConfigurator.configure();
  }
  
  private static IdefixProcessorFactory _theFactory = IdefixProcessorFactory.newFactory();

  public static void main(String[] args) {
    TestRunner.run(IdefixProcessorTest.class);
  }

  /**
   * Creates a new IdefixProcessorTest instance.
   */
  public IdefixProcessorTest(String aName) {
    super(aName);
  }


  /**
   * 
   */
  public void testSimpleObject() throws Exception {
    NamespaceFactoryIF aNSFactory = new DefaultNamespaceFactory();
    SerializerFactoryIF aSerFactory = new DefaultSerializerFactory();
    IdefixProcessorIF aProcessor = _theFactory.createProcessor(aSerFactory, aNSFactory);
    
    SimpleObject anObject = new SimpleObject("maximumPoolSize", "100", "java.lang.int");
    String aResult = aProcessor.process(anObject);
    String anExpectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<SimpleObject" +
            " name=\"maximumPoolSize\"" +
            " value=\"100\"" +
            " type=\"java.lang.int\"" +
            " />";
    assertEquals("The xml of the serializer is invalid", anExpectedResult, aResult);
  }


  /**
   * 
   */
  public void testSimpleObjectWithNS() throws Exception {
    PatternNamespaceFactory aPNSFactory = new PatternNamespaceFactory();
    aPNSFactory.addNamespace(SimpleObject.class, new Namespace("http://www.sapia-oss.org", "SAPIA"));
    DefaultNamespaceFactory aDNSFactory = new DefaultNamespaceFactory();
    CompositeNamespaceFactory aCNSFactory = new CompositeNamespaceFactory();
    aCNSFactory.registerNamespaceFactory(aPNSFactory);
    aCNSFactory.registerNamespaceFactory(aDNSFactory);

    SerializerFactoryIF aSerFactory = new DefaultSerializerFactory();
    IdefixProcessorIF aProcessor = _theFactory.createProcessor(aSerFactory, aCNSFactory);
    
    SimpleObject anObject = new SimpleObject("maximumPoolSize", "100", "java.lang.int");
    String aResult = aProcessor.process(anObject);
    String anExpectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
            "<SAPIA:SimpleObject xmlns:SAPIA=\"http://www.sapia-oss.org\"" +
            " name=\"maximumPoolSize\"" +
            " value=\"100\"" +
            " type=\"java.lang.int\"" +
            " />";
    assertEquals("The xml of the serializer is invalid", anExpectedResult, aResult);
  }

  /**
   *
   */
  public void testBasicTypes() throws Exception {
    NamespaceFactoryIF aNSFactory = new DefaultNamespaceFactory();
    SerializerFactoryIF aSerFactory = new DefaultSerializerFactory();
    IdefixProcessorIF aProcessor = _theFactory.createProcessor(aSerFactory, aNSFactory);
    
    String aResult = aProcessor.process(new BasicTypes());
    String anExpectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
    		"<BasicTypes byteObject=\"95\" shortObject=\"1095\" integerObject=\"188295\" longObject=\"100000000095\""+
    		" floatObject=\"1243.95\" doubleObject=\"1.000000000024395E13\" booleanObject=\"false\" positiveByte=\"127\""+
    		" negativeByte=\"-128\" positiveShort=\"32767\" negativeShort=\"-32768\" positiveInt=\"2147483647\""+
    		" negativeInt=\"-2147483648\" postitiveLong=\"9223372036854775807\" negativeLong=\"-9223372036854775808\""+
    		" positiveFloat=\"3.4028235E38\" negativeFloat=\"1.4E-45\" positiveDouble=\"1.7976931348623157E308\""+
    		" negativeDouble=\"4.9E-324\" boolean=\"true\" char=\"Z\" string=\"foo-bar\" />";  
    assertEquals("The xml of the serializer is invalid", anExpectedResult.length(), aResult.length());
  }
}
