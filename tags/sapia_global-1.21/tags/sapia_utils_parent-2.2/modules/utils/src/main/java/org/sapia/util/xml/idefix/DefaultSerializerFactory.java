package org.sapia.util.xml.idefix;

import org.sapia.util.xml.idefix.serializer.ArraySerializer;
import org.sapia.util.xml.idefix.serializer.CollectionSerializer;
import org.sapia.util.xml.idefix.serializer.DynamicSerializer;
import org.sapia.util.xml.idefix.serializer.IteratorSerializer;
import org.sapia.util.xml.idefix.serializer.PrimitiveSerializer;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


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
public class DefaultSerializerFactory implements SerializerFactoryIF {
  /** The map of serializaer by class. */
  private Map _theSerializersByClass;

  /**
   * Creates a new DefaultSerializerFactory instance.
   */
  public DefaultSerializerFactory() {
    _theSerializersByClass = new HashMap();

    _theSerializersByClass.put(boolean.class, new PrimitiveSerializer());
    _theSerializersByClass.put(Boolean.class, new PrimitiveSerializer());
    _theSerializersByClass.put(byte.class, new PrimitiveSerializer());
    _theSerializersByClass.put(Byte.class, new PrimitiveSerializer());
    _theSerializersByClass.put(short.class, new PrimitiveSerializer());
    _theSerializersByClass.put(Short.class, new PrimitiveSerializer());
    _theSerializersByClass.put(int.class, new PrimitiveSerializer());
    _theSerializersByClass.put(Integer.class, new PrimitiveSerializer());
    _theSerializersByClass.put(long.class, new PrimitiveSerializer());
    _theSerializersByClass.put(Long.class, new PrimitiveSerializer());
    _theSerializersByClass.put(float.class, new PrimitiveSerializer());
    _theSerializersByClass.put(Float.class, new PrimitiveSerializer());
    _theSerializersByClass.put(double.class, new PrimitiveSerializer());
    _theSerializersByClass.put(Double.class, new PrimitiveSerializer());
    _theSerializersByClass.put(char.class, new PrimitiveSerializer());
    _theSerializersByClass.put(Character.class, new PrimitiveSerializer());
    _theSerializersByClass.put(String.class, new PrimitiveSerializer());
    _theSerializersByClass.put(Date.class, new PrimitiveSerializer());
  }

  /**
   * Register the new serializer mapping to the class.
   *
   * @param aClass The class for which to register the serializer.
   * @param aSerializer The serializer to register.
   */
  public void registerSerializer(Class aClass, SerializerIF aSerializer) {
    _theSerializersByClass.put(aClass, aSerializer);
  }

  /**
   * Returns a serializer instance that knows how to convert an object of the type
   * represented by the class argument into an XML format.
   *
   * @param aClass The type of the object for which to get a serializer
   * @return The serializer instance.
   * @exception SerializerNotFoundException If no serialier was found for the class.
   */
  public SerializerIF getSerializer(Class aClass)
    throws SerializerNotFoundException {
    SerializerIF aSerializer = (SerializerIF) _theSerializersByClass.get(aClass);

    if (aSerializer == null) {
      if (aClass.isArray()) {
        aSerializer = new ArraySerializer();
      } else if (Collection.class.isAssignableFrom(aClass)) {
        aSerializer = new CollectionSerializer();
      } else if (Iterator.class.isAssignableFrom(aClass)) {
        aSerializer = new IteratorSerializer();
      } else {
        aSerializer = new DynamicSerializer();
      }

      _theSerializersByClass.put(aClass, aSerializer);
    }

    return aSerializer;
  }
}
