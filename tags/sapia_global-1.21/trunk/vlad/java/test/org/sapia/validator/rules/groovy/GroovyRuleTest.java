package org.sapia.validator.rules.groovy;

import org.sapia.validator.rules.groovy.GroovyRule;

import junit.framework.TestCase;

/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class GroovyRuleTest extends TestCase{
  
  public GroovyRuleTest(String name){
    super(name);
  }
  
  public void testOnCreate() throws Exception{
    GroovyRule g = new GroovyRule();
    //g.setId("someId");
    g.setText("println(\"Validating...\");");
    g.onCreate();
  }

}
