package org.sapia.soto.jmx.config;

import org.sapia.soto.jmx.MBeanDescriptor;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

/**
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
public class SomeBean {
  private String _name;

  public void setName(String name) {
    _name = name;
  }

  public String getName() {
    return _name;
  }

  public void performOperationOne(String param1, int param2) {
  }

  public void performOperationTwo(String param1, int param2) {
  }

  public static void main(String[] args) {
    try {
      SomeBean bean = new SomeBean();
      MBeanDescriptor mbean = MBeanDescriptor.newInstanceFor(bean);
      Attributes attrs = new Attributes();
      Attribute a = attrs.createInclude();
      a.setDescription("The Name");
      a.setName("name");
      a.setWritable(false);
      attrs.init(mbean);

      Operations ops = new Operations();
      Operation include = ops.createInclude();
      include.setName("performOperationOne");
      include.setDescription("Some operation");

      Operation exclude = ops.createExclude();
      exclude.setName("performOperationTwo");
      exclude.setDescription("Some operation");
      ops.init(mbean);

      mbean.init();

      MBeanAttributeInfo[] info = mbean.getMBeanInfo().getAttributes();
      System.out.println("Operations:");

      for(int i = 0; i < info.length; i++) {
        System.out.println("==============================");
        System.out.println(info[i].getName());
        System.out.println(info[i].getDescription());
        System.out.println("readable: " + info[i].isReadable() + ", writable: "
            + info[i].isWritable());
      }

      MBeanOperationInfo[] opInfo = mbean.getMBeanInfo().getOperations();

      for(int i = 0; i < opInfo.length; i++) {
        System.out.println("==============================");
        System.out.println(opInfo[i].getName());
        System.out.println(opInfo[i].getDescription());

        MBeanParameterInfo[] params = opInfo[i].getSignature();

        for(int j = 0; j < params.length; j++) {
          System.out.println(params[j].getName() + ", "
              + params[j].getDescription());
        }
      }
    } catch(Throwable t) {
      t.printStackTrace();
    }
  }
}
