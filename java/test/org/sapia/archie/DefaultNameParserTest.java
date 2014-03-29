package org.sapia.archie;

import org.sapia.archie.impl.*;

import junit.framework.TestCase;


/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class DefaultNameParserTest extends TestCase {
  public DefaultNameParserTest(String arg0) {
    super(arg0);
  }

  public void testParseAbsoluteName() throws Exception {
    DefaultNameParser p = new DefaultNameParser();
    Name              n = p.parse("/path1/path2/name");
    super.assertEquals(4, n.count());
    super.assertEquals("", n.get(0).asString());
    super.assertEquals("path1", n.get(1).asString());
    super.assertEquals("path2", n.get(2).asString());
    super.assertEquals("name", n.get(3).asString());
    
    super.assertEquals("/path1/path2/name", p.asString(n));
    
  }

  public void testParseRelativeName() throws Exception {
    DefaultNameParser p = new DefaultNameParser();
    Name              n = p.parse("path1/path2/name");
    super.assertEquals(3, n.count());
    super.assertEquals("path1", n.get(0).asString());
    super.assertEquals("path2", n.get(1).asString());
    super.assertEquals("name", n.get(2).asString());
  }
}
