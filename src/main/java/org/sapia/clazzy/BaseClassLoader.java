package org.sapia.clazzy;

import java.security.SecureClassLoader;

/**
 * This class overrides the <code>SecureClassLoader</code> class. It allows
 * bypassing the delegation model, by which child classloaders are supposed to
 * resolve classes by first delegating the lookup to their parent classloader.
 * <p>
 * Inheriting classes should override this class' <code>findClass(String)</code>
 * method to support looking up classes from different sources. Accordingly, 
 * inherinting classes should also override this class' resource-related methods, 
 * such as <code>findResource()</code>.
 * 
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class BaseClassLoader extends SecureClassLoader {

  private boolean _parentFirst = true;

  public BaseClassLoader() {
    super(Thread.currentThread().getContextClassLoader());
  }

  public BaseClassLoader(ClassLoader parent) {
    super(parent);
  }

  /**
   * @param parentFirst
   *          if <code>true</code>, indicates that this instance should ask
   *          its parent for the specified classes rather then looking them up
   *          itself (this is the delegation model suggested by Java's API).
   *          Defaults to <code>true</code>.
   */
  public void setParentFirst(boolean parentFirst) {
    _parentFirst = parentFirst;
  }
  
  /**
   * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
   */
  public synchronized Class loadClass(String name, boolean resolve)
      throws ClassNotFoundException {
    Class clazz = findLoadedClass(name);
    if(clazz == null) {
      if(_parentFirst) {
        clazz = loadClassFromParent(name);
        if(clazz != null) {
          if(resolve) {
            super.resolveClass(clazz);
          }
          return clazz;
        }
      }
      try {
        clazz = findClass(name);
      } catch(ClassNotFoundException e) {
        //noop;
      }
      if(clazz == null && !_parentFirst) {
        clazz = loadClassFromParent(name);
        if(clazz != null) {
          if(resolve) {
            super.resolveClass(clazz);
          }
          return clazz;
        }
      }
    }
    if(clazz == null) {
      throw new ClassNotFoundException(name);
    }
    if(resolve) {
      super.resolveClass(clazz);
    }
    return clazz;
  }

  private Class loadClassFromParent(String name) {
    ClassLoader parent = getParent();
    if(parent != null) {
      try {
        return parent.loadClass(name);
      } catch(ClassNotFoundException e) {
        return null;
      }
    }
    return null;
  }
}
