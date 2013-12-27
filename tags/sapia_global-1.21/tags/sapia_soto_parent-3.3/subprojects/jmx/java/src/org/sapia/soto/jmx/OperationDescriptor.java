package org.sapia.soto.jmx;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

/**
 * An instance of this class generates the <code>MBeanOperationInfo</code>
 * corresponding to a given MBean operation.
 * <p>
 * It allows callers to set a description that will be past to the generated
 * <code>MBeanOperationInfo</code>.
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
public class OperationDescriptor {
  private String _name;
  private String _description = MBeanDescriptor.DEFAULT_DESC;
  private List   _parameters  = new ArrayList();
  private String _type        = void.class.getName();
  private Method _method;
  private int    _impact      = MBeanOperationInfo.UNKNOWN;

  /**
   * Constructor for OperationDescriptor.
   */
  public OperationDescriptor() {
    super();
  }

  /**
   * Sets this instance's "impact" value.
   * 
   * @param impact
   *          an "impact" value, as an <code>int</code> that corresponds to a
   *          constant in the MBeanOperationInfo class.
   */
  public void setImpact(int impact) {
    _impact = impact;
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
   * Returns the <code>MBeanOperationInfo</code> corresponding to this
   * instance's MBean operation.
   * 
   * @return the <code>MBeanOperationInfo</code> corresponding to this
   *         instance.
   */
  public MBeanOperationInfo getInfo() {
    return new MBeanOperationInfo(_name, _description, getParameterInfos(),
        _type, _impact);
  }

  /**
   * Returns this instance's method - corresponding to this instance's
   * operation.
   */
  public Method getMethod() {
    return _method;
  }

  public String getOperationName() {
    return _name;
  }

  public List getParameters() {
    return _parameters;
  }

  void setMethod(Method method) {
    _method = method;
    _type = method.getReturnType().getName();
  }

  void setOperationName(String name) {
    _name = name;
  }

  MBeanParameterInfo[] getParameterInfos() {
    MBeanParameterInfo[] infos = new MBeanParameterInfo[_parameters.size()];
    ParameterDescriptor desc;

    for(int i = 0; i < _parameters.size(); i++) {
      desc = (ParameterDescriptor) _parameters.get(i);
      infos[i] = desc.getInfo();
    }

    return infos;
  }

  void addParameters(ParameterDescriptor desc) {
    _parameters.add(desc);
  }

  public String toString() {
    return "[ name=" + _name + ", description=" + _description + ", type="
        + _type + ", parameters=" + _parameters + " ]";
  }
}
