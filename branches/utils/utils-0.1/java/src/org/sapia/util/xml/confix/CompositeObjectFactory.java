package org.sapia.util.xml.confix;


// Import of Sun's JDK classes
// ---------------------------
import java.util.HashMap;
import java.util.Map;


/**
 * This instance implements an <code>ObjectFactoryIF</code> that internally
 * maps child object factories to XML namepspace prefixes OR URIs.
 *
 * @author Jean-Cedric Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class CompositeObjectFactory implements ObjectFactoryIF {
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////  INSTANCE ATTRIBUTES  /////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /** The map of object factories identified by the namespace thei manage. */
  private Map _theFactories;

  /**
   * If true, indicates that factories are mapped to prefixes - else to the namespace URI.
   * Defaults to false.
   */
  private boolean _mapToNameSpacePrefix = false;

  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Creates a new CompositeObjectFactory instance.
   */
  public CompositeObjectFactory() {
    _theFactories = new HashMap();
  }

  /**
   * Indicates if internal factories are mapped to namespace URIs or
   * prefixes; if <code>true</code>, object factories are mapped to
   * prefixes. By default, an instance of this class maps object factories
   * to the URI.
   *
   * @param mapToPrefix if <code>true</code>, object factories are mapped to
   * prefixes.
   */
  public void setMapToPrefix(boolean mapToPrefix) {
    _mapToNameSpacePrefix = mapToPrefix;
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////  MUTATOR METHODS  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Registers the object factory passed in for the namespace URI passed in.
   *
   * @param aNamespaceURIorPrefix The namespace URI or prefix to associate with the factory.
   * @param aFactory The object factory to register.
   * @exception IllegalArgumentExcption If a factory is already registered for
   *            the passed in namespace URI.
   */
  public void registerFactory(String aNamespaceURIorPrefix,
    ObjectFactoryIF aFactory) {
    if (_theFactories.containsKey(aNamespaceURIorPrefix)) {
      throw new IllegalArgumentException(
        "An object factory is already registered for the namespace URI or prefix " +
        aNamespaceURIorPrefix);
    }

    _theFactories.put(aNamespaceURIorPrefix, aFactory);
  }

  /**
   * Returns the child object factory internally mapped to the given namespace or
   * prefix.
   *
   * @return an <code>ObjectFactoryIF</code>.
   * @param aNamespaceURIorPrefix a XML namepsace prefix or URI.
   * @throws IllegalArgumentException if no factory can be found for the given
   * namespace of prefix.
   */
  public ObjectFactoryIF getFactoryFor(String aNamespaceURIorPrefix)
    throws IllegalArgumentException {
    ObjectFactoryIF fac = (ObjectFactoryIF) _theFactories.get(aNamespaceURIorPrefix);

    if (fac == null) {
      throw new IllegalArgumentException(
        "No object factory registered for the namespace URI or prefix " +
        aNamespaceURIorPrefix);
    }

    return fac;
  }

  /**
   * Returns true if an object factory is internally mapped to the given
   * namespace or prefix.
   *
   * @return <code>true</code> if an object factory is internally mapped to the given
   * namespace or prefix.
   */
  public boolean containsObjectFactory(String aNamespaceURIorPrefix) {
    return _theFactories.get(aNamespaceURIorPrefix) != null;
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////  INTERFACE IMPLEMENTATION  ///////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Creates an object for the element passed in. This method in fact delegates
   * object creation logic to the factory that is mapped to the given namespace OR prefix.
   * Whether this method uses the namespace or prefix depends on the flag that was set
   * through this instance's <code>setMapToPrefix(...)</code> method.
   *
   * @param aPrefix The namespace prefix of the element.
   * @param aNamespaceURI The namespace URI of the element.
   * @param anElementName The element name for wich to create an object.
   * @param aParent The parent object of the object to create.
   * @see #setMapToPrefix(boolean)
   * @exception ObjectCreationException If an error occurs creating the object.
   */
  public CreationStatus newObjectFor(String aPrefix, String aNamespaceURI,
    String anElementName, Object aParent) throws ObjectCreationException {
    ObjectFactoryIF aFactory;

    if (_mapToNameSpacePrefix) {
      if ((aPrefix == null) || (aPrefix.length() == 0)) {
        throw new ObjectCreationException("Prefix no defined");
      }

      aFactory = (ObjectFactoryIF) _theFactories.get(aPrefix);
    } else {
      if ((aNamespaceURI == null) || (aNamespaceURI.length() == 0)) {
        throw new ObjectCreationException("Namespace no defined");
      }

      aFactory = (ObjectFactoryIF) _theFactories.get(aNamespaceURI);
    }

    if (aFactory == null) {
      throw new ObjectCreationException("No factory found for the namespace " +
        aNamespaceURI + "; element: " + anElementName);
    } else {
      return aFactory.newObjectFor(aPrefix, aNamespaceURI, anElementName,
        aParent);
    }
  }
}
