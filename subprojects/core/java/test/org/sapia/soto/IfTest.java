package org.sapia.soto;

import java.util.Properties;

import junit.framework.TestCase;

import org.sapia.soto.examples.SecondaryService;

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
public class IfTest extends TestCase {
  public IfTest(String arg0) {
    super(arg0);
  }

  public void testIfFalse() throws Exception {
    SotoContainer cont = new SotoContainer();
    cont.load("org/sapia/soto/ifTest.xml");

    SecondaryService sec = (SecondaryService) cont.lookup("secondary");
    super.assertTrue(!sec.hasMaster());
  }

  public void testIfTrue() throws Exception {
    SotoContainer cont = new SotoContainer();
    Properties props = new Properties();
    props.setProperty("set.master.service", "true");
    cont.load("org/sapia/soto/ifTest.xml", props);

    SecondaryService sec = (SecondaryService) cont.lookup("secondary");
    super.assertTrue(sec.hasMaster());
  }
}
