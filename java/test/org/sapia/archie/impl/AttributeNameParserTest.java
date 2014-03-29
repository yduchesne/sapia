package org.sapia.archie.impl;

import org.sapia.archie.Name;

import junit.framework.TestCase;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class AttributeNameParserTest extends TestCase{
  
  public AttributeNameParserTest(String name){
    super(name);
  }
  
  public void testParseName() throws Exception{
    String name = "/some/object/name?attr1=value1&attr2=value2";
    AttributeNameParser p = new AttributeNameParser();
    Name n = p.parse(name);
    doCheckName(n);
    super.assertTrue(p.asString(n).startsWith("/some/object"));    
  }
  
  public void testParseNamePart() throws Exception{
    String name = "name?attr1=value1&attr2=value2";
    AttributeNameParser p = new AttributeNameParser();
    AttributeNamePart np = (AttributeNamePart)p.parseNamePart(name);
    super.assertEquals("name", np.getName());
    super.assertEquals("value1", np.getAttributes().getProperty("attr1"));
    super.assertEquals("value2", np.getAttributes().getProperty("attr2"));        
  }
  
  public void testAsString() throws Exception{
    String name = "/some/object/name?attr1=value1&attr2=value2";
    AttributeNameParser p = new AttributeNameParser();
    Name n = p.parse(name);
    name = p.asString(n);
    n = p.parse(name);
    doCheckName(n);
  }
  
  private void doCheckName(Name n) throws Exception{
    AttributeNamePart pn;
    for(int i = 0; i < n.count() ; i++){
      pn = (AttributeNamePart)n.get(i);
      if(i == 0){
        super.assertEquals("", pn.asString());
      }
      else if(i == 1){
        super.assertEquals("some", pn.asString());
      }
      else if(i == 2){
        super.assertEquals("object", pn.asString());        
      }
      else if(i == 3){
        super.assertEquals("value1", pn.getAttributes().getProperty("attr1"));
        super.assertEquals("value2", pn.getAttributes().getProperty("attr2"));        
        super.assertEquals("name", pn.getName());        
      }      
    }
    
  }
  
}
