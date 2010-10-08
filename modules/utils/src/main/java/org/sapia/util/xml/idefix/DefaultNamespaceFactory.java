package org.sapia.util.xml.idefix;

import org.sapia.util.xml.Namespace;

import java.lang.reflect.Method;


/**
 * This implementation of the <code>NamespaceFactoryIF</code> interface always
 * returns the default namespace instance define in the implemented interface.<p>
 *
 * This class is usefull when generating XML strings that does not use namespace
 * or when an hierarchy of namespace factories are used, this default implementation
 * can be use as a fallback.
 *
 * @author <a href="mailto:jc@sapia-oss.org">Jean-Cedric Desrochers</a>
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html" target="sapia-license">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class DefaultNamespaceFactory implements NamespaceFactoryIF {
  /**
   * Creates a new DefaultNamespaceFactory instance.
   */
  public DefaultNamespaceFactory() {
  }

  /**
   * Returns the default namespace using the attribute
   * <code>NamespaceFactoryIF.DEFAULT_NAMESPACE</code>.
   *
   * @param aClass The class for which to retrieve a namespace.
   * @return The default namespace.
   * @see NamespaceFactoryIF#DEFAULT_NAMESPACE
   */
  public Namespace getNamespaceFor(Class aClass) {
    return DEFAULT_NAMESPACE;
  }

  /**
   * Returns the default namespace using the attribute
   * <code>NamespaceFactoryIF.DEFAULT_NAMESPACE</code>.
   *
   * @param aMethod The method for which to retrieve a namespace.
   * @return The default namespace.
   * @see NamespaceFactoryIF#DEFAULT_NAMESPACE
   */
  public Namespace getNamespaceFor(Method aMethod) {
    return DEFAULT_NAMESPACE;
  }
}
