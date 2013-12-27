package org.sapia.soto;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.sapia.soto.examples.MasterService;

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
public class IncludeTest extends TestCase {
  /**
   * Constructor for IncludeTest.
   */
  public IncludeTest(String test) {
    super(test);
  }

  public void testIncludeRelativeResource() throws Exception {
    SotoContainer cont = new SotoContainer();
    Map props = new HashMap();
    props.put("serviceName", "secondaryRelativeResource");
    cont.load("org/sapia/soto/includeRelativeResource.xml", props);
  }

  public void testIncludeAbsoluteFile() throws Exception {
    SotoContainer cont = new SotoContainer();
    Map props = new HashMap();
    props.put("serviceName", "secondaryAbsoluteFile");
    cont.load("org/sapia/soto/includeAbsoluteFile.xml", props);
  }

  public void testIncludeAbsoluteResource() throws Exception {
    SotoContainer cont = new SotoContainer();
    Map props = new HashMap();
    props.put("serviceName", "secondaryAbsoluteResource");
    cont.load("org/sapia/soto/includeAbsoluteResource.xml", props);
  }
  
  public void testIncludeParametrizedResource() throws Exception {
    SotoContainer cont = new SotoContainer();
    cont.load("org/sapia/soto/includeParamResource.xml");

    MasterService svc = (MasterService) cont.lookup("master");
    super.assertEquals("included", svc.getMessage());
  }
  
  public void testIncludedService() throws Exception {
    SotoContainer cont = new SotoContainer();
    cont.load(new File("etc/test/includedService.xml"));
  }  
  
}
