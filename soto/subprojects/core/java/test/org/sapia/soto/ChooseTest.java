package org.sapia.soto;

import java.util.Properties;

import junit.framework.TestCase;

import org.sapia.soto.examples.MasterService;
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
public class ChooseTest extends TestCase {
  public ChooseTest(String arg0) {
    super(arg0);
  }

  public void testOtherwise() throws Exception {
    SotoContainer cont = new SotoContainer();
    cont.load("org/sapia/soto/chooseTest.xml");

    SecondaryService sec = (SecondaryService) cont.lookup("secondary");
    super.assertTrue(sec.hasMaster());
    super
        .assertEquals("3", ((MasterService) sec.getSomeService()).getMessage());
  }

  public void testIf() throws Exception {
    SotoContainer cont = new SotoContainer();
    Properties props = new Properties();
    props.setProperty("set.master.service", "1");
    cont.load("org/sapia/soto/chooseTest.xml", props);

    SecondaryService sec = (SecondaryService) cont.lookup("secondary");
    super.assertTrue(sec.hasMaster());
    super
        .assertEquals("1", ((MasterService) sec.getSomeService()).getMessage());
  }

  public void testElseif() throws Exception {
    SotoContainer cont = new SotoContainer();
    Properties props = new Properties();
    props.setProperty("set.master.service", "2");
    cont.load("org/sapia/soto/chooseTest.xml", props);

    SecondaryService sec = (SecondaryService) cont.lookup("secondary");
    super.assertTrue(sec.hasMaster());
    super
        .assertEquals("2", ((MasterService) sec.getSomeService()).getMessage());
  }
}
