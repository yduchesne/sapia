package org.sapia.util.xml.idefix.serializer;

import org.sapia.util.xml.Namespace;
import org.sapia.util.xml.idefix.SerializationContext;
import org.sapia.util.xml.idefix.SerializationException;
import org.sapia.util.xml.idefix.SerializerIF;
import org.sapia.util.xml.idefix.SerializerNotFoundException;

import java.lang.reflect.Array;


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
public class ArraySerializer implements SerializerIF {
  /**
   * Creates a new PrimitiveSerializer instance.
   */
  public ArraySerializer() {
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
    throw new UnsupportedOperationException(
      "The array serializer can serialize a root element");
  }

  /**
   * Transforms the object passed in into an XML representation. This method is called when the
   * object to transform is nested inside another object.
   *
   * @param anArray The array to serialize.
   * @param aNamespace The namespace of the object to serialize.
   * @param anObjectName The name of the object to serialize.
   * @param aContext The serialization context to use.
   * @exception SerializationException If an error occurs serializing the object.
   */
  public void serialize(Object anArray, Namespace aNamespace,
    String anObjectName, SerializationContext aContext)
    throws SerializationException {
    try {
      // Start the XML element
      aContext.getXmlBuffer().addNamespace(aNamespace.getURI(),
        aNamespace.getPrefix());
      aContext.getXmlBuffer().startElement(aNamespace.getURI(), anObjectName);

      // Get a serializer for the array
      SerializerIF aSerializer = aContext.getSerializerFactory().getSerializer(anArray.getClass()
                                                                                      .getComponentType());

      // Extract all the objects of the array
      int anArrayLength = Array.getLength(anArray);

      for (int i = 0; i < anArrayLength; i++) {
        Object anObject = Array.get(anArray, i);
        aSerializer.serialize(anObject, aContext);
      }

      // End the XML element
      aContext.getXmlBuffer().endElement(aNamespace.getURI(), anObjectName);
      aContext.getXmlBuffer().removeNamespace(aNamespace.getURI());
    } catch (SerializerNotFoundException snfe) {
      String aMessage = "Unable to found a serializer for the array: " +
        anArray;
      throw new SerializationException(aMessage, snfe);
    }
  }
}
