package org.sapia.util.xml.idefix.serializer;


//import org.apache.log4j.Logger;
import org.sapia.util.xml.Namespace;
import org.sapia.util.xml.idefix.SerializationContext;
import org.sapia.util.xml.idefix.SerializationException;
import org.sapia.util.xml.idefix.SerializerIF;
import org.sapia.util.xml.idefix.SerializerNotFoundException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * this implementation of the <code>SerializerIF</code> interface uses reflection
 * to serialize the object passed to it.
 *
 * @author <a href="mailto:jc@sapia-oss.org">Jean-Cedric Desrochers</a>
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html" target="sapia-license">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class DynamicSerializer implements SerializerIF {
  /** Defines an empty immutable list. */
  private static final List EMPTY_LIST = new ArrayList(0);

  /** Defines an empty Class array. */
  private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];

  /** Define the logger instance of this class. */

  /*private static final Logger _theLogger =
          Logger.getLogger(DynamicSerializer.class);*/

  /**
   * Creates a new DynamicSerializer instance.
   */
  public DynamicSerializer() {
    super();
  }

  /**
   * Transforms the object passed in into an XML representation.
   *
   * @param anObject The object to serialize.
   * @param aContext The serialization context to use.
   * @exception SerializationException If an error occurs serializing the object.
   */
  public void serialize(Object anObject, SerializationContext aContext)
    throws SerializationException {
    // Define the name of the XML element
    String aRootElementName = getObjectName(anObject.getClass());

    // Define the namespace of the XML element
    Namespace aRootNamespace = aContext.getNamespaceFactory().getNamespaceFor(anObject.getClass());

    // Serialize the object with the extracted values
    serialize(anObject, aRootNamespace, aRootElementName, aContext);
  }

  /**
   * Transforms the object passed in into an XML representation. This method is called when the
   * object to transform is nested inside another object.
   *
   * @param anObject The object to serialize.
   * @param aNamespace The namespace of the object to serialize.
   * @param anObjectName The name of the object to serialize.
   * @param aContext The serialization context to use.
   * @exception SerializationException If an error occurs serializing the object.
   */
  public void serialize(Object anObject, Namespace aNamespace,
    String anObjectName, SerializationContext aContext)
    throws SerializationException {
    // Start the XML element
    aContext.getXmlBuffer().addNamespace(aNamespace.getURI(),
      aNamespace.getPrefix());
    aContext.getXmlBuffer().startElement(aNamespace.getURI(), anObjectName);

    Method aMethod = null;

    try {
      // Extract all the methods of the objects
      List someMethods = getMatchingMethods(anObject);

      // Walk though the object tree
      for (Iterator it = someMethods.iterator(); it.hasNext();) {
        aMethod = (Method) it.next();

        // Extracting the value of the method
        Object aMethodValue = aMethod.invoke(anObject, EMPTY_CLASS_ARRAY);

        if (aMethodValue != null) {
          // Define the object name
          String aMethodName = getObjectName(aMethod);

          // Define the namespace of the XML element
          Namespace aMethodNamespace = aContext.getNamespaceFactory()
                                               .getNamespaceFor(aMethodValue.getClass());

          // Get a serializer for the object
          SerializerIF aSerializer = aContext.getSerializerFactory()
                                             .getSerializer(aMethodValue.getClass());

          // Serialize the object
          aSerializer.serialize(aMethodValue, aMethodNamespace, aMethodName,
            aContext);
        }
      }
    } catch (IllegalAccessException iae) {
      String aMessage = "Unable to extract the value of the method: " +
        aMethod;

      //_theLogger.error(aMessage, iae);
      throw new SerializationException(aMessage, iae);
    } catch (InvocationTargetException ite) {
      String aMessage = "Error calling the method: " + aMethod;

      //_theLogger.error(aMessage, ite);
      throw new SerializationException(aMessage, ite);
    } catch (SerializerNotFoundException snfe) {
      String aMessage = "Unable to found a serializer for the method: " +
        aMethod;

      //_theLogger.error(aMessage, snfe);
      throw new SerializationException(aMessage, snfe);
    }

    // End the XML element
    aContext.getXmlBuffer().endElement(aNamespace.getURI(), anObjectName);
    aContext.getXmlBuffer().removeNamespace(aNamespace.getURI());
  }

  /**
   * Returns the list of methods of the object passed in that should be
   * introspected to perform the serialization of the object.
   *
   * @param anObject The object from which to retrieve the methods.
   * @return A list of <code>Method</code> objects.
   */
  private List getMatchingMethods(Object anObject) {
    if (anObject == null) {
      return EMPTY_LIST;
    }

    Class     aClass              = anObject.getClass();
    Method[]  allMethods          = aClass.getMethods(); // get all public methods
    ArrayList someMatchingMethods = new ArrayList(allMethods.length);

    for (int i = 0; i < allMethods.length; i++) {
      Method aMethod = allMethods[i];

      if ((aMethod.getName().startsWith("get") || // starts with 'get'
            aMethod.getName().startsWith("is")) && //    or 'is'
            !aMethod.getName().equals("getClass") && // the method is not getClass()
            !Modifier.isStatic(aMethod.getModifiers()) && // not static
            (aMethod.getParameterTypes().length == 0) && // has no parameter
            (aMethod.getReturnType() != Void.TYPE)) { // do not return void
        someMatchingMethods.add(aMethod);
      }
    }

    return someMatchingMethods;
  }

  /**
   *
   * @param aClass
   * @return
   */
  private String getObjectName(Class aClass) {
    String aQualifiedClassName = aClass.getName();
    String aPackageName    = aClass.getPackage().getName();
    String aLocalClassName = aQualifiedClassName.substring(aPackageName.length() +
        1);

    return aLocalClassName;
  }

  /**
   *
   * @param aMethod
   * @return
   */
  private String getObjectName(Method aMethod) {
    int    aPrefixLength = 0;
    String aMethodName = aMethod.getName();

    if (aMethodName.startsWith("get")) {
      aPrefixLength = 3;
    } else if (aMethodName.startsWith("is")) {
      aPrefixLength = 2;
    } else {
      throw new IllegalArgumentException(
        "The method name does not starts with \'get\' or \'is\'");
    }

    String anObjectName = SerializerHelper.firstToUpperFromIndex(aMethodName,
        aPrefixLength);

    return anObjectName;
  }
}
