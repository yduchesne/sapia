package org.sapia.archie.impl;

import junit.framework.TestCase;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class AttributeNamePartTest extends TestCase{
  
  public void testMatches() throws Exception{
    AttributeNameParser p = new AttributeNameParser();
    AttributeNamePart query = (AttributeNamePart)p.parseNamePart("name?attr1=value1");
    AttributeNamePart item1 = (AttributeNamePart)p.parseNamePart("name?attr1=value1&attr2=value2");
    super.assertTrue(item1.matches(query));
    super.assertTrue(item1.matches(item1));
    super.assertTrue(!query.matches(item1));    
  }
  
  public void testEquals()  throws Exception{
    
  }
}
