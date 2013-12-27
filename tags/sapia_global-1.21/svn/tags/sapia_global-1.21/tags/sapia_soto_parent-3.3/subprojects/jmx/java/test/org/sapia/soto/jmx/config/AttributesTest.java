package org.sapia.soto.jmx.config;

import junit.framework.TestCase;

import org.sapia.soto.jmx.AttributeDescriptor;
import org.sapia.soto.jmx.MBeanDescriptor;
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
public class AttributesTest extends TestCase {
  public AttributesTest(String arg0) {
    super(arg0);
  }

  public void testExcludes() throws Exception {
    MBeanDescriptor desc = MBeanDescriptor.newInstanceFor(new TestBean());
    Attributes attrs = new Attributes();
    Attribute exclude = attrs.createExclude();
    exclude.setName("firstname");
    attrs.init(desc);
    desc.init();
    super.assertEquals(0, desc.getAttributeDescriptorsFor("firstname", null)
        .size());
  }

  public void testIncludes() throws Exception {
    MBeanDescriptor desc = MBeanDescriptor.newInstanceFor(new TestBean());
    Attributes attrs = new Attributes();
    Attribute exclude = attrs.createInclude();
    exclude.setName("firstname");
    exclude.setDescription("The first name");
    attrs.init(desc);
    desc.init();

    AttributeDescriptor ad = (AttributeDescriptor) desc
        .getAttributeDescriptorsFor("firstname", null).get(0);
    super.assertEquals("The first name", ad.getInfo().getDescription());
  }
}
