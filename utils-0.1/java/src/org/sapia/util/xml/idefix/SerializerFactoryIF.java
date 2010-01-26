package org.sapia.util.xml.idefix;


/**
 *
 *
 * @author <a href="mailto:jc@sapia-oss.org">Jean-Cedric Desrochers</a>
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html" target="sapia-license">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface SerializerFactoryIF {
  /**
   * Returns a serializer instance that knows how to convert an object of the type
   * represented by the class argument into an XML format.
   *
   * @param aClass The type of the object for which to get a serializer
   * @return The serializer instance.
   * @exception SerializerNotFoundException If no serialier was found for the class.
   */
  public SerializerIF getSerializer(Class aClass)
    throws SerializerNotFoundException;
}
