package org.sapia.util.xml.idefix;

import org.sapia.util.xml.Namespace;

import java.lang.reflect.Method;


/**
 * This interface allow the creation of <CODE>Namespace</CODE> objects.
 *
 * @author <a href="mailto:jc@sapia-oss.org">Jean-Cedric Desrochers</a>
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html" target="sapia-license">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface NamespaceFactoryIF {
  /** Defines a default namespace instance. */
  public static final Namespace DEFAULT_NAMESPACE = new Namespace("", "");

  /**
   * Returns the namespace that should be associated with the XML representation
   * of the passed in class. If no association is found <code>null</code> is
   * return.
   *
   * @param aClass The class for which to retrieve a namespace.
   * @return The associated namespace or <code>null</code> is none is found.
   * @see #DEFAULT_NAMESPACE
   */
  public Namespace getNamespaceFor(Class aClass);

  /**
   * Returns the namespace that should be associated with the XML representation
   * of the passed in method. If no association is found <code>null</code> is
   * return.
   *
   * @param aMethod The method for which to retrieve a namespace.
   * @return The associated namespace or <code>null</code> is none is found.
   * @see #DEFAULT_NAMESPACE
   */
  public Namespace getNamespaceFor(Method aMethod);
}
