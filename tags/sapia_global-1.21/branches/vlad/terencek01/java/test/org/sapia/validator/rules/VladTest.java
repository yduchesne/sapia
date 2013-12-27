package org.sapia.validator.rules;

import org.sapia.validator.Vlad;

import junit.framework.TestCase;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class VladTest extends TestCase{

  /**
   * Constructor for VladTest.
   */
  public VladTest(String name) {
    super(name);
  }
  
  public void testVlad() throws Exception{
  	Vlad v = new Vlad();
  	v.load("org/sapia/validator/rules/vladTest.xml");
  }

}
