package org.sapia.util.xml.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.sapia.util.xml.Namespace;

/**
 *
 *
 * @author Jean-Cedric Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class NamespaceTest extends TestCase{

  public static void main(String[] args) {
    TestRunner.run(NamespaceTest.class);
  }

  public NamespaceTest(String aName) {
    super(aName);
  }

  /**
   *
   */
  public void testEmptyNamespace() throws Exception {
    Namespace aNamespace = new Namespace();
    assertNotNull(aNamespace);
    assertNull(aNamespace.getURI());
    assertNull(aNamespace.getPrefix());
    assertTrue(aNamespace.hashCode() != 0);
    assertEquals(aNamespace, new Namespace());
  }

  /**
   *
   */
  public void testValidNamespace() throws Exception {
    Namespace aNamespace = new Namespace();
    aNamespace.setURI("http://schemas.sapia.org/");
    aNamespace.setPrefix("A");
    assertNotNull(aNamespace);
    assertEquals("http://schemas.sapia.org/", aNamespace.getURI());
    assertEquals("A", aNamespace.getPrefix());
    assertTrue(aNamespace.hashCode() != 0);
    assertTrue(!aNamespace.equals(new Namespace()));
    assertEquals(aNamespace, new Namespace("http://schemas.sapia.org/", "A"));
  }
}
