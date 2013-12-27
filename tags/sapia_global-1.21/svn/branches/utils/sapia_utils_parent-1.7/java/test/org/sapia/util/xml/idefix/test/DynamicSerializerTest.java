package org.sapia.util.xml.idefix.test;

import org.sapia.util.xml.idefix.DynamicSerializer;

import junit.framework.TestCase;
import junit.textui.TestRunner;


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
public class DynamicSerializerTest extends TestCase {

  public static void main(String[] args) {
    TestRunner.run(DynamicSerializerTest.class);
  }

  public DynamicSerializerTest(String aName) {
    super(aName);
  }


  /**
   *
   */
  public void testSimpleObject() throws Exception {
    SimpleObject anObject = new SimpleObject("maximumPoolSize", "100", "java.lang.int");
    DynamicSerializer aSerializer = new DynamicSerializer();
    String aResult = aSerializer.serialize(anObject);
    String anExpectedResult = "<SimpleObject" +
            " name=\"maximumPoolSize\"" +
            " value=\"100\"" +
            " type=\"java.lang.int\"" +
            " />";
    assertEquals("The xml of the serializer is invalid", anExpectedResult, aResult);
  }


  /**
   *
   */
  public void testComplexObject() throws Exception {
    SimpleObject anObject = new SimpleObject("maximumPoolSize", "100", "java.lang.int");
    ComplexObject aComplex = new ComplexObject(anObject, "AnotherObject");
    DynamicSerializer aSerializer = new DynamicSerializer();
    String aResult = aSerializer.serialize(aComplex);
    String anExpectedResult =
            "<ComplexObject object2=\"AnotherObject\">" +
                "<SimpleObject name=\"maximumPoolSize\" value=\"100\" type=\"java.lang.int\" />" +
                "<JAVA:Array xmlns:JAVA=\"http://java.sun.com\" type=\"int\" size=\"3\">" +
                    "<JAVA:Element index=\"0\" value=\"1\" />" +
                    "<JAVA:Element index=\"1\" value=\"2\" />" +
                    "<JAVA:Element index=\"2\" value=\"3\" />" +
                "</JAVA:Array>" +
            "</ComplexObject>";
    assertEquals("The xml of the serializer is invalid", anExpectedResult, aResult);
  }


  /**
   *
   */
  public void testBasicTypes() throws Exception {
    DynamicSerializer aSerializer = new DynamicSerializer();
    String aResult = aSerializer.serialize(new BasicTypes());
    String anExpectedResult =
      "<BasicTypes byteObject=\"95\" shortObject=\"1095\" integerObject=\"188295\" longObject=\"100000000095\""+
      " floatObject=\"1243.95\" doubleObject=\"1.000000000024395E13\" booleanObject=\"false\" positiveByte=\"127\""+
      " negativeByte=\"-128\" positiveShort=\"32767\" negativeShort=\"-32768\" positiveInt=\"2147483647\""+
      " negativeInt=\"-2147483648\" postitiveLong=\"9223372036854775807\" negativeLong=\"-9223372036854775808\"" +
      " positiveFloat=\"3.4028235E38\" negativeFloat=\"1.4E-45\" positiveDouble=\"1.7976931348623157E308\"" +
      " negativeDouble=\"4.9E-324\" boolean=\"true\" char=\"Z\" string=\"foo-bar\" />";
    assertEquals("The xml of the serializer is invalid", anExpectedResult.length(), aResult.length());
  }


  public void testCircularReference() throws Exception {
    String aValue = "value";
    SimpleObject anObject = new SimpleObject(aValue, aValue, aValue);
    DynamicSerializer aSerializer = new DynamicSerializer();
    String aResult = aSerializer.serialize(anObject);
    String anExpectedResult = "<SimpleObject" +
            " name=\"value\"" +
            " value=\"value\"" +
            " type=\"value\"" +
            " />";
    assertEquals("The xml of the serializer is invalid", anExpectedResult, aResult);

    ComplexObject aComplex = new ComplexObject(anObject, anObject);
    aResult = aSerializer.serialize(aComplex);
    anExpectedResult =
        "<ComplexObject>" +
          "<SimpleObject name=\"value\" value=\"value\" type=\"value\" />" +
          "<SimpleObject name=\"value\" value=\"value\" type=\"value\" />" +
          "<JAVA:Array xmlns:JAVA=\"http://java.sun.com\" type=\"int\" size=\"3\">" +
            "<JAVA:Element index=\"0\" value=\"1\" />" +
            "<JAVA:Element index=\"1\" value=\"2\" />" +
            "<JAVA:Element index=\"2\" value=\"3\" />" +
          "</JAVA:Array>" +
        "</ComplexObject>";
    assertEquals("The xml of the serializer is invalid", anExpectedResult, aResult);

    try {
      aComplex.setObject2(aComplex);
      aResult = aSerializer.serialize(aComplex);
      fail(aResult);
    } catch (IllegalStateException ise) {
    }
  }


  /**
   *
   */
  public void testMoreComplexObject() {
    ComplexObject aComplex = new MoreComplexObject(null, null);
    DynamicSerializer aSerializer = new DynamicSerializer();
    String aResult = aSerializer.serialize(aComplex);
//    String anExpectedResult =
//      "<MoreComplexObject>" +
//      	"<JAVA:Array xmlns:JAVA=\"http://java.sun.com\" type=\"java.lang.Object\" size=\"5\">" +
//      		"<JAVA:Element index=\"2\">" +
//      			"<ZoneInfo rawOffset=\"-18000000\" dSTSavings=\"3600000\" dirty=\"false\" displayName=\"Eastern Standard Time\" iD=\"EST\">" +
//      				"<SimpleTimeZone rawOffset=\"-18000000\" dSTSavings=\"3600000\" displayName=\"Eastern Standard Time\" iD=\"EST\" />" +
//            "</ZoneInfo>" +
//          "</JAVA:Element>" +
//        "</JAVA:Array>" +
//        "<JAVA:Array xmlns:JAVA=\"http://java.sun.com\" type=\"int\" size=\"3\">" +
//        	"<JAVA:Element index=\"0\" value=\"1\" />" +
//        	"<JAVA:Element index=\"1\" value=\"2\" />" +
//        	"<JAVA:Element index=\"2\" value=\"3\" />" +
//        "</JAVA:Array>" +
//      "</MoreComplexObject>";
//    assertEquals("The xml of the serializer is invalid", anExpectedResult, aResult);
    assertNotNull(aResult);
  }


  /**
   *
   */
  public void testCollectionObject() {
    CollectionObject anObject = new CollectionObject();
    DynamicSerializer aSerializer = new DynamicSerializer();
    String aResult = aSerializer.serialize(anObject);
//    String anExpectedResult =
//      "<CollectionObject>" +
//        "<JAVA:Collection xmlns:JAVA=\"http://java.sun.com\" type=\"java.util.Vector\" size=\"0\" />" +
//        "<JAVA:Iterator xmlns:JAVA=\"http://java.sun.com\">" +
//          "<JAVA:Element index=\"0\" value=\"One\" />" +
//          "<JAVA:Element index=\"2\">" +
//            "<SimpleObject name=\"element\" value=\"Two\" type=\"java.lang.String\" />" +
//          "</JAVA:Element>" +
//          "<JAVA:Element index=\"4\">" +
//           	"<ComplexObject>" +
//           		"<SimpleObject name=\"element\" value=\"Three.One\" type=\"java.lang.String\" />" +
//           		"<SimpleObject name=\"element\" value=\"Three.Two\" type=\"java.lang.String\" />" +
//           		"<JAVA:Array type=\"int\" size=\"3\">" +
//           		  "<JAVA:Element index=\"0\" value=\"1\" />" +
//           		  "<JAVA:Element index=\"1\" value=\"2\" />" +
//           	    "<JAVA:Element index=\"2\" value=\"3\" />" +
//              "</JAVA:Array>" +
//            "</ComplexObject>" +
//          "</JAVA:Element>" +
//        "</JAVA:Iterator>" +
//        "<JAVA:Collection xmlns:JAVA=\"http://java.sun.com\" type=\"java.util.ArrayList\" size=\"6\">" +
//        	"<JAVA:Element index=\"0\" value=\"One\" />" +
//        	"<JAVA:Element index=\"2\">" +
//        		"<SimpleObject name=\"element\" value=\"Two\" type=\"java.lang.String\" />" +
//          "</JAVA:Element>" +
//          "<JAVA:Element index=\"4\">" +
//          	"<ComplexObject>" +
//          		"<SimpleObject name=\"element\" value=\"Three.One\" type=\"java.lang.String\" />" +
//          		"<SimpleObject name=\"element\" value=\"Three.Two\" type=\"java.lang.String\" />" +
//          		"<JAVA:Array type=\"int\" size=\"3\">" +
//          			"<JAVA:Element index=\"0\" value=\"1\" />" +
//          			"<JAVA:Element index=\"1\" value=\"2\" />" +
//          			"<JAVA:Element index=\"2\" value=\"3\" />" +
//          		"</JAVA:Array>" +
//          	"</ComplexObject>" +
//          "</JAVA:Element>" +
//        "</JAVA:Collection>" +
//      "</CollectionObject>";
//
//    assertEquals("The xml of the serializer is invalid", anExpectedResult, aResult);
    assertNotNull(aResult);
  }

//  /**
//   *
//   */
//  public void testMapObject() {
//    MapObject anObject = new MapObject();
//    DynamicSerializer aSerializer = new DynamicSerializer();
//    String aResult = aSerializer.serialize(anObject);
//    String anExpectedResult =
//        "<MapObject>" +
//          "<JAVA:Map xmlns:JAVA=\"http://java.sun.com\" type=\"java.util.HashMap\" size=\"6\">" +
//            "<JAVA:Element index=\"0\" key=\"2\">" +
//              "<JAVA:Value>" +
//                "<SimpleObject name=\"element\" value=\"Two\" type=\"java.lang.String\" />" +
//              "</JAVA:Value>" +
//            "</JAVA:Element>" +
//            "<JAVA:Element index=\"1\" key=\"null3\" />" +
//            "<JAVA:Element index=\"2\" key=\"null1\" />" +
//            "<JAVA:Element index=\"3\">" +
//              "<JAVA:Key>" +
//                "<SimpleObject name=\"key\" value=\"Three\" type=\"java.lang.String\" />" +
//              "</JAVA:Key>" +
//              "<JAVA:Value>" +
//                "<ComplexObject><SimpleObject name=\"element\" value=\"Three.One\" type=\"java.lang.String\" /><SimpleObject name=\"element\" value=\"Three.Two\" type=\"java.lang.String\" /><JAVA:Array type=\"int\" size=\"3\"><JAVA:Element index=\"0\" value=\"1\" /><JAVA:Element index=\"1\" value=\"2\" /><JAVA:Element index=\"2\" value=\"3\" /></JAVA:Array></ComplexObject>" +
//              "</JAVA:Value>" +
//            "</JAVA:Element>" +
//            "<JAVA:Element index=\"4\" key=\"null2\" />" +
//            "<JAVA:Element index=\"5\" key=\"One\" value=\"One\" />" +
//          "</JAVA:Map>" +
//          "<JAVA:Map xmlns:JAVA=\"http://java.sun.com\" type=\"java.util.Hashtable\" size=\"0\" />" +
//        "</MapObject>";
//    assertEquals("The xml of the serializer is invalid", anExpectedResult, aResult);
//  }


  /**
   *
   */
  public void testArrayObject() {
    ArrayObject anObject = new ArrayObject();
    DynamicSerializer aSerializer = new DynamicSerializer();
    String aResult = aSerializer.serialize(anObject);
    String anExpectedResult =
        "<ArrayObject>" +
          "<JAVA:Array xmlns:JAVA=\"http://java.sun.com\" type=\"[Ljava.lang.String;\" size=\"3\">" +
            "<JAVA:Element index=\"0\">" +
              "<JAVA:Array type=\"java.lang.String\" size=\"3\">" +
                "<JAVA:Element index=\"0\" value=\"A\" />" +
                "<JAVA:Element index=\"1\" value=\"B\" />" +
                "<JAVA:Element index=\"2\" value=\"C\" />" +
              "</JAVA:Array>" +
            "</JAVA:Element>" +
            "<JAVA:Element index=\"1\">" +
              "<JAVA:Array type=\"java.lang.String\" size=\"3\">" +
                "<JAVA:Element index=\"0\" value=\"1\" />" +
                "<JAVA:Element index=\"1\" value=\"2\" />" +
                "<JAVA:Element index=\"2\" value=\"3\" />" +
              "</JAVA:Array>" +
            "</JAVA:Element>" +
            "<JAVA:Element index=\"2\">" +
              "<JAVA:Array type=\"java.lang.String\" size=\"3\">" +
                "<JAVA:Element index=\"0\" value=\"i\" />" +
                "<JAVA:Element index=\"1\" value=\"ii\" />" +
                "<JAVA:Element index=\"2\" value=\"iii\" />" +
              "</JAVA:Array>" +
            "</JAVA:Element>" +
          "</JAVA:Array>" +
        "</ArrayObject>";
    assertEquals("The xml of the serializer is invalid", anExpectedResult, aResult);
  }
}
