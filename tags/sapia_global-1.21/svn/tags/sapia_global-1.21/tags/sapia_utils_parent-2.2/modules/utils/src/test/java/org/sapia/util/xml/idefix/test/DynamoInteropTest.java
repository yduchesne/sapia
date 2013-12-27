package org.sapia.util.xml.idefix.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;
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
public class DynamoInteropTest extends TestCase {

  public static final String SOAP_NAMESPACE_URI = "http://schemas.xmlsoap.org/soap/envelope/";
  public static final String SOAP_NAMESPACE_PREFIX = "SOAP-ENV";
  public static final String DYNAMO_NAMESPACE_URI = "http://schemas.sapia.org/dynamo/interoperability/";
  public static final String DYNAMO_NAMESPACE_PREFIX = "DYN-IOP";

  public static void main(String[] args) {
    TestRunner.run(DynamoInteropTest.class);
  }

  public DynamoInteropTest(String aName) {
    super(aName);
  }


  public void testPollRequest() throws Exception {
    XmlBuffer aBuffer = createXmlBufferForRequest();
    aBuffer.startElement(SOAP_NAMESPACE_URI, "Body").
              startElement(DYNAMO_NAMESPACE_URI, "Poll").
                startElement(DYNAMO_NAMESPACE_URI, "commandId").addContent("675432").endElement(DYNAMO_NAMESPACE_URI, "commandId").
              endElement(DYNAMO_NAMESPACE_URI, "Poll").
            endElement(SOAP_NAMESPACE_URI, "Body").
          endElement(SOAP_NAMESPACE_URI, "Envelope");

    String aBody =
        "<DYN-IOP:Poll xmlns:DYN-IOP=\"http://schemas.sapia.org/dynamo/interoperability/\">" +
          "<DYN-IOP:commandId>675432</DYN-IOP:commandId>" +
        "</DYN-IOP:Poll>" ;
    assertRequestXmlBuffer(aBuffer, aBody);
  }


  public void testStatusRequest() throws Exception {
    XmlBuffer aBuffer = createXmlBufferForRequest();
    aBuffer.startElement(SOAP_NAMESPACE_URI, "Body").
              startElement(DYNAMO_NAMESPACE_URI, "Status").
              addAttribute(SOAP_NAMESPACE_URI, "encodingStyle", "http://schemas.sapia.org/dynamo/encoding/").
                startElement(DYNAMO_NAMESPACE_URI, "commandId").addContent("675433").endElement(DYNAMO_NAMESPACE_URI, "commandId").
                startElement(DYNAMO_NAMESPACE_URI, "topic").
                  addAttribute("name", "someTopic").
                  startElement(DYNAMO_NAMESPACE_URI, "item").
                    addAttribute("name", "item1").addAttribute("value", "item1_value").
                  endElement(DYNAMO_NAMESPACE_URI, "item").
                  startElement(DYNAMO_NAMESPACE_URI, "item").
                    addAttribute("name", "item2").addAttribute("value", "item2_value").
                  endElement(DYNAMO_NAMESPACE_URI, "item").
                endElement(DYNAMO_NAMESPACE_URI, "topic").
                startElement(DYNAMO_NAMESPACE_URI, "topic").
                  addAttribute("name", "someOtherTopic").
                  startElement(DYNAMO_NAMESPACE_URI, "item").
                    addAttribute("name", "item1").addAttribute("value", "item1_value").
                  endElement(DYNAMO_NAMESPACE_URI, "item").
                  startElement(DYNAMO_NAMESPACE_URI, "item").
                    addAttribute("name", "item2").addAttribute("value", "item2_value").
                  endElement(DYNAMO_NAMESPACE_URI, "item").
                endElement(DYNAMO_NAMESPACE_URI, "topic").
              endElement(DYNAMO_NAMESPACE_URI, "Status").
            endElement(SOAP_NAMESPACE_URI, "Body").
          endElement(SOAP_NAMESPACE_URI, "Envelope");

    String aBody =
        "<DYN-IOP:Status xmlns:DYN-IOP=\"http://schemas.sapia.org/dynamo/interoperability/\"" +
        " SOAP-ENV:encodingStyle=\"http://schemas.sapia.org/dynamo/encoding/\">" +
          "<DYN-IOP:commandId>675433</DYN-IOP:commandId>" +
              "<DYN-IOP:topic name=\"someTopic\">" +
                "<DYN-IOP:item name=\"item1\" value=\"item1_value\" />" +
                "<DYN-IOP:item name=\"item2\" value=\"item2_value\" />" +
            "</DYN-IOP:topic>" +
            "<DYN-IOP:topic name=\"someOtherTopic\">" +
                "<DYN-IOP:item name=\"item1\" value=\"item1_value\" />" +
                "<DYN-IOP:item name=\"item2\" value=\"item2_value\" />" +
            "</DYN-IOP:topic>" +
      "</DYN-IOP:Status>" ;
    assertRequestXmlBuffer(aBuffer, aBody);
  }


  public void testConfirmShutdownRequest() throws Exception {
    XmlBuffer aBuffer = createXmlBufferForRequest();
    aBuffer.startElement(SOAP_NAMESPACE_URI, "Body").
              startElement(DYNAMO_NAMESPACE_URI, "ConfirmShutdown").
                startElement(DYNAMO_NAMESPACE_URI, "commandId").addContent("675434").endElement(DYNAMO_NAMESPACE_URI, "commandId").
              endElement(DYNAMO_NAMESPACE_URI, "ConfirmShutdown").
            endElement(SOAP_NAMESPACE_URI, "Body").
          endElement(SOAP_NAMESPACE_URI, "Envelope");

    String aBody =
        "<DYN-IOP:ConfirmShutdown xmlns:DYN-IOP=\"http://schemas.sapia.org/dynamo/interoperability/\">" +
          "<DYN-IOP:commandId>675434</DYN-IOP:commandId>" +
        "</DYN-IOP:ConfirmShutdown>" ;
    assertRequestXmlBuffer(aBuffer, aBody);
  }


  public void testAckResponse() throws Exception {
    XmlBuffer aBuffer = createXmlBufferForResponse();
    aBuffer.startElement(SOAP_NAMESPACE_URI, "Body").
              startElement(DYNAMO_NAMESPACE_URI, "Ack").endElement(DYNAMO_NAMESPACE_URI, "Ack").
            endElement(SOAP_NAMESPACE_URI, "Body").
          endElement(SOAP_NAMESPACE_URI, "Envelope");

    String aBody =
        "<DYN-IOP:Ack xmlns:DYN-IOP=\"http://schemas.sapia.org/dynamo/interoperability/\" />";
    assertResponseXmlBuffer(aBuffer, aBody);
  }


  public void testShutdownResponse() throws Exception {
    XmlBuffer aBuffer = createXmlBufferForResponse();
    aBuffer.startElement(SOAP_NAMESPACE_URI, "Body").
              startElement(DYNAMO_NAMESPACE_URI, "Shutdown").
                startElement(DYNAMO_NAMESPACE_URI, "commandId").addContent("1234").endElement(DYNAMO_NAMESPACE_URI, "commandId").
              endElement(DYNAMO_NAMESPACE_URI, "Shutdown").
            endElement(SOAP_NAMESPACE_URI, "Body").
          endElement(SOAP_NAMESPACE_URI, "Envelope");

    String aBody =
        "<DYN-IOP:Shutdown xmlns:DYN-IOP=\"http://schemas.sapia.org/dynamo/interoperability/\">" +
          "<DYN-IOP:commandId>1234</DYN-IOP:commandId>" +
        "</DYN-IOP:Shutdown>";
    assertResponseXmlBuffer(aBuffer, aBody);
  }


  public void testSOAPFaultResponse() throws Exception {
    XmlBuffer aBuffer = createXmlBufferForResponse();
    aBuffer.startElement(SOAP_NAMESPACE_URI, "Body").
              startElement(SOAP_NAMESPACE_URI, "Fault").
                startElement("faultcode").addContent("... some code ...").endElement("faultcode").
                startElement("faultactor").addContent("... some actor ...").endElement("faultactor").
                startElement("faultstring").addContent("... some message ...").endElement("faultstring").
                startElement("detail").addContent("... some details ...").endElement("detail").
              endElement(SOAP_NAMESPACE_URI, "Fault").
            endElement(SOAP_NAMESPACE_URI, "Body").
          endElement(SOAP_NAMESPACE_URI, "Envelope");

    String aBody =
        "<SOAP-ENV:Fault>" +
          "<faultcode>... some code ...</faultcode>" +
          "<faultactor>... some actor ...</faultactor>" +
          "<faultstring>... some message ...</faultstring>" +
          "<detail>... some details ...</detail>" +
        "</SOAP-ENV:Fault>";
    assertResponseXmlBuffer(aBuffer, aBody);
  }


  private XmlBuffer createXmlBufferForRequest() {
    XmlBuffer aBuffer = new XmlBuffer(true).
            addNamespace(SOAP_NAMESPACE_URI, SOAP_NAMESPACE_PREFIX).
            addNamespace(DYNAMO_NAMESPACE_URI, DYNAMO_NAMESPACE_PREFIX);

    aBuffer.startElement(SOAP_NAMESPACE_URI, "Envelope").
              addAttribute(SOAP_NAMESPACE_URI, "encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/").
              startElement(SOAP_NAMESPACE_URI, "Header").
                startElement(DYNAMO_NAMESPACE_URI, "Process").
                  startElement(DYNAMO_NAMESPACE_URI, "dynPid").addContent("2045").endElement(DYNAMO_NAMESPACE_URI, "dynPid").
                  startElement(DYNAMO_NAMESPACE_URI, "requestId").addContent("134").endElement(DYNAMO_NAMESPACE_URI, "requestId").
                endElement(DYNAMO_NAMESPACE_URI, "Process").
              endElement(SOAP_NAMESPACE_URI, "Header");
    return aBuffer;
  }

  private void assertRequestXmlBuffer(XmlBuffer aBuffer, String aBody) {
    String anExpectedValue =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
        "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
        " SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
          "<SOAP-ENV:Header>" +
            "<DYN-IOP:Process xmlns:DYN-IOP=\"http://schemas.sapia.org/dynamo/interoperability/\">" +
              "<DYN-IOP:dynPid>2045</DYN-IOP:dynPid>" +
              "<DYN-IOP:requestId>134</DYN-IOP:requestId>" +
            "</DYN-IOP:Process>" +
          "</SOAP-ENV:Header>" +
          "<SOAP-ENV:Body>" + aBody + "</SOAP-ENV:Body>" +
        "</SOAP-ENV:Envelope>";

    assertEquals("The request xml buffer is not valid", anExpectedValue, aBuffer.toString());
  }

  private XmlBuffer createXmlBufferForResponse() {
    XmlBuffer aBuffer = new XmlBuffer(true).
            addNamespace(SOAP_NAMESPACE_URI, SOAP_NAMESPACE_PREFIX).
            addNamespace(DYNAMO_NAMESPACE_URI, DYNAMO_NAMESPACE_PREFIX);

    aBuffer.startElement(SOAP_NAMESPACE_URI, "Envelope").
              addAttribute(SOAP_NAMESPACE_URI, "encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/").
              startElement(SOAP_NAMESPACE_URI, "Header").
                startElement(DYNAMO_NAMESPACE_URI, "Server").
                  startElement(DYNAMO_NAMESPACE_URI, "requestId").addContent("134").endElement(DYNAMO_NAMESPACE_URI, "requestId").
                  startElement(DYNAMO_NAMESPACE_URI, "processingTime").addContent("250").endElement(DYNAMO_NAMESPACE_URI, "processingTime").
                endElement(DYNAMO_NAMESPACE_URI, "Server").
              endElement(SOAP_NAMESPACE_URI, "Header");
    return aBuffer;
  }

  private void assertResponseXmlBuffer(XmlBuffer aBuffer, String aBody) {
    String anExpectedValue =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
        "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
        " SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
          "<SOAP-ENV:Header>" +
            "<DYN-IOP:Server xmlns:DYN-IOP=\"http://schemas.sapia.org/dynamo/interoperability/\">" +
              "<DYN-IOP:requestId>134</DYN-IOP:requestId>" +
              "<DYN-IOP:processingTime>250</DYN-IOP:processingTime>" +
            "</DYN-IOP:Server>" +
          "</SOAP-ENV:Header>" +
          "<SOAP-ENV:Body>" + aBody + "</SOAP-ENV:Body>" +
        "</SOAP-ENV:Envelope>";

    assertEquals("The response xml buffer is not valid", anExpectedValue, aBuffer.toString());
  }

}

