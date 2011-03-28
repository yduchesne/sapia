package org.sapia.soto.jmx.config;

import junit.framework.TestCase;

import org.sapia.soto.jmx.MBeanDescriptor;
import org.sapia.soto.jmx.OperationDescriptor;
import org.sapia.soto.jmx.ParameterDescriptor;
import org.sapia.soto.jmx.TestBean;

/**
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class OperationsTest extends TestCase {
  public OperationsTest(String arg0) {
    super(arg0);
  }

  public void testExcludes() throws Exception {
    MBeanDescriptor desc = MBeanDescriptor.newInstanceFor(new TestBean());
    Operations ops = new Operations();
    Operation exclude = ops.createExclude();
    exclude.setName("incrementAge");
    ops.init(desc);
    desc.init();
    super.assertEquals(0, desc.getAttributeDescriptorsFor("incrementAge", null)
        .size());
  }

  public void testIncludes() throws Exception {
    MBeanDescriptor desc = MBeanDescriptor.newInstanceFor(new TestBean());
    Operations ops = new Operations();
    Operation include = ops.createInclude();
    include.setName("incrementAge");
    include.setDescription("Age increment");

    Param p = include.createParam();
    p.setName("increment");
    p.setDescription("an increment");
    ops.init(desc);
    desc.init();

    OperationDescriptor od = (OperationDescriptor) desc
        .getOperationDescriptorsFor("incrementAge", null).get(0);
    super.assertEquals("Age increment", od.getInfo().getDescription());

    
    /*ParameterDescriptor pd = (ParameterDescriptor) od.getParameters().get(0);
    super.assertEquals("an increment", pd.getInfo().getDescription());
    super.assertEquals("increment", pd.getInfo().getName());*/
  }
}
