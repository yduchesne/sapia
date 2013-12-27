package org.sapia.util.xml.confix;


// Import of Sun's JDK classes
// ---------------------------
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * This class defines the behavior for generating an object graph from an
 * XML document.
 *
 * @author Yanick Duchesne
 * @author Jean-Cedric Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public abstract class AbstractXMLProcessor implements ConfixProcessorIF {
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////  INSTANCE ATTRIBUTES  /////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  
  /** The object factory of this processor. */
  private ObjectFactoryIF _theObjectFactory;
  
  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  
  /**
   * Creates a new AbstractXMLProcessor instance with the argument passed in.
   *
   * @param anObjectFactory The object factory of this processor.
   */
  public AbstractXMLProcessor(ObjectFactoryIF anObjectFactory) {
    _theObjectFactory = anObjectFactory;
  }
  
  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////  STATIC METHODS  ////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  
  /**
   * Assigns the given <code>child</code> object to the <code>parent</code> one.
   * This method searches for the method name based on the <code>elementName</code>
   * parameter, which is the name of an XML element. The method name must match
   * either the setXXX or addXXX pattern; for example, for an XML element named
   * 'elephant', the class of the <code>parent</code> object should contain either
   * the <code>setElephant</code> or <code>addElephant</code> method.
   *
   * @param aParent the object to <code>child</code> will be set or added
   * @param aChild the object to add to <code>parent</code>
   * @param anElementName the name of the XML element to which a addXXX or
   *        setXXX method should correspond in the parent object.
   * @throws ConfigurationException
   */
  protected static void assignToParent(Object aParent, Object aChild,
    String anElementName) throws ConfigurationException {
    try {
      String aMethodName = "set" +
        toMethodName(formatElementName(anElementName));
      invokeMethod(aMethodName, aParent, aChild);
    } catch (ConfigurationException ce) {
      String aMessage = "Error assigning the child object " + aChild +
        " with the setXXX method on the parent object " + aParent;
      throw new ConfigurationException(aMessage, ce);
    } catch (NoSuchMethodException nsme) {
      try {
        String aMethodName = "add" +
          toMethodName(formatElementName(anElementName));
        try{
          invokeMethod(aMethodName, aParent, aChild);
        }catch(NoSuchMethodException e){
          try{
            invokeSetterOrAdder(aParent, aChild);
          }catch(NoSuchMethodException e2){
            throw e;
          }
        }
      } catch (ConfigurationException ce) {
        String aMessage = "Error assigning the child object " + aChild +
          " with the addXXX method on the parent object " + aParent;
        throw new ConfigurationException(aMessage, ce);
      } catch (NoSuchMethodException nsme2) {
        // If the parent is an object wrapper, takes the wrappep object
        if (aParent instanceof ObjectWrapperIF) {
          aParent = ((ObjectWrapperIF) aParent).getWrappedObject();
          assignToParent(aParent, aChild, anElementName);
        }
        // If the parent is an object handler, delegate it the object
        else if (aParent instanceof ObjectHandlerIF) {
          ((ObjectHandlerIF) aParent).handleObject(anElementName, aChild);
        } else {
          String aMessage =
            "There's no setXXX or addXXX method to assign the object " +
            aChild + " on the parent object " + aParent;
          throw new ConfigurationException(aMessage);
        }
      }
    }
  }
  
  /**
   * Invokes the setXXXX/addXXXX method corresponding to the given attribute name.
   * The method is invoked on the target instance. The passed in value is
   * coerced to the type expected by the setter or adder method.
   *
   * @param anElementName the XML element name corresponding to the passed in target.
   * @param aTarget the instance on which to invoke the method.
   * @param anAttributeName the name of the attribute whose corresponding method
   * is called.
   * @param aValue the value to assign to the setter/adder method - the value is
   * coerced to the type expected by the method.
   */
  protected static void invokeSetter(String anElementName, Object aTarget,
    String anAttributeName, String aValue) throws ConfigurationException {
    try {
      String aMethodName = "set" +
        toMethodName(formatElementName(anAttributeName));
      invokeMethod(aMethodName, aTarget, aValue);
    } catch (ConfigurationException ce) {
      String aMessage =
        "Application error invoking the setXXX method to assign the value " +
        aValue + " for the attribute " + anAttributeName + " on the object " +
        aTarget;
      throw new ConfigurationException(aMessage, ce);
    } catch (NoSuchMethodException nsme) {
      try{
        String aMethodName = "add" + toMethodName(formatElementName(anAttributeName));
        invokeMethod(aMethodName, aTarget, aValue);
        // If the target object is an ObjectWrapperIF use the wrapped object
      }catch(NoSuchMethodException nsme2){
        if (aTarget instanceof ObjectWrapperIF) {
          aTarget = ((ObjectWrapperIF) aTarget).getWrappedObject();
          if(aTarget == null){
            throw new ConfigurationException("Wrapped object is null",  nsme);
          }
          invokeSetter(anElementName, aTarget, anAttributeName, aValue);
        }
        // If the target object is an ObjectHandlerIF, delegate it the value
        else if (aTarget instanceof ObjectHandlerIF) {
          ((ObjectHandlerIF) aTarget).handleObject(anAttributeName, aValue);
        }
        // Otherwise throw a configuration exception if it is not a null object
        else if (!(aTarget instanceof NullObject)) {
          String aMessage = "There's no setXXX method to assign the value " +
            aValue + " for the attribute/element " + anAttributeName +
            " on the object " + aTarget;
          throw new ConfigurationException(aMessage, nsme);
	}
      }
    }
  }
  
  /**
   * @param aTarget the <code>Object</code> on which to attempt the method calls.
   * @param aValue the <code>Object</code> to assign.
   * @return the return value of the method call.
   * @throws ConfigurationException if a problem occurs invoking the method.
   * @throws NoSuchMethodException if no adder or setter could be found.
   */
  protected static Object invokeSetterOrAdder(Object aTarget, Object aValue)
  throws ConfigurationException, NoSuchMethodException{
    String methodName = "set";
    try{
      return invokeMethod(methodName, aTarget, aValue);
    }catch(NoSuchMethodException e){
      methodName = "add";
      return invokeMethod(methodName, aTarget, aValue);
    }
  }
  
  /**
   * Invokes the method whose name is given on the provided target
   * instance.
   *
   * @param aMethodName the name of the method to invoke.
   * @param aTarget the instance on which to invoke the method.
   * @param aValue the value to pass to the method that will be called.
   */
  protected static Object invokeMethod(String aMethodName, Object aTarget,
    Object aValue) throws ConfigurationException, NoSuchMethodException {
    try {
      Method[] someMethods = aTarget.getClass().getMethods();
      
      for (int i = 0; i < someMethods.length; i++) {
        if (someMethods[i].getName().equals(aMethodName) &&
          (someMethods[i].getParameterTypes().length == 1)) {
          Method aMethod = someMethods[i];
          
          Class  aParamType = aMethod.getParameterTypes()[0];
          
          if (aParamType.isAssignableFrom(aValue.getClass())) {
            return aMethod.invoke(aTarget, new Object[] { aValue });
          }
          // If the method parameter is an enum
          else if (aParamType.isEnum() && aValue instanceof String) {
            return aMethod.invoke(aTarget, new Object[] {
                    getEnumValueOf(aParamType, (String) aValue)});
//                    Enum.valueOf(aParamType, (String) aValue)});
          }
          // If the method parameter is a boolean type
          else if ((aParamType.equals(boolean.class) ||
            aParamType.equals(Boolean.class)) && aValue instanceof String) {
            String  aStringValue = (String) aValue;
            Boolean aBoolean = new Boolean(aStringValue.equalsIgnoreCase("true") ||
              aStringValue.equalsIgnoreCase("yes"));
            
            return aMethod.invoke(aTarget, new Object[] { aBoolean });
          } else if (aParamType.equals(boolean.class) &&
            aValue instanceof Boolean) {
            return aMethod.invoke(aTarget, new Object[] { aValue });
          }
          // If the method parameter is byte type
          else if ((aParamType.equals(byte.class) ||
            aParamType.equals(Byte.class)) && aValue instanceof String) {
            String aStringValue = (String) aValue;
            Byte   aByte = new Byte(Byte.parseByte(aStringValue));
            
            return aMethod.invoke(aTarget, new Object[] { aByte });
          } else if (aParamType.equals(byte.class) && aValue instanceof Byte) {
            return aMethod.invoke(aTarget, new Object[] { aValue });
          }
          // If the method parameter is a short type
          else if ((aParamType.equals(short.class) ||
            aParamType.equals(Short.class)) && aValue instanceof String) {
            String aStringValue = (String) aValue;
            Short  aShort = new Short(Short.parseShort(aStringValue));
            
            return aMethod.invoke(aTarget, new Object[] { aShort });
          } else if (aParamType.equals(short.class) && aValue instanceof Short) {
            return aMethod.invoke(aTarget, new Object[] { aValue });
          }
          // If the method parameter is a char type
          else if ((aParamType.equals(char.class) ||
            aParamType.equals(Character.class)) &&
            aValue instanceof String) {
            String aStringValue = (String) aValue;
            
            if (aStringValue.length() != 1) {
              throw new ConfigurationException("'" + aStringValue +
                "' must be a single character string");
            }
            
            Character aChar = new Character(aStringValue.charAt(0));
            
            return aMethod.invoke(aTarget, new Object[] { aChar });
          } else if (aParamType.equals(char.class) &&
            aValue instanceof Character) {
            return aMethod.invoke(aTarget, new Object[] { aValue });
          }
          // If the method parameter is a int type
          else if ((aParamType.equals(int.class) ||
            aParamType.equals(Integer.class)) && aValue instanceof String) {
            String  aStringValue = (String) aValue;
            Integer aInt = new Integer(Integer.parseInt(aStringValue));
            
            return aMethod.invoke(aTarget, new Object[] { aInt });
          } else if (aParamType.equals(int.class) && aValue instanceof Integer) {
            return aMethod.invoke(aTarget, new Object[] { aValue });
          }
          // If the method parameter is a long type
          else if ((aParamType.equals(long.class) ||
            aParamType.equals(Long.class)) && aValue instanceof String) {
            String aStringValue = (String) aValue;
            Long   aLong = new Long(Long.parseLong(aStringValue));
            
            return aMethod.invoke(aTarget, new Object[] { aLong });
          } else if (aParamType.equals(long.class) && aValue instanceof Long) {
            return aMethod.invoke(aTarget, new Object[] { aValue });
          }
          // If the method parameter is a float type
          else if ((aParamType.equals(float.class) ||
            aParamType.equals(Float.class)) && aValue instanceof String) {
            String aStringValue = (String) aValue;
            Float  aFloat = new Float(Float.parseFloat(aStringValue));
            
            return aMethod.invoke(aTarget, new Object[] { aFloat });
          } else if (aParamType.equals(float.class) && aValue instanceof Float) {
            return aMethod.invoke(aTarget, new Object[] { aValue });
          }
          // If the method parameter is a double type
          else if ((aParamType.equals(double.class) ||
            aParamType.equals(Double.class)) && aValue instanceof String) {
            String aStringValue = (String) aValue;
            Double aDouble = new Double(Double.parseDouble(aStringValue));
            
            return aMethod.invoke(aTarget, new Object[] { aDouble });
          } else if (aParamType.equals(double.class) &&
            aValue instanceof Double) {
            return aMethod.invoke(aTarget, new Object[] { aValue });
          }
        }
      }
      
    } catch (NumberFormatException nfe) {
      String aMessage = "'" + aValue +
        "' is not a valid numeric value for method '" + aMethodName + "'";
      throw new ConfigurationException(aMessage, nfe);
      
    } catch (IllegalAccessException iae) {
      String aMessage = "Security error invoking the method '" + aMethodName +
        "'to assign the value " + aValue + " on the object " + aTarget;
      throw new ConfigurationException(aMessage, iae);
      
    } catch (InvocationTargetException ite) {
      String aMessage = "Application error invoking the method '" +
        aMethodName + "'to assign the value " + aValue + " on the object " +
        aTarget;
      
      if (ite.getTargetException() == null) {
        throw new ConfigurationException(aMessage, ite);
      } else {
        throw new ConfigurationException(aMessage, ite.getTargetException());
      }
      
    } catch (Exception e) {
      String aMessage = "System error invoking the method '" + aMethodName +
        "'to assign the value " + aValue + " on the object " + aTarget;
      throw new ConfigurationException(aMessage, e);
    }
    
    String aMessage = "No method found for the name '" + aMethodName +
      "' on the object " + aTarget + " - value: " + aValue + " (" +
      aValue.getClass() + ")";
    throw new NoSuchMethodException(aMessage);
  }
  
  private static <E extends Enum<E>> Object getEnumValueOf(Class<E> aClass, String aName) throws Exception {
    Method m = aClass.getMethod("values");
    Object[] results=(Object[]) m.invoke(null);
    
    for (int i = 0; i < results.length; i++) {
      Object c = results[i];
      Method mc = c.getClass().getMethod("name");
      String name = (String) mc.invoke(c);
    
      if (name != null && name.equalsIgnoreCase(aName)) {
        return c;
      }
    }
    
    throw new IllegalArgumentException("No value '" + aName + "' found on the enum " + aClass);
  }
  
  /**
   * Converts the given attribute name to a method name. The following
   * indicates how attribute names are converted to method names - the method
   * name does not include the "set" or "add" prefix. All the following attribute
   * names are resolved to "FirstName":
   *
   * <ul>
   *   <li>firstName
   *   <li>FirstName
   *   <li>first-name
   *   <li>First-name
   *   <li>first-Name
   *   <li>first.name
   *   <li>First.name
   *   <li>first.Name
   * </ul>
   */
  protected static String toMethodName(String attrName) {
    return convertChars(new char[] { '-', '.' }, attrName);
  }
  
  protected static boolean containsMethod(String prefix, Object target,
    String elemName) {
    Method[] methods = target.getClass().getMethods();
    String   name = prefix + toMethodName(elemName);
    
    for (int i = 0; i < methods.length; i++) {
      if (methods[i].getName().equals(name)) {
        return true;
      }
    }
    if(target instanceof ObjectWrapperIF){
      try{
        Object wrapped = ((ObjectWrapperIF)target).getWrappedObject();
        if(wrapped != null)
          return containsMethod(prefix, wrapped, elemName);
      }catch(RuntimeException e){
        return false;
      }
    }
    
    return false;
  }
  
  /**
   * Formats an element name according to the rules defined
   * by the above method.
   */
  protected static String formatElementName(String elemName) {
    return convertChars(new char[] { '-', '.' }, elemName);
  }
  
  protected static String convertChars(char[] toConvert, String aSource) {
    StringBuffer aBuffer = new StringBuffer(aSource.length());
    char         current;
    
    for (int i = 0; i < aSource.length(); i++) {
      current = aSource.charAt(i);
      
      if (isToConvert(toConvert, current)) {
        if ((i + 1) < (aSource.length() - 1)) {
          i++;
          aBuffer.append(Character.toUpperCase(aSource.charAt(i)));
          
          continue;
        }
      }
      
      if (i == 0) {
        current = Character.toUpperCase(current);
      }
      
      aBuffer.append(current);
    }
    
    return aBuffer.toString();
  }
  
  private static boolean isToConvert(char[] toConvert, char current) {
    for (int i = 0; i < toConvert.length; i++) {
      if (toConvert[i] == current) {
        return true;
      }
    }
    
    return false;
  }
  
  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  ACCESSOR METHODS  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  
  /**
   * Returns the <code>ObjectFactoryIF</code> method held within this
   * instance.
   */
  protected ObjectFactoryIF getObjectFactory() {
    return _theObjectFactory;
  }
}
