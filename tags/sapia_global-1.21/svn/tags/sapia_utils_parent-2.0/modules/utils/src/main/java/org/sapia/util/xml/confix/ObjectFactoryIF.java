package org.sapia.util.xml.confix;


/**
 * This interface specifies the behavior of factories that create
 * objects out of XML content.
 *
 * @author  Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface ObjectFactoryIF {
  /**
   * Creates an object for the element passed in.
   *
   * @param aPrefix The namespace prefix of the element.
   * @param aNamespaceURI The namespace URI of the element.
   * @param anElementName The element name for wich to create an object.
   * @param aParent The parent object of the object to create.
   * @exception ObjectCreationException If an error occurs creating the object.
   */
  public CreationStatus newObjectFor(String aPrefix, String aNamespaceURI,
    String anElementName, Object aParent) throws ObjectCreationException;
}
