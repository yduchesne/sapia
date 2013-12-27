package org.sapia.util.xml.idefix;


//import org.apache.log4j.Logger;
import org.sapia.util.xml.Namespace;

import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.Map;


/**
 * This implementation of the <code>NamespaceFactoryIF</code> interface contains
 * a map of class names and a map of method names to which are associated the
 * namespace definition. Like the name of the class implies you can configure the
 * factory using patterns. The only pattern supported for now is the '*' (sorry
 * if you were expecting regular expressions... maybe in the next release).<p>
 *
 * To find namespace using a <code>java.lang.Class</code> object, this factory
 * uses the following resolution path:
 * <ol>
 *   <li>search for the full qualified name of the class using the method
 *       <code>getName()</code> on the class.
 *   </li>
 *   <li>search for the package of the class using the method
 *       <code>getPackage().getName()</code> on the class to which the string "<code>.*</code>"
 *       is added (i.e. for a class <code>org.foo.Bar</code>, the search string is
 *       <code>org.foo.*</code>).
 *   </li>
 * </ol><p>
 *
 * To find namespace using a <code>java.lang.reflect.Method</code> object, this factory
 * uses the following resolution path:
 * <ol>
 *   <li>search for the full qualified name of the method using the method
 *       <code>aMethod.getDeclaringClass().getName()</code> on the class. Note that this
 *       method will return the class where the method is <b>declared</b>. To that name
 *       the string "<code>#</code>" is added following by the name of the method (i.e.
 *       for a method <code>getName</code> on a class <code>org.foo.Bar</code>, the search
 *       string would be <code>org.foo.Bar#getName</code>).
 *   </li>
 *   <li>search for the full qualified name of the <b>declaring class</b> of the method using the
 *       method <code>getDeclaringClass().getName()</code>.
 *   </li>
 *   <li>search for the package of the <b>declaring class</b> of the method using the method
 *       <code>getDeclaringClass().getPackage().getName()</code> to which the string "<code>.*</code>"
 *       is added.
 *   </li>
 * </ol><p>
 *
 * @author <a href="mailto:jc@sapia-oss.org">Jean-Cedric Desrochers</a>
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">
 *     Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html" target="sapia-license">
 *        license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class PatternNamespaceFactory implements NamespaceFactoryIF {
  /** Defines the logger instance for this class. */

  /*private static final Logger _theLogger =
          Logger.getLogger(PatternNamespaceFactory.class);*/

  /** The map of namespace objects by their associated method, class or package name. */
  private Map _theNamespaces;

  /**
   * Creates a new PatternNamespaceFactory instance.
   */
  public PatternNamespaceFactory() {
    _theNamespaces = new HashMap();
  }

  /**
   * Adds the namespace passed for the class.
   *
   * @param aClass The class to which associate the namespace.
   * @param aNamespace The namespace to associate.
   */
  public void addNamespace(Class aClass, Namespace aNamespace) {
    _theNamespaces.put(aClass.getName(), aNamespace);
  }

  /**
   * Adds the namespace passed for the package.
   *
   * @param aPackage The package to which associate the namespace
   * @param aNamespace The namespace to associate.
   */
  public void addNamespace(Package aPackage, Namespace aNamespace) {
    _theNamespaces.put(aPackage.getName() + ".*", aNamespace);
  }

  /**
   * Adds the method passed for the package.
   *
   * @param aMethod The method to which associate the namespace
   * @param aNamespace The namespace to associate.
   */
  public void addNamespace(Method aMethod, Namespace aNamespace) {
    String aName = aMethod.getDeclaringClass().getName() + "#" +
      aMethod.getName();
    _theNamespaces.put(aName, aNamespace);
  }

  /**
   * Associates the namespace to the passed in name. To be found by the get methods
   * the name passed in must follow the resolution path define by this class.
   *
   * @param aName The name to which associate the namespace
   * @param aNamespace The namespace to associate.
   */
  public void addNamespace(String aName, Namespace aNamespace) {
    _theNamespaces.put(aName, aNamespace);
  }

  /**
   * Returns the namespace that should be associated with the XML representation
   * of the passed in class. If no association is found <code>null</code> is
   * return.
   *
   * @param aClass The class for which to retrieve a namespace.
   * @return The associated namespace or <code>null</code> is none is found.
   * @see #DEFAULT_NAMESPACE
   */
  public Namespace getNamespaceFor(Class aClass) {
    Namespace aNamespace = null;

    // First look for the full qualified class name
    /*if (_theLogger.isDebugEnabled()) {
      _theLogger.debug("Getting a namespace for the method: " + aClass);
      _theLogger.debug("    >>> Looking for the class name " + aClass.getName());
    }*/
    aNamespace = (Namespace) _theNamespaces.get(aClass.getName());

    if (aNamespace == null && aClass.getPackage() != null) {
      // If not found search using the package name
      String aPackageName = aClass.getPackage().getName() + ".*";

      /*if (_theLogger.isDebugEnabled()) {
        _theLogger.debug("    >>> Looking for the package name " + aPackageName);
      }*/
      aNamespace = (Namespace) _theNamespaces.get(aPackageName);
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
   * @see #DEFAULT_NAMESPACE
   */
  public Namespace getNamespaceFor(Method aMethod) {
    Namespace aNamespace = null;

    // First look for the full qualified name of the method
    String aName = aMethod.getDeclaringClass().getName() + "#" +
      aMethod.getName();

    /*if (_theLogger.isDebugEnabled()) {
      _theLogger.debug("Getting a namespace for the method: " + aMethod);
      _theLogger.debug("    >>> Looking for the method name " + aName);
    }*/
    aNamespace = (Namespace) _theNamespaces.get(aName);

    if (aNamespace == null) {
      // If not found search using the class name

      /*if (_theLogger.isDebugEnabled()) {
        _theLogger.debug("    >>> Looking for the class name " + aMethod.getDeclaringClass().getName());
      }*/
      aNamespace = (Namespace) _theNamespaces.get(aMethod.getDeclaringClass()
                                                         .getName());

      if (aNamespace == null) {
        // If not found search using the package name
        String aPackageName = aMethod.getDeclaringClass().getPackage().getName() +
          ".*";

        /*if (_theLogger.isDebugEnabled()) {
          _theLogger.debug("    >>> Looking for the package name " + aPackageName);
        }*/
        aNamespace = (Namespace) _theNamespaces.get(aPackageName);
      }
    }

    return aNamespace;
  }
}
