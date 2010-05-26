package org.sapia.util.xml.idefix;


// Import of Sun's JDK classes
// ---------------------------
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 *
 *
 * @author Jean-Cedric Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class DynamicSerializer {
  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  CLASS ATTRIBUTES  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  public static final String JAVA_NAMESPACE_URI    = "http://java.sun.com";
  public static final String JAVA_NAMESPACE_PREFIX = "JAVA";
  public static final String JAVA_ARRAY_ELEMENT_NAME      = "Array";
  public static final String JAVA_COLLECTION_ELEMENT_NAME = "Collection";
  public static final String JAVA_ITERATOR_ELEMENT_NAME   = "Iterator";
  public static final String JAVA_MAP_ELEMENT_NAME        = "Map";
  public static final String JAVA_ELEMENT_ELEMENT_NAME    = "Element";
  public static final String JAVA_KEY_ELEMENT_NAME        = "Key";
  public static final String JAVA_VALUE_ELEMENT_NAME      = "Value";
  public static final String JAVA_TYPE_ATTRIBUTE_NAME  = "type";
  public static final String JAVA_SIZE_ATTRIBUTE_NAME  = "size";
  public static final String JAVA_INDEX_ATTRIBUTE_NAME = "index";
  public static final String JAVA_KEY_ATTRIBUTE_NAME   = "key";
  public static final String JAVA_VALUE_ATTRIBUTE_NAME = "value";

  /** Defines an empty immutable list. */
  private static final List EMPTY_LIST = new ArrayList(0);

  /** Defines an empty Class array. */
  private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];

  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  public DynamicSerializer() {
  }

  /**
   * Transforms the object passed in into an xml string.
   *
   * @param anObject The object to serialize to xml.
   * @return A string representing the result xml.
   */
  public String serialize(Object anObject) {
    XmlBuffer anXmlBuffer = new XmlBuffer();
    serializeIter("Root", anObject, anXmlBuffer, new ArrayList());

    return anXmlBuffer.toString();
  }

  /**
   *
   */
  private void serializeIter(String anObjectName, Object anObject,
    XmlBuffer anXmlBuffer, List someObjects) {
    // Validate for circular references
    if (someObjects.contains(anObject)) {
      throw new IllegalStateException("Circular referenced objects detected: " +
        anObject);
    } else {
      someObjects.add(anObject);
    }

    if (isLeafType(anObject.getClass())) {
      encodeLeaf(anObjectName, anObject, anXmlBuffer);
    } else if (anObject.getClass().isArray()) {
      encodeArray(anObject, anXmlBuffer, someObjects);
    } else if (anObject instanceof Collection) {
      encodeCollection((Collection) anObject, anXmlBuffer, someObjects);
    } else if (anObject instanceof Map) {
      encodeMap((Map) anObject, anXmlBuffer, someObjects);
    } else if (anObject instanceof Iterator) {
      encodeIterator((Iterator) anObject, anXmlBuffer, someObjects);
    } else {
      encodeNode(anObject, anXmlBuffer, someObjects);
    }

    someObjects.remove(anObject);
  }

  /**
   *
   */
  private String getObjectName(Object anObject) {
    if (anObject == null) {
      return "";
    }

    Class  aClass = anObject.getClass();

    String aQualifiedClassName = aClass.getName();
    String aPackageName        = aClass.getPackage().getName();
    String aLocalClassName     = aQualifiedClassName.substring(aPackageName.length() +
        1);

    return aLocalClassName;
  }

  /**
   *
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

    String anAttributeName = firstToLower(aMethodName, aPrefixLength);

    return anAttributeName;
  }

  /**
   *
   */
  private String firstToLower(String aString, int aStartingIndex) {
    char[] newChars = new char[aString.length() - aStartingIndex];
    newChars[0] = Character.toLowerCase(aString.charAt(aStartingIndex));
    aString.getChars(aStartingIndex + 1, aString.length(), newChars, 1);

    return new String(newChars);
  }

  /**
   *
   */
  private String firstToUpper(String aString, int aStartingIndex) {
    char[] newChars = new char[aString.length() - aStartingIndex];
    newChars[0] = Character.toUpperCase(aString.charAt(aStartingIndex));
    aString.getChars(aStartingIndex + 1, aString.length(), newChars, 1);

    return new String(newChars);
  }

  /**
   *
   */
  private boolean isLeafType(Class aClass) {
    return (aClass.isPrimitive() || (aClass == Character.class) ||
    (aClass == String.class) || (aClass == Boolean.class) ||
    (aClass == Byte.class) || (aClass == Short.class) ||
    (aClass == Integer.class) || (aClass == Long.class) ||
    (aClass == Float.class) || (aClass == Double.class));
  }

  /**
   *
   */
  private void encodeLeaf(String aName, Object aValue, XmlBuffer anXmlBuffer) {
    String aStringValue = String.valueOf(aValue);
    anXmlBuffer.addAttribute(firstToLower(aName, 0), aStringValue);
  }

  /**
   *
   */
  private void encodeNode(Object anObject, XmlBuffer anXmlBuffer,
    List someObjects) {
    // Start the element
    String anElementName = getObjectName(anObject);
    anXmlBuffer.startElement(anElementName);

    // Walk though the object tree
    Method aMethod = null;

    try {
      List someMethods = getMatchingMethods(anObject);

      for (Iterator it = someMethods.iterator(); it.hasNext();) {
        aMethod = (Method) it.next();

        Object aMethodValue = aMethod.invoke(anObject, EMPTY_CLASS_ARRAY);

        if (aMethodValue != null) {
          String anObjectName = getObjectName(aMethod);
          serializeIter(anObjectName, aMethodValue, anXmlBuffer, someObjects);
        }
      }
    } catch (IllegalAccessException iae) {
      throw new org.sapia.util.CompositeRuntimeException(
        "Unable to extract the value of the method: " + aMethod, iae);
    } catch (InvocationTargetException ite) {
      throw new org.sapia.util.CompositeRuntimeException(
        "Error calling the method: " + aMethod, ite);
    }

    // End the element
    anXmlBuffer.endElement(anElementName);
  }

  /**
   *
   */
  private void encodeArray(Object anArray, XmlBuffer anXmlBuffer,
    List someObjects) {
    boolean isArrayOfLeafType = isLeafType(anArray.getClass().getComponentType());
    String  anArrayTypeName = anArray.getClass().getComponentType().getName();
    int     aLength         = Array.getLength(anArray);

    // Start Array element
    anXmlBuffer.addNamespace(JAVA_NAMESPACE_URI, JAVA_NAMESPACE_PREFIX)
               .startElement(JAVA_NAMESPACE_URI, JAVA_ARRAY_ELEMENT_NAME)
               .addAttribute(JAVA_TYPE_ATTRIBUTE_NAME, anArrayTypeName)
               .addAttribute(JAVA_SIZE_ATTRIBUTE_NAME, Integer.toString(aLength));

    for (int i = 0; i < aLength; i++) {
      Object anObject = Array.get(anArray, i);

      if (anObject != null) {
        // Start ArrayElement element
        anXmlBuffer.startElement(JAVA_NAMESPACE_URI, JAVA_ELEMENT_ELEMENT_NAME)
                   .addAttribute(JAVA_INDEX_ATTRIBUTE_NAME, Integer.toString(i));

        serializeIter(JAVA_VALUE_ATTRIBUTE_NAME, anObject, anXmlBuffer,
          someObjects);

        // Start ArrayElement element
        anXmlBuffer.endElement(JAVA_NAMESPACE_URI, JAVA_ELEMENT_ELEMENT_NAME);
      }
    }

    // End Array element
    anXmlBuffer.endElement(JAVA_NAMESPACE_URI, JAVA_ARRAY_ELEMENT_NAME)
               .removeNamespace(JAVA_NAMESPACE_URI);
  }

  /**
   *
   */
  private void encodeCollection(Collection aCollection, XmlBuffer anXmlBuffer,
    List someObjects) {
    String aCollectionTypeName = aCollection.getClass().getName();
    int    aCollectionLength = aCollection.size();

    // Start Collection element
    anXmlBuffer.addNamespace(JAVA_NAMESPACE_URI, JAVA_NAMESPACE_PREFIX)
               .startElement(JAVA_NAMESPACE_URI, JAVA_COLLECTION_ELEMENT_NAME)
               .addAttribute(JAVA_TYPE_ATTRIBUTE_NAME, aCollectionTypeName)
               .addAttribute(JAVA_SIZE_ATTRIBUTE_NAME,
      Integer.toString(aCollectionLength));

    int anIndex = 0;

    for (Iterator it = aCollection.iterator(); it.hasNext(); anIndex++) {
      Object anObject = it.next();

      if (anObject != null) {
        // Start Element element
        anXmlBuffer.startElement(JAVA_NAMESPACE_URI, JAVA_ELEMENT_ELEMENT_NAME)
                   .addAttribute(JAVA_INDEX_ATTRIBUTE_NAME,
          Integer.toString(anIndex));

        serializeIter(JAVA_VALUE_ATTRIBUTE_NAME, anObject, anXmlBuffer,
          someObjects);

        // Start Element element
        anXmlBuffer.endElement(JAVA_NAMESPACE_URI, JAVA_ELEMENT_ELEMENT_NAME);
      }
    }

    // End Collection element
    anXmlBuffer.endElement(JAVA_NAMESPACE_URI, JAVA_COLLECTION_ELEMENT_NAME)
               .removeNamespace(JAVA_NAMESPACE_URI);
  }

  /**
   *
   */
  private void encodeIterator(Iterator anIterator, XmlBuffer anXmlBuffer,
    List someObjects) {
    // Start Iterator element
    anXmlBuffer.addNamespace(JAVA_NAMESPACE_URI, JAVA_NAMESPACE_PREFIX)
               .startElement(JAVA_NAMESPACE_URI, JAVA_ITERATOR_ELEMENT_NAME);

    for (int anIndex = 0; anIterator.hasNext(); anIndex++) {
      Object anObject = anIterator.next();

      if (anObject != null) {
        // Start Element element
        anXmlBuffer.startElement(JAVA_NAMESPACE_URI, JAVA_ELEMENT_ELEMENT_NAME)
                   .addAttribute(JAVA_INDEX_ATTRIBUTE_NAME,
          Integer.toString(anIndex));

        serializeIter(JAVA_VALUE_ATTRIBUTE_NAME, anObject, anXmlBuffer,
          someObjects);

        // Start Element element
        anXmlBuffer.endElement(JAVA_NAMESPACE_URI, JAVA_ELEMENT_ELEMENT_NAME);
      }
    }

    // End Iterator element
    anXmlBuffer.endElement(JAVA_NAMESPACE_URI, JAVA_ITERATOR_ELEMENT_NAME)
               .removeNamespace(JAVA_NAMESPACE_URI);
  }

  /**
   *
   */
  private void encodeMap(Map aMap, XmlBuffer anXmlBuffer, List someObjects) {
    String aMapTypeName = aMap.getClass().getName();
    int    aMapLength = aMap.size();

    // Start Collection element
    anXmlBuffer.addNamespace(JAVA_NAMESPACE_URI, JAVA_NAMESPACE_PREFIX)
               .startElement(JAVA_NAMESPACE_URI, JAVA_MAP_ELEMENT_NAME)
               .addAttribute(JAVA_TYPE_ATTRIBUTE_NAME, aMapTypeName)
               .addAttribute(JAVA_SIZE_ATTRIBUTE_NAME,
      Integer.toString(aMapLength));

    int anIndex = 0;

    for (Iterator it = aMap.keySet().iterator(); it.hasNext(); anIndex++) {
      Object aKey   = it.next();
      Object aValue = aMap.get(aKey);

      // Start Element element
      anXmlBuffer.startElement(JAVA_NAMESPACE_URI, JAVA_ELEMENT_ELEMENT_NAME)
                 .addAttribute(JAVA_INDEX_ATTRIBUTE_NAME,
        Integer.toString(anIndex));

      // Encode the key
      if (isLeafType(aKey.getClass())) {
        serializeIter(JAVA_KEY_ATTRIBUTE_NAME, aKey, anXmlBuffer, someObjects);
      } else {
        anXmlBuffer.startElement(JAVA_NAMESPACE_URI, JAVA_KEY_ELEMENT_NAME);
        serializeIter(JAVA_KEY_ATTRIBUTE_NAME, aKey, anXmlBuffer, someObjects);
        anXmlBuffer.endElement(JAVA_NAMESPACE_URI, JAVA_KEY_ELEMENT_NAME);
      }

      // Encode the value
      if (aValue != null) {
        if (isLeafType(aValue.getClass())) {
          serializeIter(JAVA_VALUE_ATTRIBUTE_NAME, aValue, anXmlBuffer,
            someObjects);
        } else {
          anXmlBuffer.startElement(JAVA_NAMESPACE_URI, JAVA_VALUE_ELEMENT_NAME);
          serializeIter(JAVA_VALUE_ATTRIBUTE_NAME, aValue, anXmlBuffer,
            someObjects);
          anXmlBuffer.endElement(JAVA_NAMESPACE_URI, JAVA_VALUE_ELEMENT_NAME);
        }
      }

      // Start Element element
      anXmlBuffer.endElement(JAVA_NAMESPACE_URI, JAVA_ELEMENT_ELEMENT_NAME);
    }

    // End Map element
    anXmlBuffer.endElement(JAVA_NAMESPACE_URI, JAVA_MAP_ELEMENT_NAME)
               .removeNamespace(JAVA_NAMESPACE_URI);
  }
}
