package org.sapia.soto.properties;

import java.io.File;

import org.sapia.soto.Debug;
import org.sapia.soto.NotFoundException;
import org.sapia.soto.SotoContainer;
import org.sapia.soto.properties.example.PropertyExample;

import junit.framework.TestCase;

public class PropertyServiceImplTest extends TestCase {
  
  private SotoContainer _container;
  private PropertyService _props;
  
  public PropertyServiceImplTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    _container = new SotoContainer();
    _container.load(new File("etc/properties/propService.xml"));
    _container.start();
    _props = (PropertyService)_container.lookup(PropertyService.class);
  }

  protected void tearDown() throws Exception {
    _container.dispose();
  }
  
  public void testGetSimpleProperties(){
    super.assertEquals("sna", _props.getProperties("main").getProperty("bar"));
    super.assertEquals("sna", _props.getProperties("main").getProperty("chained"));    
    super.assertEquals("sna", _props.getProperties("secondary").getProperty("fu"));
  }

  public void testGetComplexProperties(){
    super.assertEquals("sna", _props.getProperties(new String[]{"main", "secondary"}).getProperty("bar"));
    super.assertEquals("sna", _props.getProperties(new String[]{"main", "secondary"}).getProperty("fu"));
  }  
  
  public void testProperties() throws Exception{
    PropertyExample bean = (PropertyExample)_container.lookup("bean");
    super.assertEquals("sna", bean.getProperty());
    super.assertEquals("sna", bean.getProperties().getProperty("fu"));
  }
  
  public void testInheritance() throws Exception{
    super.assertEquals("global", _props.getProperties("secondary").getProperty("inherited"));    
  }
  
  public void testUriContent() throws Exception{
    PropertyExample bean = (PropertyExample)_container.lookup("bean");
    super.assertEquals("<content>sna</content>", bean.getContent().trim());
  }
  
  public void testIf() throws Exception{
    PropertyExample bean = (PropertyExample)_container.lookup("bean2");
    super.assertEquals("sna", bean.getProperties().getProperty("bar"));
    try{
      bean = (PropertyExample)_container.lookup("bean3");
      fail("Should not have configured bean");
    }catch(NotFoundException e){
      //ok
    }
  }
  
  public void testUnless() throws Exception{
    try{
      _container.lookup("bean6");
      fail("Should not have bean able to look up bean6");
    }catch(NotFoundException e){
      //ok
    }
  }  

  public void testWhen() throws Exception{
    PropertyExample bean = (PropertyExample)_container.lookup("bean4");
    super.assertEquals("test2", bean.getProperty());
  }
  
  public void testInclude() throws Exception{
    PropertyExample bean = (PropertyExample)_container.lookup("bean5");
    super.assertEquals("test", bean.getProperty());    
  }
  
}
