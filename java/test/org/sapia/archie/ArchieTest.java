package org.sapia.archie;

import junit.framework.TestCase;

import org.sapia.archie.impl.DefaultNode;


/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ArchieTest extends TestCase{
  
  public ArchieTest(String name){
    super(name);
  }
  public void testBind() throws Exception {
    Archie arch = new Archie(new DefaultNode());
    arch.bind(arch.getNameParser().parse("name"), "value");
    try{
      arch.bind(arch.getNameParser().parse("name"), "value2");
      throw new Exception("Duplicate binding should not have been authorized");
    }catch(DuplicateException e){
      //ok;
    }
  }
  public void testRebind() throws Exception {
    Archie arch = new Archie(new DefaultNode());
    arch.bind(arch.getNameParser().parse("name"), "value");
    arch.rebind(arch.getNameParser().parse("name"), "value2");
  }  
  
  public void testLookup() throws Exception{
    Archie arch = new Archie(new DefaultNode());
    Name n;
    arch.bind(n = arch.getNameParser().parse("name"), "value");
    super.assertEquals(arch.lookup(n), "value");
    arch.rebind(n, "value2");
    super.assertEquals(arch.lookup(n), "value2");    
  }
  
  public void testLookupRootNode() throws Exception{
    Archie arch = new Archie(new DefaultNode());
    Node n = arch.lookupNode(arch.getNameParser().parse("/"), false);
    super.assertTrue(n.getParent() == null);
  }
  
  public void testLookupComplexNode() throws Exception{
    Archie arch = new Archie(new DefaultNode());
    Name n = arch.getNameParser().parse("path/to/object");
    arch.bind(n, "value");
    Node node = arch.lookupNode(arch.getNameParser().parse("path/to"), false);
  }  
  
  public void testNodeNameAsString() throws Exception{
    Archie arch = new Archie(new DefaultNode());
    Name n = arch.getNameParser().parse("path/to/object");
    arch.bind(n, "value");    
    Node node = arch.lookupNode(arch.getNameParser().parse("path/to"), false);
    super.assertTrue(arch.getNameParser().asString(node.getAbsolutePath()).startsWith("/path/to"));
  }
  
  public void testUnbind() throws Exception{
    Archie arch = new Archie(new DefaultNode());
    Name n = arch.getNameParser().parse("path/to/object");
    arch.bind(n, "value");
    arch.lookup(n);
    arch.unbind(n);
    try{
      arch.lookup(n);
      throw new Exception("Object not unbound");
    }catch(NotFoundException e){
      // ok
    }
  }
  
  
}
