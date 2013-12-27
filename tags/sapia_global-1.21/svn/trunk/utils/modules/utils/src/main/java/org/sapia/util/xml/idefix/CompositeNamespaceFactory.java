package org.sapia.util.xml.idefix;

import org.sapia.util.xml.Namespace;

import java.lang.reflect.Method;

import java.util.Iterator;
import java.util.LinkedList;


/**
 * This implementation of the <code>NamespaceFactoryIF</code> interface contains
 * a collection of namespace factories to which it delegates the method calls. The
 * order in which the delegate factories are added will define the internal hierarchy
 * of factories. That will directly affect the order in which the factories are called
 * when a <code>getNamespaceFor()</code> method is called on this compostie factory.<p>
 *
 * When such a call is made, this factory will forward the call to the first factory
 * of the hierarchy. If this root factory returns a <code>Namespace</code> it returns
 * the object. If <code>null</code> is return then the call will be forwarded to the
 * next factry untill a <code>Namespace<code> object is returned or untill it reaches
 * the last factoy of the chain.<p>
 *
 * Note that this factory is <b>not synchronzed</b> so if you add factories to it while
 * another thread is calling one of the <code>getNamespaceFor()</code> method, the result
 * can be... kind of weird!
 *
 * @author <a href="mailto:jc@sapia-oss.org">Jean-Cedric Desrochers</a>
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html" target="sapia-license">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class CompositeNamespaceFactory implements NamespaceFactoryIF {
  /** The list of delegate factories. */
  private LinkedList _theDelegates;

  /**
   * Creates a new CompositeNamespaceFactory instance.
   */
  public CompositeNamespaceFactory() {
    _theDelegates = new LinkedList();
  }

  /**
   * Register the namespace factory passed in as a delegate.
   *
   * @param aFactory The delegate factory to add.
   */
  public void registerNamespaceFactory(NamespaceFactoryIF aFactory) {
    _theDelegates.addLast(aFactory);
  }

  /**
   * Returns the namespace that should be associated with the XML representation
   * of the passed in class. If no association is found <code>null</code> is
   * return.
   *
   * @param aClass The class for which to retrieve a namespace.
   * @return The associated namespace or <code>null</code> is none is found.
   */
  public Namespace getNamespaceFor(Class aClass) {
    Namespace aNamespace = null;

    for (Iterator it = _theDelegates.iterator();
          it.hasNext() && (aNamespace == null);) {
      NamespaceFactoryIF aFactory = (NamespaceFactoryIF) it.next();
      aNamespace = aFactory.getNamespaceFor(aClass);
    }

    return aNamespace;
  }

  /**
   * Returns the namespace that should be associated with the XML representation
   * of the passed in method. If no association is found <code>null</code> is
   * return.
   *
   * @param aMethod The method for which to retrieve a namespace.
   * @return The associated namespace or <code>null</code> is none is found.
   */
  public Namespace getNamespaceFor(Method aMethod) {
    Namespace aNamespace = null;

    for (Iterator it = _theDelegates.iterator();
          it.hasNext() && (aNamespace == null);) {
      NamespaceFactoryIF aFactory = (NamespaceFactoryIF) it.next();
      aNamespace = aFactory.getNamespaceFor(aMethod);
    }

    return aNamespace;
  }
}
