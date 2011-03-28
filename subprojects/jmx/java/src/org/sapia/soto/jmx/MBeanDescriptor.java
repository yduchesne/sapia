package org.sapia.soto.jmx;

import org.sapia.soto.util.matcher.PathPattern;
import org.sapia.soto.util.matcher.Pattern;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;

/**
 * An instance of this class generates the <code>MBeanInfo</code>
 * corresponding to a given arbitrary Java object that must respect the JavaBean
 * pattern pertaining to the naming of setter/getter methods. Specifically, a
 * setter and/or getter will be mapped to a given MBean attribute.
 * <p>
 * In addition, all methods that are not setter/getter will be considered as JMX
 * operations.
 * <p>
 * Whether the methods correspond to setter and/or getter, they must be public
 * an non-static to be considered as attributes or operations.
 * <p>
 * An instance of this class is used as follows:
 * 
 * <pre>
 * Object someJavaBean = new SomeObject();MBeanDescriptor desc = MBeanDescriptor.newInstanceFor(someJavaBean);
 *  
 * </pre>
 * 
 * @author Yanick Duchesne
 *         <dl>
 *         <dt><b>Copyright: </b>
 *         <dd>Copyright &#169; 2002-2003 <a
 *         href="http://www.sapia-oss.org">Sapia Open Source Software </a>. All
 *         Rights Reserved.</dd>
 *         </dt>
 *         <dt><b>License: </b>
 *         <dd>Read the license.txt file of the jar or visit the <a
 *         href="http://www.sapia-oss.org/license.html">license page </a> at the
 *         Sapia OSS web site</dd>
 *         </dt>
 *         </dl>
 */
public class MBeanDescriptor {
  public static final String DEFAULT_DESC = "No description available";
  private Map                _attributes  = new HashMap();
  private Map                _operations  = new HashMap();
  private MBeanInfo          _info;
  private String             _description = DEFAULT_DESC;
  private Object             _bean;

  /**
   * Constructor for MBeanDescriptor.
   */
  private MBeanDescriptor(Object bean) {
    _bean = bean;
  }

  /**
   * Sets this instance's description.
   * 
   * @param desc
   *          a description.
   */
  public void setDescription(String desc) {
    _description = desc;
  }

  /**
   * Returns the bean with which this instance was created.
   * 
   * @return an <code>Object</code>
   */
  public Object getBean() {
    return _bean;
  }

  /**
   * Creates a <code>MBeanDescriptor</code> with the given object (that must
   * comply with the JavaBean style) and returns it.
   * 
   * @param obj
   *          an <code>Object</code> for which to create an instance of this
   *          class.
   * @return a <code>MBeanDescriptor</code>
   */
  public static MBeanDescriptor newInstanceFor(Object obj)
      throws IntrospectionException {
    MBeanDescriptor desc = new MBeanDescriptor(obj);
    Method[] methods = obj.getClass().getDeclaredMethods();

    for(int i = 0; i < methods.length; i++) {
      if(((methods[i].getModifiers() & Modifier.STATIC) != 0)
          && ((methods[i].getModifiers() & Modifier.PUBLIC) == 0)) {
        continue;
      }

      if(MethodUtils.isSetter(methods[i]) || MethodUtils.isGetter(methods[i])
          || MethodUtils.isBoolean(methods[i])) {
        desc.getAttributeDescriptorFor(methods[i]);
      } else if(MethodUtils.isOperation(methods[i])) {
        desc.getOperationDescriptor(methods[i]);
      }
    }

    desc.init();

    return desc;
  }

  /**
   * Returns the <code>AttributeDescriptor</code> that corresponds to the
   * given method. The method object passed in must be a setter or getter.
   * <p>
   * If no descriptor exists for the given method, one is internally created.
   * Only one descriptor will be created for a given setter/getter pair.
   * 
   * @param method
   *          a <code>Method</code> object for which to create an
   *          <code>AttributeDescriptor</code>, or for which to return an
   *          existing one.
   * @return an <code>AttributeDescriptor</code>.
   */
  public AttributeDescriptor getAttributeDescriptorFor(Method method)
      throws IllegalArgumentException {
    String name = null;
    MethodInfo info;

    if(MethodUtils.isSetter(method)) {
      name = MethodUtils.getAttributeName(method, MethodUtils.SET_PREFIX);
      info = new MethodInfo(name, method.getParameterTypes());
    } else if(MethodUtils.isGetter(method)) {
      name = MethodUtils.getAttributeName(method, MethodUtils.GET_PREFIX);
      info = new MethodInfo(name, new Class[] { method.getReturnType() });
    } else if(MethodUtils.isBoolean(method)) {
      name = MethodUtils.getAttributeName(method, "is");
      info = new MethodInfo(name, new Class[] { method.getReturnType() });
    } else {
      throw new IllegalArgumentException("Method " + method
          + " does not start with set/get/is prefix");
    }

    AttributeDescriptor desc = (AttributeDescriptor) _attributes.get(info);

    if(desc == null) {
      desc = new AttributeDescriptor();
      desc.setAttributeName(name);
      initAttributeDesc(desc, method);
      _attributes.put(info, desc);
    } else {
      initAttributeDesc(desc, method);
    }

    return desc;
  }

  /**
   * Returns the <code>OperationDescriptor</code> that corresponds to the
   * given method. The method object passed in must not corrspond to a setter or
   * getter.
   * <p>
   * If no descriptor exists for the given method, one is internally created.
   * 
   * @param method
   *          a <code>Method</code> object for which to create an
   *          <code>OperationDescriptor</code>, or for which to return an
   *          existing one.
   * @return an <code>OperationDescriptor</code>
   */
  public OperationDescriptor getOperationDescriptor(Method method) {
    MethodInfo toCompare = new MethodInfo(method);
    OperationDescriptor od = (OperationDescriptor) _operations.get(toCompare);

    if(od == null) {
      if(!MethodUtils.isOperation(method)) {
        throw new IllegalArgumentException("Method " + method
            + " does not correspond to a MBean operation");
      }

      od = new OperationDescriptor();
      _operations.put(toCompare, od);
      initOperationDesc(od, method);
    }

    return od;
  }

  /**
   * Returns the <code>MBeanInfo</code> corresponding to this instance - or
   * rather, to the object wrapped by this instance.
   * 
   * @return the <code>MBeanInfo</code> corresponding to this instance.
   */
  public MBeanInfo getMBeanInfo() {
    return _info;
  }

  /**
   * Invokes the getter corresponding to the given attribute name an returns the
   * result of the invocation.
   * 
   * @param name
   *          the name of the attribute whose getter should be invoked.
   * @return the getter's invocation result.
   * @throws AttributeNotFoundException
   *           if no getter could be found for the given attribute name.
   * @throws MBeanException
   *           if an exception occurred calling the given attribute's
   *           corresponding getter.
   * @throws ReflectionException
   *           if a problem pertaining to the internal Java reflection hack
   *           occurs.
   */
  public Object getAttribute(String name) throws AttributeNotFoundException,
      MBeanException, ReflectionException {
    List attrs;

    try {
      attrs = getAttributeDescriptorsFor(name, null);
    } catch(IntrospectionException e) {
      throw new MBeanException(e, "Get value for attribute: " + name);
    }

    if(attrs.size() == 0) {
      throw new AttributeNotFoundException("No attribute for: " + name);
    }

    AttributeDescriptor ad = (AttributeDescriptor) attrs.get(0);

    if(ad == null) {
      throw new AttributeNotFoundException(name);
    }

    if(ad.getReadMethod() == null) {
      throw new AttributeNotFoundException("Attribute '" + name
          + "' is not readable");
    }

    try {
      return ad.getReadMethod().invoke(_bean, new Object[0]);
    } catch(IllegalAccessException e) {
      throw new ReflectionException(e, "Attribute '" + name
          + "'  is not accessible");
    } catch(InvocationTargetException e) {
      Exception err;

      if(e.getTargetException() instanceof Exception) {
        err = (Exception) e.getTargetException();
      } else {
        err = e;
      }

      throw new MBeanException(err, "Error occured reading attribute: " + name);
    }
  }

  /**
   * Invokes the setter corresponding to the given attribute.
   * 
   * @param attr an <code>Attribute</code>.
   * @throws AttributeNotFoundException
   *           if no setter could be found for the given attribute name.
   * @throws MBeanException
   *           if an exception occurred calling the given attribute's
   *           corresponding setter.
   * @throws ReflectionException
   *           if a problem pertaining to the internal Java reflection hack
   *           occurs.
   */
  public void setAttribute(Attribute attr) throws AttributeNotFoundException,
      MBeanException, ReflectionException {
    List attrs;

    try {
      attrs = getAttributeDescriptorsFor(attr.getName(), null);
    } catch(IntrospectionException e) {
      throw new MBeanException(e, "Get value for attribute: " + attr.getName());
    }

    if(attrs.size() == 0) {
      throw new AttributeNotFoundException("No attribute for: "
          + attr.getName());
    }

    AttributeDescriptor ad = null;

    for(int i = 0; i < attrs.size(); i++) {
      ad = (AttributeDescriptor) attrs.get(i);

      if((ad.getWriteMethod() != null)
          && ((attr.getValue() == null) || ad.getWriteMethod()
              .getParameterTypes()[0].isAssignableFrom(attr.getValue()
              .getClass()))) {
        break;
      }
    }

    if(ad == null) {
      throw new AttributeNotFoundException(attr.getName());
    }

    if(ad.getWriteMethod() == null) {
      throw new AttributeNotFoundException("Attribute '" + attr.getName()
          + "' is not writable");
    }

    try {
      ad.getWriteMethod().invoke(_bean, new Object[] { attr.getValue() });
    } catch(IllegalAccessException e) {
      throw new ReflectionException(e, "Attribute '" + attr.getName()
          + "'  is not accessible");
    } catch(InvocationTargetException e) {
      Exception err;

      if(e.getTargetException() instanceof Exception) {
        err = (Exception) e.getTargetException();
      } else {
        err = e;
      }

      throw new MBeanException(err, "Error occured writing attribute: "
          + attr.getName());
    }
  }

  /**
   * Performs the operation corresponding to the given parameters.
   * 
   * @param opName
   *          the name of the method to invoke.
   * @param params
   *          the method's parameters.
   * @param sig
   *          an array of <code>Class</code> objects corresponding to the
   *          desired method's signature.
   */
  public Object invoke(String opName, Object[] params, String[] sig)
      throws MBeanException, ReflectionException {
    MethodInfo info = new MethodInfo(opName, sig);
    OperationDescriptor od = (OperationDescriptor) _operations.get(info);

    if(od == null) {
      throw new MBeanException(new NullPointerException(),
          "No action found for " + opName);
    }

    try {
      return od.getMethod().invoke(_bean, params);
    } catch(IllegalAccessException e) {
      throw new ReflectionException(e, "Operation '" + opName
          + "'  is not accessible");
    } catch(InvocationTargetException e) {
      throw new MBeanException(e, "Error occured peforming operation: "
          + opName);
    }
  }

  /**
   * Returns the list of attribute descriptors corresponding to the given
   * information.
   * <p>
   * This method's first argument is the pattern intended to match given
   * attribute names. For example:
   * <ul>
   * <li>'Name' matches the 'Name' attribute (corresponding to the
   * setName/getName method.
   * <li>'*Name' matches any attribute whose name ends with 'Name'.
   * <li>'Name*' matches any attribute whose name starts with 'Name'.
   * <li>'*Name*' matches any attribute whose name ends and starts with 'Name'.
   * <li>etc.
   * </ul>
   * <p>
   * Pattern matching for the type works the same way; if the type is null, then
   * any attribute descriptor whose name matches the name pattern is returned.
   * <p>
   * 
   * 
   * @param name
   *          an attribute name pattern.
   * @param type
   *          an attribute type (class name) pattern.
   */
  public List getAttributeDescriptorsFor(String name, String type)
      throws IntrospectionException {
    if(name == null) {
      throw new IntrospectionException("name arg cannot be null");
    }

    List toReturn = new ArrayList();
    AttributeDescriptor desc;
    Pattern namePattern = PathPattern.parse(name, true);
    Pattern typePattern = null;

    if(type != null) {
      typePattern = PathPattern.parse(type, true);
    }

    for(Iterator iter = _attributes.values().iterator(); iter.hasNext();) {
      desc = (AttributeDescriptor) iter.next();

      if(desc.getInfo().getType() != null) {
        if(type != null) {
          if(namePattern.matches(desc.getAttributeName())
              && typePattern.matches(desc.getType())) {
            toReturn.add(desc);
          }
        } else {
          if(namePattern.matches(desc.getAttributeName())) {
            toReturn.add(desc);
          }
        }
      } else {
        if(namePattern.matches(desc.getAttributeName())) {
          toReturn.add(desc);
        }
      }
    }

    return toReturn;
  }

  /**
   * Removes the list of attribute descriptors corresponding to the given
   * information.
   * <p>
   * This method's first argument is the pattern intended to match given
   * attribute names. For example:
   * <ul>
   * <li>'Name' matches the 'Name' attribute (corresponding to the
   * setName/getName method.
   * <li>'*Name' matches any attribute whose name ends with 'Name'.
   * <li>'Name*' matches any attribute whose name starts with 'Name'.
   * <li>'*Name*' matches any attribute whose name ends and starts with 'Name'.
   * <li>etc.
   * </ul>
   * <p>
   * Pattern matching for the type works the same way; if the type is null, then
   * any attribute descriptor whose name matches the name pattern is removed.
   * <p>
   * 
   * @param name
   *          an attribute name pattern.
   * @param type
   *          an attribute type (class name) pattern.
   */
  public void removeAttributeDescriptorsFor(String name, String type)
      throws IntrospectionException {
    List toRemove = getAttributeDescriptorsFor(name, type);
    AttributeDescriptor desc;

    for(int i = 0; i < toRemove.size(); i++) {
      desc = (AttributeDescriptor) toRemove.get(i);
      _attributes.remove(new MethodInfo(desc.getAttributeName(),
          new String[] { desc.getType() }));
    }
  }

  /**
   * Adds the given attribute descriptor to this instance.
   * 
   * @param desc 
   *          an <code>AttributeDescriptor</code>.
   */
  public void addAttributeDescriptor(AttributeDescriptor desc) {
    _attributes.put(new MethodInfo(desc.getAttributeName(), new String[] { desc
        .getType() }), desc);
  }

  /**
   * Returns the list of attribute descriptors corresponding to the given
   * information.
   * <p>
   * This method's first argument is the pattern intended to match given
   * operation names. For example:
   * <ul>
   * <li>'performAction' matches the 'performAction' operation (corresponding
   * to the setName/getName method.
   * <li>'*Action' matches any operation whose name ends with 'Action'.
   * <li>'perform*' matches any operation whose name starts with 'perform'.
   * <li>'*Action*' matches any operation whose name ends and starts with
   * 'Action'.
   * <li>etc.
   * </ul>
   * <p>
   * Pattern matching for the parameters works the same way; if the params array
   * is null null, then any operation descriptor whose name matches the name
   * pattern is returned; if the params array is not null, then its length is
   * compared to each operation descriptor's number of parameter. If that number
   * is equal AND the operation's parameter types match the passed in parameter
   * patterns, then the operation descriptor is returned.
   * <p>
   * 
   * @param name
   *          an operation name pattern.
   * @param params
   *          an array of parameter type (class name) patterns.
   */
  public List getOperationDescriptorsFor(String name, String[] params)
      throws IntrospectionException {
    if(name == null) {
      throw new IntrospectionException("name arg cannot be null");
    }

    List toReturn = new ArrayList();
    OperationDescriptor desc;
    ParameterDescriptor paramDesc;
    Pattern namePattern = PathPattern.parse(name, true);
    Pattern[] typePatterns = null;

    if(params != null) {
      typePatterns = new Pattern[params.length];

      for(int i = 0; i < params.length; i++) {
        typePatterns[i] = PathPattern.parse(params[i], false);
      }
    }

    for(Iterator iter = _operations.values().iterator(); iter.hasNext();) {
      desc = (OperationDescriptor) iter.next();

      if(namePattern.matches(desc.getOperationName())) {
        if(params == null) {
          toReturn.add(desc);
        } else {
          List paramList = desc.getParameters();
          int count = 0;

          if(paramList.size() != typePatterns.length) {
            continue;
          }

          for(int i = 0; i < paramList.size(); i++) {
            paramDesc = (ParameterDescriptor) paramList.get(i);

            if(typePatterns[i].matches(paramDesc.getInfo().getType())) {
              count++;
            }
          }

          if(count == params.length) {
            toReturn.add(desc);
          }
        }
      }
    }

    return toReturn;
  }

  /**
   * Removes the list of attribute descriptors corresponding to the given
   * information.
   * <p>
   * This method's first argument is the pattern intended to match given
   * operation names. For example:
   * <ul>
   * <li>'performAction' matches the 'performAction' operation (corresponding
   * to the setName/getName method.
   * <li>'*Action' matches any operation whose name ends with 'Action'.
   * <li>'perform*' matches any operation whose name starts with 'perform'.
   * <li>'*Action*' matches any operation whose name ends and starts with
   * 'Action'.
   * <li>etc.
   * </ul>
   * <p>
   * Pattern matching for the parameters works the same way; if the params array
   * is null null, then any operation descriptor whose name matches the name
   * pattern is removed; if the params array is not null, then its length is
   * compared to each operation descriptor's number of parameter. If that number
   * is equal AND the operation's parameter types match the passed in parameter
   * patterns, then the operation descriptor is removed.
   * <p>
   * 
   * @param name
   *          an operation name pattern.
   * @param params
   *          an array of parameter type (class name) patterns.
   */
  public void removeOperationDescriptorsFor(String name, String[] params)
      throws IntrospectionException {
    List toRemove = getOperationDescriptorsFor(name, params);
    OperationDescriptor desc;

    for(int i = 0; i < toRemove.size(); i++) {
      desc = (OperationDescriptor) toRemove.get(i);
      _operations.remove(new MethodInfo(desc.getMethod()));
    }
  }

  /**
   * Adds the given operation descriptor to this instance.
   * 
   * @param desc
   *          an <code>OperationDescriptor</code>.
   */
  public void addOperationDescriptor(OperationDescriptor desc) {
    _operations.put(new MethodInfo(desc.getMethod()), desc);
  }

  private MBeanAttributeInfo[] getAttributeInfos()
      throws IntrospectionException {
    MBeanAttributeInfo[] infos = new MBeanAttributeInfo[_attributes.size()];
    AttributeDescriptor desc;
    int i = 0;

    for(Iterator iter = _attributes.values().iterator(); iter.hasNext(); i++) {
      desc = (AttributeDescriptor) iter.next();
      infos[i] = desc.getInfo();
    }

    return infos;
  }

  private MBeanOperationInfo[] getOperationInfos() {
    MBeanOperationInfo[] infos = new MBeanOperationInfo[_operations.size()];
    OperationDescriptor desc;
    int i = 0;

    for(Iterator iter = _operations.values().iterator(); iter.hasNext(); i++) {
      desc = (OperationDescriptor) iter.next();
      infos[i] = desc.getInfo();
    }

    return infos;
  }

  private static void initAttributeDesc(AttributeDescriptor ad, Method method) {
    if(MethodUtils.isSetter(method)) {
      ad.setWritable(method);
    } else if(MethodUtils.isGetter(method)) {
      ad.setReadable(method);
    }
  }

  private static void initOperationDesc(OperationDescriptor od, Method method) {
    od.setOperationName(method.getName());
    od.setMethod(method);

    Class[] params = method.getParameterTypes();
    ParameterDescriptor pd;
    for(int i = 0; i < params.length; i++) {
      pd = new ParameterDescriptor();
      pd.setType(params[i].getName());
      pd.setName(MethodUtils.getShortClassName(params[i]));
      od.addParameters(pd);
    }
  }

  public void init() throws IntrospectionException {
    _info = new MBeanInfo(_bean.getClass().getName(), _description,
        getAttributeInfos(), new MBeanConstructorInfo[0], getOperationInfos(),
        new MBeanNotificationInfo[0]);
  }

  public String toString() {
    return "[ attributes=" + _attributes + ", operations=" + _operations + " ]";
  }
}
