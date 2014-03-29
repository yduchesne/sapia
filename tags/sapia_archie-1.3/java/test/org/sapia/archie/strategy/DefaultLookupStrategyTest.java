package org.sapia.archie.strategy;

import junit.framework.TestCase;

import org.junit.Test;
import org.sapia.archie.*;
import org.sapia.archie.impl.*;


/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class DefaultLookupStrategyTest extends TestCase {
  public DefaultLookupStrategyTest(String arg0) {
    super(arg0);
  }

  public void testRelativeLookup() throws Exception {
    DefaultNode root  = new DefaultNode();
    Name        name  = root.getNameParser().parse("some/path/name");
    NamePart    part  = name.last();
    Node        child = (Node) new DefaultLookupNodeStrategy(true).lookup(name,
                                                                          root);
    child.putValue(part, "SomeObject", false);
    new DefaultLookupStrategy(false).lookup(name.add(part), root);
  }

  public void testAbsoluteLookup() throws Exception {
    DefaultNode root  = new DefaultNode();
    Name        name  = root.getNameParser().parse("some/path/name");
    NamePart    part  = name.chopLast();
    Node        child = (Node) new DefaultLookupNodeStrategy(true).lookup(name,
                                                                          root);
    child.putValue(part, "SomeObject", false);

    Name rootName = root.getNameParser().parse("/some/path/name");
    new DefaultLookupStrategy(false).lookup(rootName, child);
  }
  
  public void testLookupCreateMissingNodes() throws Exception {
    DefaultNode root  = new DefaultNode();
    Name        name  = root.getNameParser().parse("some/path/name");
    try {
      new DefaultLookupStrategy(true).lookup(name, root);
      fail("Expected failure");
    } catch (NotFoundException e) {
      //ok
    }
  }
}
