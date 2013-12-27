/*
 * Created on 27-Feb-2004
 */
package org.sapia.archie.jndi;

import java.util.HashSet;
import java.util.Set;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;

import org.sapia.archie.impl.DefaultNode;

import junit.framework.TestCase;

/**
 * @author yduchesne
 * 
 * 27-Feb-2004
 */
public class JndiContextTest extends TestCase{
  
  private JndiContext _ctx;
  
  public JndiContextTest(String name){
    super(name);
  }
  
  /**
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception {
    _ctx = new JndiContext(new DefaultNode());
  }
  
  /**
   * @see junit.framework.TestCase#tearDown()
   */
  protected void tearDown() throws Exception {
    super.tearDown();
  }


  
  public void testBindSimple() throws Exception{
    String name = "name";
    _ctx.bind(name, "someObject");
    try{
      _ctx.bind(_ctx.getNameParser(name).parse(name), "someObject");
      throw new Exception("Duplicate binding should be forbidden");
    }catch(NameAlreadyBoundException e){
      // ok
    }
  }
  
  public void testBindComplex() throws Exception{
    String name = "part1/part2";
    _ctx.bind(name, "someObject");
    try{
      _ctx.bind(_ctx.getNameParser(name).parse(name), "someObject");
      throw new Exception("Duplicate binding should be forbidden");
    }catch(NameAlreadyBoundException e){
      // ok
    }
  }  
  
  public void testRebind() throws Exception{
    String name = "name";
    _ctx.bind(name, "someObject");
    super.assertEquals(_ctx.lookup(name), "someObject");
    _ctx.rebind(_ctx.getNameParser(name).parse(name), "someObject2");
    super.assertEquals(_ctx.lookup(name), "someObject2");    
  }
  
  public void testUnbind() throws Exception{
    String name = "name";
    _ctx.bind(name, "someObject");
    super.assertEquals(_ctx.lookup(name), "someObject");
    _ctx.unbind(_ctx.getNameParser(name).parse(name));
    try{
      _ctx.lookup(name);
      throw new Exception("Object was not unbound");
    }catch(NameNotFoundException e){
      //ok
    }    
    
  }
  
  public void testComposeStringName() throws Exception{
    String name1 = "part1/part2";
    String name2 = "part3/part4";    
    String name  = _ctx.composeName(name2, name1);
    super.assertEquals("part1/part2/part3/part4", name);
  }
  
  public void testComposeName() throws Exception{
    Name name1     = _ctx.getNameParser("").parse("part1/part2");
    Name name2     = _ctx.getNameParser("").parse("part3/part4");    
    Name name      = _ctx.composeName(name2, name1);
    Name toCompare = _ctx.getNameParser("").parse("part1/part2/part3/part4");
    super.assertEquals(toCompare, name);
  }  
  
  public void testCreateSubcontext() throws Exception{
    Context ctx = _ctx.createSubcontext("some/sub/context");
    ctx.bind("name", "value");
    _ctx.lookup("some/sub/context/name");
    
    Name name;
    ctx = _ctx.createSubcontext(name = _ctx.getNameParser("").parse("some/sub/context"));
    _ctx.bind(_ctx.getNameParser("").parse("name"), "value");
    _ctx.lookup(_ctx.getNameParser("").parse("some/sub/context/name"));
  }
  
  public void destroySubcontext() throws Exception{
    Context ctx = _ctx.createSubcontext("some/sub/context");
    ctx.bind("name", "value");
    _ctx.lookup("some/sub/context/name");
    _ctx.destroySubcontext("some/sub/context");
    try{
      _ctx.lookup("some/sub/context/name");
      throw new Exception("Context was not destroyed");
    }catch(NameNotFoundException e){
      //ok;
    }
  }
  
  public void testList() throws Exception{
    _ctx.bind("path/name1", "value1");  
    _ctx.bind("path/name2", "value2");
    NamingEnumeration enumeration = _ctx.listObjects("path");
    
    Set values = new HashSet();
    values.add("value1");
    values.add("value2");
    while(enumeration.hasMore()){
      super.assertTrue(values.contains(enumeration.next()));
    }
  }

  public void testListBindings() throws Exception{
    _ctx.bind("some/path/name1", "value1");  
    _ctx.bind("some/path/name2", "value2");
    NamingEnumeration enumeration = _ctx.listBindings("path");
    
    while(enumeration.hasMore()){
      Binding b = (Binding)enumeration.next();
      super.assertTrue(b.getName().equals("name1") || b.getName().equals("name2"));
      super.assertTrue(b.getObject().equals("value1") || b.getObject().equals("value2"));      
    }
  }  
  
  public void testLookup() throws Exception{
    _ctx.bind("some/object/name", "SomeObject");
    super.assertEquals(_ctx.lookup("some/object/name"), "SomeObject");
  }
  
  public void testLookupName() throws Exception{
    Name n = _ctx.getNameParser("").parse("some/object/name");
    _ctx.bind(n, "SomeObject");
    super.assertEquals(_ctx.lookup(n), "SomeObject");
  }  
  
  public void testDuplicateName() throws Exception {
    String name = "part1/part2";
    _ctx.bind(name, "someObject");
    _ctx.bind(name+"/part3", "anotherObject");
    super.assertEquals(_ctx.lookup("part1/part2"), "someObject");
    super.assertEquals(_ctx.lookup("part1/part2/part3"), "anotherObject");
  }
}
