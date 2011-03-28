package org.sapia.soto.me.xml;


/**
 * This interface specifies the behavior of factories that create objects out of XML content.
 *
 * @author Jean-Cedric Desrochers
 */
public interface ObjectFactory {

  /**
   * Creates an object for the element passed in.
   *
   * @param aPrefix The namespace prefix of the element.
   * @param aNamespaceURI The namespace URI of the element.
   * @param anElementName The element name for wich to create an object.
   * @param aParent The parent object of the object to create.
   * @return The created object.
   * @exception ObjectCreationException If an error occurs creating the object.
   */
  public Object newObjectFor(String aPrefix, String aNamespaceURI, 
          String anElementName, Object aParent) throws ObjectCreationException;
}
