package org.sapia.soto.jmx;

import org.sapia.soto.util.Type;

import javax.management.MBeanParameterInfo;

/**
 * An instance of this class encapsulates the information necessary to create a
 * corresponding <code>MBeanParameterInfo</code> object. Callers can set a
 * description.
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
public class ParameterDescriptor {
  private String _name;
  private String _type;
  private String _desc = MBeanDescriptor.DEFAULT_DESC;

  /**
   * Constructor for ParameterDescriptor.
   */
  public ParameterDescriptor() {
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
   * Sets the name of this parameter - used for display purposes by JMX
   * consoles.
   * 
   * @param name
   *          the name of the MBean parameter to which this instance
   *          corresponds.
   */
  public void setName(String name) {
    _name = name;
  }

  /**
   * Returns the <code>MBeanParameter</code> that corresponds to this
   * instance's parameter - in a MBean operation.
   * 
   * @return a <code>MBeanParameterInfo</code>.
   * @see OperationDescriptor
   */
  public MBeanParameterInfo getInfo() {
    return new MBeanParameterInfo(_name, Type.hasTypeForName(_type) ? Type
        .getTypeForName(_type).getClassName() : _type, _desc);
  }

  /**
   * Sets the "type" of this instance - corresponds to the full-qualified name
   * of a Java type that is part of a method signature.
   * 
   * @param type
   *          the name of Java type.
   */
  void setType(String type) {
    _type = type;
  }

  public String toString() {
    return "[ name=" + _name + ", type=" + _type + ", description=" + _desc
        + " ]";
  }
}
