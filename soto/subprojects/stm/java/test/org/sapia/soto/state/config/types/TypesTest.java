package org.sapia.soto.state.config.types;

import java.io.File;
import java.text.SimpleDateFormat;

import org.sapia.soto.SotoContainer;
import org.sapia.soto.examples.TypeService;

import junit.framework.TestCase;

/**
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class TypesTest extends TestCase {

  public TypesTest(String name) {
    super(name);
  }

  public void testTypes() throws Exception {
    SotoContainer container = new SotoContainer();
    try {
      container.load(new File("etc/test/types.xml"));
    } catch(Exception e) {
      e.printStackTrace();
      throw e;
    }

    TypeService ts = (TypeService) container.lookup("typeService");
    super.assertTrue(ts.isSomeBoolean());
    super.assertEquals(1, ts.getSomeShort());
    super.assertEquals(2, ts.getSomeInt());
    super.assertEquals(3, ts.getSomeLong());
    super.assertTrue(4 == ts.getSomeFloat());
    super.assertTrue(5 == ts.getSomeDouble());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/DD");
    super.assertEquals(sdf.parse("2000/12/25"), ts.getSomeDate());
  }

}
