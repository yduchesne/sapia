package org.sapia.soto.jmx;

import org.sapia.soto.util.Type;

import java.lang.reflect.Method;

import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;

/**
 * An instance of this class wraps read and write <code>Method</code> objects
 * corresponding to an attribute of a MBean. An instance of this class generates
 * the <code>MBeanAttributeInfo</code> corresponding to its attribute. <b>The
 * class provides a setter to define the description of the attribute. If no
 * description is specified, a default one is used.
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
public class AttributeDescriptor {
  private String  _desc = MBeanDescriptor.DEFAULT_DESC;
  private String  _name;
  private boolean _isIs;
  private Method  _readable;
  private Method  _writable;

  /**
   * Constructor for AttributeDescriptor.
   */
  public AttributeDescriptor() {
    super();
  }

  /**
   * Sets this instance's description.
   * 
   * @param desc
   *          a description.
   */
  public void setDescription(String desc) {
    _desc = desc;
  }

  /**
   * Returns the name of the MBean attribute to which this instance corresponds.
   * 
   * @return an attribute name.
   */
  public String getAttributeName() {
    return _name;
  }

  /**
   * Return the method object corresponding to the "getter" of this instance's
   * corresponding MBean attribute.
   * 
   * @return a <code>Method</code> object, or <code>null</code> if this
   *         instance has no getter defined.
   */
  public Method getReadMethod() {
    return _readable;
  }

  /**
   * Return the method object corresponding to the "setter" of this instance's
   * corresponding MBean attribute.
   * 
   * @return a <code>Method</code> object, or <code>null</code> if this
   *         instance has no setter defined.
   */
  public Method getWriteMethod() {
    return _writable;
  }

  public void setWritable(boolean writable) {
    if(!writable) {
      _writable = null;
    }
  }

  /**
   * Returns the <code>MBeanAttributeInfo</code> corresponding to this
   * instance's MBean attribute.
   * 
   * @return a <code>MBeanAttribute</code>.
   */
  public MBeanAttributeInfo getInfo() throws IntrospectionException {
    return new MBeanAttributeInfo(_name, _desc, _readable, _writable);
  }

  /**
   * Sets the name of this instance's corresponding MBean attribute.
   * 
   * @param name
   *          a name.
   */
  void setAttributeName(String name) {
    _name = name;
  }

  /**
   * Specifies if this instance corresponds to an "is" method.
   * 
   * @param bool
   *          if <code>true</code>, specifies that this instance corresponds
   *          to a "is" method.
   */
  void setBoolean(boolean bool) {
    _isIs = bool;
  }

  /**
   * Sets the method object corresponding to the setter of this instance's MBean
   * attribute.
   * 
   * @param writable
   *          a <code>Method</code> object.
   */
  void setWritable(Method writable) {
    _writable = writable;
  }

  /**
   * Returns the type name of the attribute to which this instance corresponds.
   * 
   * @return a type name.
   */
  public String getType() {
    if(_readable != null) {
      if(Type.hasTypeForTypeName(_readable.getReturnType().getName())) {
        return Type.getTypeForTypeName(_readable.getReturnType().getName())
            .getName();
      } else {
        return _readable.getReturnType().getName();
      }
    } else {
      if(Type.hasTypeForTypeName(_writable.getParameterTypes()[0].getName())) {
        return Type.getTypeForTypeName(
            _writable.getParameterTypes()[0].getName()).getName();
      } else {
        return _writable.getParameterTypes()[0].getName();
      }
    }
  }

  /**
   * Sets the method object corresponding to the getter of this instance's MBean
   * attribute.
   * 
   * @param readable
   *          a <code>Method</code> object.
   */
  void setReadable(Method readable) {
    _readable = readable;
  }

  public String toString() {
    return "[ name=" + _name + ", description=" + _desc + ", isIs=" + _isIs
        + ", readable=" + _readable + ", writable=" + _writable + " ]";
  }
}
