package org.sapia.util.xml.idefix;


/**
 * This serialization context contains different entities necessary to
 * serialize an object into an XML representation.
 *
 * @author <a href="mailto:jc@sapia-oss.org">Jean-Cedric Desrochers</a>
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html" target="sapia-license">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class SerializationContext {
  /** The serializer factory of this context. */
  private SerializerFactoryIF _theSerializerFactory;

  /** The namespace factory of this context. */
  private NamespaceFactoryIF _theNamespaceFactory;

  /** The XML buffer of this context. */
  private XmlBuffer _theXmlBuffer;
  
  /** The encoding style of this context. */
  private String _theEncodingStyle;

  /**
   * Creates a new SerializationContext instance with the passed in arguments.
   *
   * @param aSerializerFactory The serializer factory of this context.
   * @param aNamespaceFactory The namespace factory of this context.
   * @param aBuffer The XML buffer of this context.
   */
  public SerializationContext(SerializerFactoryIF aSerializerFactory,
    NamespaceFactoryIF aNamespaceFactory, XmlBuffer aBuffer, String anEncoding) {
    _theSerializerFactory   = aSerializerFactory;
    _theNamespaceFactory    = aNamespaceFactory;
    _theXmlBuffer           = aBuffer;
    _theEncodingStyle = anEncoding;
  }

  /**
   * Returns the serializer factory of this context.
   *
   * @return The serializer factory of this context.
   */
  public SerializerFactoryIF getSerializerFactory() {
    return _theSerializerFactory;
  }

  /**
   * Returns the namespace factory of this context.
   *
   * @return The namespace factory of this context.
   */
  public NamespaceFactoryIF getNamespaceFactory() {
    return _theNamespaceFactory;
  }

  /**
   * Returns the XML buffer of this serialization context.
   *
   * @return The XML buffer of this serialization context.
   */
  public XmlBuffer getXmlBuffer() {
    return _theXmlBuffer;
  }

  /**
   * Returns the encoding style of this serialization context.
   *
   * @return The encoding style of this serialization context.
   */
  public String getEncodingStyle() {
    return _theEncodingStyle;
  }
}
