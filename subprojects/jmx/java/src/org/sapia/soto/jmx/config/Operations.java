package org.sapia.soto.jmx.config;

import org.sapia.soto.ConfigurationException;
import org.sapia.soto.jmx.MBeanDescriptor;
import org.sapia.soto.jmx.OperationDescriptor;
import org.sapia.soto.jmx.ParameterDescriptor;

import java.util.ArrayList;
import java.util.List;

import javax.management.IntrospectionException;

/**
 * @author Yanick Duchesne 19-Aug-2003
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
public class Operations {
  private List _includes = new ArrayList();
  private List _excludes = new ArrayList();

  /**
   * Constructor for Operations.
   */
  public Operations() {
    super();
  }

  public void init(MBeanDescriptor mbean) throws IntrospectionException,
      ConfigurationException {
    Operation operation;
    OperationDescriptor desc;
    List includes = new ArrayList();
    List descs;

    for(int i = 0; i < _includes.size(); i++) {
      operation = (Operation) _includes.get(i);

      if(operation.getName() == null) {
        throw new ConfigurationException(
            "'name' attribute not specified on 'include' element from JMX operation");
      }

      includes.addAll(descs = mbean.getOperationDescriptorsFor(operation
          .getName(), operation.getSig()));

      for(int j = 0; j < descs.size(); j++) {
        desc = (OperationDescriptor) descs.get(j);

        if(operation.getDescription() != null) {
          desc.setDescription(operation.getDescription());
        }

        List paramDescs = desc.getParameters();
        List params = operation.getParams();
        Param param;
        ParameterDescriptor paramDesc;

        if(paramDescs.size() == params.size()) {
          for(int k = 0; k < params.size(); k++) {
            param = (Param) params.get(k);
            paramDesc = (ParameterDescriptor) paramDescs.get(k);

            if(param.getDescription() != null) {
              paramDesc.setDescription(param.getDescription());
            }

            if(param.getName() != null) {
              paramDesc.setName(param.getName());
            }
          }
        }
      }
    }

    for(int i = 0; i < _excludes.size(); i++) {
      operation = (Operation) _excludes.get(i);

      if(operation.getName() == null) {
        throw new ConfigurationException(
            "'name' attribute not specified on 'include' element from JMX operation");
      }

      mbean.removeOperationDescriptorsFor(operation.getName(), operation
          .getSig());
    }

    for(int i = 0; i < includes.size(); i++) {
      mbean.addOperationDescriptor((OperationDescriptor) includes.get(i));
    }
  }

  public Operation createInclude() {
    Operation op = new Operation();
    _includes.add(op);

    return op;
  }

  public Operation createExclude() {
    Operation op = new Operation();
    _excludes.add(op);

    return op;
  }

}
