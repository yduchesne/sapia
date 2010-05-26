package org.sapia.util.xml.idefix.serializer;

import org.sapia.util.xml.Namespace;
import org.sapia.util.xml.idefix.NamespaceFactoryIF;
import org.sapia.util.xml.idefix.SerializationContext;
import org.sapia.util.xml.idefix.SerializationException;
import org.sapia.util.xml.idefix.SerializerIF;
import org.sapia.util.xml.idefix.SerializerNotFoundException;

import java.util.Iterator;


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
public class IteratorSerializer implements SerializerIF {
  /**
   * Creates a new PrimitiveSerializer instance.
   */
  public IteratorSerializer() {
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
    Namespace aNamespace   = NamespaceFactoryIF.DEFAULT_NAMESPACE;
    String    anObjectName = SerializerHelper.getLocalClassName(anObject);
    serialize(anObject, aNamespace, anObjectName, aContext);
  }

  /**
   * Transforms the object passed in into an XML representation. This method is called when the
   * object to transform is nested inside another object.
   *
   * @param anIterator The iterator to serialize.
   * @param aNamespace The namespace of the object to serialize.
   * @param anObjectName The name of the object to serialize.
   * @param aContext The serialization context to use.
   * @exception SerializationException If an error occurs serializing the object.
   */
  public void serialize(Object anIterator, Namespace aNamespace,
    String anObjectName, SerializationContext aContext)
    throws SerializationException {
    Object anObject = null;

    try {
      // Extract all the objects of the collection
      for (Iterator it = (Iterator) anIterator; it.hasNext();) {
        anObject = it.next();

        // Get a serializer for the object
        SerializerIF aSerializer = aContext.getSerializerFactory()
                                           .getSerializer(anObject.getClass());

        // Serialize the object
        aSerializer.serialize(anObject, aContext);
      }
    } catch (SerializerNotFoundException snfe) {
      String aMessage = "Unable to find a serializer for the object: " +
        anObject;
      throw new SerializationException(aMessage, snfe);
    }
  }
}
