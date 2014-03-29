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
public class NameTest extends TestCase {
  public NameTest(String name) {
    super(name);
  }

  public void testAddPart() throws Exception {
    Name n = new Name();
    n.add(new DefaultNamePart("part"));
    super.assertEquals(1, n.count());
  }

  public void testFirst() throws Exception {
    Name n = new Name();
    n.add(new DefaultNamePart("part"));
    super.assertEquals("part", n.first().asString());
    n.add(new DefaultNamePart("otherPart"));
    super.assertEquals("part", n.first().asString());
  }

  public void testLast() throws Exception {
    Name n = new Name();
    n.add(new DefaultNamePart("part"));
    super.assertEquals("part", n.last().asString());
    n.add(new DefaultNamePart("otherPart"));
    super.assertEquals("otherPart", n.last().asString());
  }

  public void testChopLast() throws Exception {
    Name n = new Name();
    n.add(new DefaultNamePart("part"));
    n.add(new DefaultNamePart("otherPart"));
    super.assertEquals("otherPart", n.chopLast().asString());
    super.assertEquals(1, n.count());
    super.assertEquals("part", n.first().asString());
    super.assertEquals("part", n.last().asString());
  }

  public void testIteration() throws Exception {
    Name n = new Name();
    n.add(new DefaultNamePart("path1")).add(new DefaultNamePart("path2")).add(new DefaultNamePart("path3"));

    String[] parts = new String[] { "path1", "path2", "path3" };

    NamePart current;
    int      count = 0;

    while (n.hasNextPart()) {
      current = n.nextPart();
      super.assertEquals(parts[count++], current.asString());
    }

    super.assertEquals(3, count);
    n.reset();
    super.assertEquals(0, n.getCurrentIndex());
  }

  public void testFrom() throws Exception {
    Name n = new Name();
    n.add(new DefaultNamePart("path1")).add(new DefaultNamePart("path2")).add(new DefaultNamePart("path3"));

    Name from = n.getFrom(1);
    super.assertEquals(2, from.count());
    super.assertEquals("path2", from.get(0).asString());
    super.assertEquals("path3", from.get(1).asString());
  }

  public void testTo() throws Exception {
    Name n = new Name();
    n.add(new DefaultNamePart("path1")).add(new DefaultNamePart("path2")).add(new DefaultNamePart("path3"));

    Name to = n.getTo(2);
    super.assertEquals(2, to.count());
    super.assertEquals("path1", to.get(0).asString());
    super.assertEquals("path2", to.get(1).asString());
  }
  
  public void testAdd() throws Exception{
    Name n = new Name();
    n.add(new DefaultNamePart("path1")).add(new DefaultNamePart("path2"));
    
    Name n2 = new Name();
    n2.add(new DefaultNamePart("path3")).add(new DefaultNamePart("path4"));
    
    n.add(n2);
    super.assertEquals("path1", n.get(0).asString());
    super.assertEquals("path2", n.get(1).asString());
    super.assertEquals("path3", n.get(2).asString());
    super.assertEquals("path4", n.get(3).asString());            
  }
  
  public void testAddAtBeginning() throws Exception{
    Name n = new Name();
    n.add(new DefaultNamePart("path1"));
    n.addAt(0, new DefaultNamePart("path2"));
    super.assertEquals("path1", n.get(1).asString());
    super.assertEquals("path2", n.get(0).asString());
  }
  
  public void testAddAtEnd() throws Exception{
    Name n = new Name();
    n.add(new DefaultNamePart("path1"));
    n.addAt(1, new DefaultNamePart("path2"));
    super.assertEquals("path1", n.get(0).asString());
    super.assertEquals("path2", n.get(1).asString());
  }  
  
  public void testRemoveAtBeginning() throws Exception{
    Name n = new Name();
    n.add(new DefaultNamePart("path1"));
    n.add(new DefaultNamePart("path2"));
    super.assertEquals("path1", n.get(0).asString());
    super.assertEquals("path2", n.get(1).asString());
    n.removeAt(0);
    super.assertEquals(1, n.count());
    super.assertEquals("path2", n.get(0).asString());
  }
  
  public void testRemoveAtEnd() throws Exception{
    Name n = new Name();
    n.add(new DefaultNamePart("path1"));
    n.add(new DefaultNamePart("path2"));
    super.assertEquals("path1", n.get(0).asString());
    super.assertEquals("path2", n.get(1).asString());
    n.removeAt(1);
    super.assertEquals(1, n.count());
    super.assertEquals("path1", n.get(0).asString());
  }
  
  public void testEndsWith() throws Exception{
    Name n1 = new Name();
    n1.add(new DefaultNamePart("path1"));
    n1.add(new DefaultNamePart("path2"));
    Name n2 = new Name();
    n2.add(new DefaultNamePart("path2"));
    super.assertTrue(n1.endsWith(n1));    
    super.assertTrue(n1.endsWith(n2));    
  }
  
  public void testStartsWith() throws Exception{
    Name n1 = new Name();
    n1.add(new DefaultNamePart("path1"));
    n1.add(new DefaultNamePart("path2"));
    Name n2 = new Name();
    n2.add(new DefaultNamePart("path2"));
    
    Name n3 = new Name();
    n2.add(new DefaultNamePart("path3"));    
    super.assertTrue(n1.startsWith(n1));    
    super.assertTrue(!n1.startsWith(n2));
    super.assertTrue(n1.startsWith(n3));
    super.assertTrue(!n3.startsWith(n1));    
  }
}
