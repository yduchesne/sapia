package org.sapia.soto.jmx.config;

import org.sapia.soto.ConfigurationException;
import org.sapia.soto.jmx.AttributeDescriptor;
import org.sapia.soto.jmx.MBeanDescriptor;

import java.util.ArrayList;
import java.util.List;

import javax.management.IntrospectionException;

/**
 * @author Yanick Duchesne 18-Aug-2003
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
public class Attributes {
  private List _includes = new ArrayList();
  private List _excludes = new ArrayList();

  /**
   * Constructor for Attributes.
   */
  public Attributes() {
    super();
  }

  public void init(MBeanDescriptor mbean) throws IntrospectionException,
      ConfigurationException {
    Attribute attr;
    AttributeDescriptor desc;
    List includes = new ArrayList();
    List descs;

    for(int i = 0; i < _includes.size(); i++) {
      attr = (Attribute) _includes.get(i);

      if(attr.getName() == null) {
        throw new ConfigurationException(
            "'name' attribute not specified on 'include' element");
      }

      includes.addAll(descs = mbean.getAttributeDescriptorsFor(attr.getName(),
          attr.getType()));

      for(int j = 0; j < descs.size(); j++) {
        desc = (AttributeDescriptor) descs.get(j);
        desc.setDescription(attr.getDescription());

        if(desc.getWriteMethod() != null) {
          desc.setWritable(attr.isWritable());
        }
      }
    }

    for(int i = 0; i < _excludes.size(); i++) {
      attr = (Attribute) _excludes.get(i);

      if(attr.getName() == null) {
        throw new ConfigurationException(
            "'name' attribute not specified on 'exclude' element");
      }

      mbean.removeAttributeDescriptorsFor(attr.getName(), attr.getType());
    }

    for(int i = 0; i < includes.size(); i++) {
      mbean.addAttributeDescriptor((AttributeDescriptor) includes.get(i));
    }
  }

  public Attribute createInclude() {
    Attribute attr = new Attribute();
    _includes.add(attr);

    return attr;
  }

  public Attribute createExclude() {
    Attribute attr = new Attribute();
    _excludes.add(attr);

    return attr;
  }
}
