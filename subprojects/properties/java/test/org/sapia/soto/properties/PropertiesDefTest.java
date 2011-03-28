package org.sapia.soto.properties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.sapia.util.text.SystemContext;

import junit.framework.TestCase;

public class PropertiesDefTest extends TestCase {

  public PropertiesDefTest(String arg0) {
    super(arg0);
  }

  /*
   * Test method for 'org.sapia.soto.properties.PropertiesDef.render(TemplateContextIF)'
   */
  public void testRender() throws Exception{
    PropertiesDef root = new PropertiesDef(null);
    root.setName("root");
    root.getProperties().setProperty("root_key", "root_value");

    PropertiesDef child1 = new PropertiesDef(null);
    child1.setName("child1");
    child1.setDepends("root");
    child1.getProperties().setProperty("child1_key", "${root_key}");

    PropertiesDef child2 = new PropertiesDef(null);   
    child2.setName("child2");
    child2.setDepends("root");
    child2.getProperties().setProperty("child2_key", "${root_key}");    
    
    PropertiesDef child11 = new PropertiesDef(null);
    child11.setName("child11");
    child11.setDepends("root, child1");
    child11.getProperties().setProperty("child11_key1", "${root_key}");
    child11.getProperties().setProperty("child11_key2", "${child1_key}_1");
    
    Map props = new HashMap();
    props.put(root.getName(), root);
    props.put(child1.getName(), child1);
    props.put(child11.getName(), child11);
    props.put(child2.getName(), child2);
    
    root.visit(new HashSet(), props);
    child1.visit(new HashSet(), props);
    child2.visit(new HashSet(), props);
    child11.visit(new HashSet(), props);
    
    root.render(new HashSet(), new SystemContext());
    child1.render(new HashSet(), new SystemContext());
    child2.render(new HashSet(), new SystemContext());
    child11.render(new HashSet(), new SystemContext());
    
    super.assertEquals("root_value", child1.getProperties().getProperty("child1_key"));
    super.assertEquals("root_value", child11.getProperties().getProperty("child11_key1"));
    super.assertEquals("root_value_1", child11.getProperties().getProperty("child11_key2"));    
    super.assertEquals("root_value", child2.getProperties().getProperty("child2_key"));
  }
  
  public void testRenderCircReference() throws Exception{
    PropertiesDef a = new PropertiesDef(null);
    a.setName("a");
    a.setDepends("b");
    a.getProperties().setProperty("a_key", "${b_key}");

    PropertiesDef b = new PropertiesDef(null);
    b.setName("b");
    b.setDepends("a");
    b.getProperties().setProperty("b_key", "${a_key}");
    
    Map props = new HashMap();
    props.put("a", a);
    props.put("b", b);
    
    a.visit(new HashSet(), props);
    b.visit(new HashSet(), props);
    try{
      a.render(new HashSet(), new SystemContext());
      fail("Did not detect circular reference");
    }catch(IllegalStateException e){
      // ok
    }
    
  }

}
