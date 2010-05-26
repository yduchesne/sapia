package org.sapia.util.xml.confix;

import java.lang.reflect.InvocationTargetException;

// Import of Sun's JDK classes
// ---------------------------
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * This class instantiates objects based on the name of XML elements, using these
 * names to dynamically find the class of the objects to instantiate.
 *
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ReflectionFactory implements ObjectFactoryIF {
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////  INSTANCE ATTRIBUTES  /////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /** The list of packages from which to use reflection. */
  private List _thePackages;

  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Creates a new ReflectionFactory instance with the arguments passed in.
   *
   * @param somePackages The package from which to use reflection.
   */
  public ReflectionFactory(String[] somePackages) {
    _thePackages = new ArrayList();

    for (int i = 0; i < somePackages.length; i++) {
      _thePackages.add(somePackages[i]);
    }
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////  STATIC METHODS  ////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Utility to change the first letter of the string passed in to uppercase.
   *
   * @return The new string with the first letter in uppercase.
   */
  public static String firstToUpper(String aString) {
    StringBuffer aBuffer = new StringBuffer();

    for (int i = 0; i < aString.length(); i++) {
      if (i == 0) {
        aBuffer.append(Character.toUpperCase(aString.charAt(i)));
      } else {
        aBuffer.append(aString.charAt(i));
      }
    }

    return aBuffer.toString();
  }

  /**
   * Invokes a method on the target object using the method prefix and the
   * element name passed in.
   *
   * @param aMethodPrefix The prefix of the method to call (create, add, ...).
   * @param anElementName The element name for which we have to invoke a method.
   * @param aTarget The target object on which the method invocation is done.
   * @return The result of the method invocation or null if the method call failed.
   */
  protected static Object invokeMethod(String aMethodPrefix,
    String anElementName, Object aTarget) {
    Object anObject = null;

    try {
      // Invoking the method on the parent object that starts with the prefix passed in
      Method aMethod = aTarget.getClass().getMethod(aMethodPrefix +
          anElementName, new Class[0]);

      anObject = aMethod.invoke(aTarget, new Object[0]);
    } catch (NoSuchMethodException nsme) {
    } catch (IllegalAccessException iae) {
    } catch (InvocationTargetException ite) {
    }

    return anObject;
  }
  
  protected static boolean invokeVoidMethod(Object aTarget, String elementName){
    if(aTarget == null){
      return false;
    }
    
    Method toInvoke = null;
    toInvoke = findVoidMethod(aTarget.getClass(), elementName);
   
    if(toInvoke == null){
      toInvoke = findVoidMethod(aTarget.getClass(), "set" + AbstractXMLProcessor.toMethodName(elementName));
    }    
    if(toInvoke == null){
      toInvoke = findVoidMethod(aTarget.getClass(), "add" + AbstractXMLProcessor.toMethodName(elementName));
    }
    if(toInvoke == null){
      return false;
    }
    try{
      toInvoke.invoke(aTarget, new Object[0]);
      return true;
    } catch (IllegalAccessException iae) {
      return false;
    } catch (InvocationTargetException ite) {
      return false;
    }
  }
  
  protected static Method findVoidMethod(Class owner, String name){
    try{
      Method m = owner.getMethod(name, new Class[0]);
      if(m.getReturnType().equals(void.class)){
        return m;
      }
      return null;
    }catch(NoSuchMethodException e){
      return null;
    }
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////  MUTATOR METHODS  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Adds the package passed in to this factory.
   *
   * @param aPackage The package to add.
   */
  public void addPackage(String aPackage) {
    _thePackages.add(aPackage);
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////  INTERACE IMPLEMENTATION  ///////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

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
    String anElementName, Object aParent) throws ObjectCreationException {
    Object anObject = null;
    
    if (aParent != null) {
      // Call the createXXX() method on the target object
      
      anObject = invokeMethod("create", firstToUpper(anElementName), aParent);

      if (anObject != null) {
        return CreationStatus.create(anObject).assigned(true);
      } else {
        // Call the addXXX() method on the target object
        anObject = invokeMethod("add", firstToUpper(anElementName), aParent);

        if (anObject != null) {
          return CreationStatus.create(anObject).assigned(true);
        } else {
          anObject = invokeMethod("set", firstToUpper(anElementName), aParent);

          if (anObject != null) {
            return CreationStatus.create(anObject).assigned(true);
          }
        }
      }
    }

    String  aLocalClassName = firstToUpper(AbstractXMLProcessor.formatElementName(
          anElementName));
    Class   clazz   = null;
    boolean isFound = false;

    for (Iterator it = _thePackages.iterator(); !isFound && it.hasNext();) {
      String aPackageName = (String) it.next();

      try {
        String aQualifiedClassName = new StringBuffer().append(aPackageName)
                                                       .append(".")
                                                       .append(aLocalClassName)
                                                       .toString();

        clazz     = Class.forName(aQualifiedClassName);
        isFound   = true;
      } catch (ClassNotFoundException cnfe) {
      }
    }

    try {
      if (clazz != null) {
        return CreationStatus.create(clazz.newInstance());
      } else {
        if(!invokeVoidMethod(aParent, anElementName)){
          throw new ObjectCreationException("Element not recognized: " +
              anElementName);
        }
        else{
          return CreationStatus.create(new NullObjectImpl()).assigned(true);
        }
      }
    } catch (IllegalAccessException iae) {
      String aMessage = "Security error creating an object for the element " +
        anElementName;

      throw new ObjectCreationException(aMessage, iae);
    } catch (InstantiationException ie) {
      String aMessage = "Instantiation error creating an object for the element " +
        anElementName;

      throw new ObjectCreationException(aMessage, ie);
    } catch (RuntimeException re) {
      String aMessage = "System error creating an object for the element " +
        anElementName;

      throw new ObjectCreationException(aMessage, re);
    }
  }
  
  public static class NullObjectImpl implements NullObject{}
}
