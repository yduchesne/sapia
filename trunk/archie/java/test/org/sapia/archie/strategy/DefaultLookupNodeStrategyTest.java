package org.sapia.archie.strategy;

import junit.framework.TestCase;

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
public class DefaultLookupNodeStrategyTest extends TestCase {
  public DefaultLookupNodeStrategyTest(String name) {
    super(name);
  }

  public void testLookupCreate() throws Exception {
    DefaultLookupNodeStrategy strat  = new DefaultLookupNodeStrategy(true);
    DefaultNode               root   = new DefaultNode();
    Node                      child3 = (Node) strat.lookup(root.getNameParser()
                                                               .parse("some/node/path"),
                                                           root);
    super.assertEquals("path", child3.getName().asString());

    Node child1 = (Node) strat.lookup(root.getNameParser().parse("some"), root);
    System.out.println(child1.getParent());
    super.assertTrue(child1.getParent() == root);

    Node child2 = (Node) strat.lookup(root.getNameParser().parse("some/node"),
                                      root);
    super.assertTrue(child2.getParent() == child1);

    child3 = (Node) strat.lookup(root.getNameParser().parse("some/node/path"),
                                 root);
    super.assertTrue(child3.getParent() == child2);
  }

  public void testLookupNotCreate() throws Exception {
    DefaultLookupNodeStrategy strat = new DefaultLookupNodeStrategy(false);
    DefaultNode               root = new DefaultNode();
    String                    path = "some/node/path";

    try {
      Node child = (Node) strat.lookup(root.getNameParser().parse(path), root);
      throw new Exception("Node should not have been created for " + path);
    } catch (NotFoundException e) {
      // ok		
    }
  }

  public void testLookup() throws Exception {
    DefaultLookupNodeStrategy strat = new DefaultLookupNodeStrategy(true);
    DefaultNode               root  = new DefaultNode();
    Node                      child = (Node) strat.lookup(root.getNameParser()
                                                              .parse("some/node/path"),
                                                          root);

    strat = new DefaultLookupNodeStrategy(false);
    child = (Node) strat.lookup(root.getNameParser().parse("some/node/path"),
                                root);
                                
    child = (Node) strat.lookup(root.getNameParser().parse("some"),
                                root);                                
  }
}
