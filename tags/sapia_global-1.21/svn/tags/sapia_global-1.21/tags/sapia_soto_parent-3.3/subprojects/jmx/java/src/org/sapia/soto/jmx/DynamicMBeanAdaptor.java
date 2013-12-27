package org.sapia.soto.jmx;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;

/**
 * An instance of this class adapts a <code>MBeanDescriptor</code> to the
 * <code>DynamicMBean</code> interface. Usage is show below:
 * 
 * <pre>
 * Object someJavaBean = new SomeObject();MBeanDescriptor desc = MBeanDescriptor.newInstanceFor(someJavaBean);
 *  DynamicMBeanAdaptor mbean = new DynamicMBeanAdaptor(desc);
 *  
 * </pre>
 * 
 * @see org.sapia.soto.jmx.MBeanDescriptor
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
public class DynamicMBeanAdaptor implements DynamicMBean {
  private MBeanDescriptor _desc;

  /**
   * Constructor for DynamicMBeanAdaptor.
   * 
   * @param desc
   *          the <code>MBeanDescriptor</code> wrapped by this instance.
   */
  public DynamicMBeanAdaptor(MBeanDescriptor desc) {
    _desc = desc;
  }

  /**
   * @see javax.management.DynamicMBean#getAttribute(String)
   */
  public Object getAttribute(String attribute)
      throws AttributeNotFoundException, MBeanException, ReflectionException {
    return _desc.getAttribute(attribute);
  }

  /**
   * @see javax.management.DynamicMBean#getAttributes(String[])
   */
  public AttributeList getAttributes(String[] names) {
    AttributeList list = new AttributeList();

    for(int i = 0; i < names.length; i++) {
      try {
        list.add(new Attribute(names[i], _desc.getAttribute(names[i])));
      } catch(ReflectionException e) {
        //noop
      } catch(MBeanException e) {
        //noop
      } catch(AttributeNotFoundException e) {
        //noop
      }
    }

    return list;
  }

  /**
   * @see javax.management.DynamicMBean#getMBeanInfo()
   */
  public MBeanInfo getMBeanInfo() {
    return _desc.getMBeanInfo();
  }

  /**
   * @see javax.management.DynamicMBean#invoke(String, Object[], String[])
   */
  public Object invoke(String actionName, Object[] params, String[] sig)
      throws MBeanException, ReflectionException {
    return _desc.invoke(actionName, params, sig);
  }

  /**
   * @see javax.management.DynamicMBean#setAttribute(Attribute)
   */
  public void setAttribute(Attribute attr) throws AttributeNotFoundException,
      InvalidAttributeValueException, MBeanException, ReflectionException {
    _desc.setAttribute(attr);
  }

  /**
   * @see javax.management.DynamicMBean#setAttributes(AttributeList)
   */
  public AttributeList setAttributes(AttributeList attributes) {
    AttributeList list = new AttributeList();
    Attribute attr;

    for(int i = 0; i < attributes.size(); i++) {
      try {
        attr = (Attribute) attributes.get(i);
        _desc.setAttribute(attr);
        list.add(new Attribute(attr.getName(), null));
      } catch(ReflectionException e) {
        //noop
      } catch(MBeanException e) {
        //noop
      } catch(AttributeNotFoundException e) {
        //noop
      }
    }

    return list;
  }
}
