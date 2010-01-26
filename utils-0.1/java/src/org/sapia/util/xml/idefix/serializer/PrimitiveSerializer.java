package org.sapia.util.xml.idefix.serializer;

import org.sapia.util.xml.Namespace;
import org.sapia.util.xml.idefix.IdefixProcessorIF;
import org.sapia.util.xml.idefix.SerializationContext;
import org.sapia.util.xml.idefix.SerializationException;
import org.sapia.util.xml.idefix.SerializerIF;


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
public class PrimitiveSerializer implements SerializerIF {
  /**
   * Creates a new PrimitiveSerializer instance.
   *
   *
   */
  public PrimitiveSerializer() {
    super();
  }

  /**
   * Transforms the object passed in into an XML representation. This method is called when the
   * object to transform is represents the root element of the XML document to create.
   *
   * @param anObject The object to serialize.
   * @param aContext The serialization context to use.
   * @exception SerializationException If an error occurs serializing the object.
   */
  public void serialize(Object anObject, SerializationContext aContext)
    throws SerializationException {
    if (anObject != null) {
      // Extract the value of the object
      String aValue = anObject.toString();
  
      // Add the value as the content of the current element
      aContext.getXmlBuffer().addContent(aValue);
    }
  }

  /**
   * Transforms the object passed in into an XML representation. This method is called when the
   * object to transform is nested inside another object.
   *
   * @param anObject The object to serialize.
   * @param aNamespace The namespace of the object to serialize.
   * @param anObjectName The name of the object to serialize.
   * @param aContext The serialization context to use.
   * @exception SerializationException If an error occurs serializing the object.
   */
  public void serialize(Object anObject, Namespace aNamespace,
    String anObjectName, SerializationContext aContext) throws SerializationException {
    if (anObject != null) {
      // Extract the value of the object
      String aValue = anObject.toString();
  
      // Modify the case of the object name
      String aName = SerializerHelper.firstToLowerFromIndex(anObjectName, 0);
  
      // Generated the XML attribute according to the encoding style
      if (aContext.getEncodingStyle() == IdefixProcessorIF.ENCODING_INLINE) {
        SerializerHelper.attributeEncodeInline(aContext.getXmlBuffer(), aNamespace, aName, aValue);
      } else if (aContext.getEncodingStyle() == IdefixProcessorIF.ENCODING_SOAP) {
        SerializerHelper.attributeEncodeSoap(aContext.getXmlBuffer(), aNamespace, aName, aValue);
      } else {
        throw new IllegalArgumentException("The XML encoding style is ivalid: " + aContext.getEncodingStyle());
      }
    }
  }
}
