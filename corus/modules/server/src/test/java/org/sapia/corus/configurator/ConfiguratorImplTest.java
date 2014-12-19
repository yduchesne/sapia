package org.sapia.corus.configurator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sapia.corus.client.common.Arg;
import org.sapia.corus.client.common.ArgFactory;
import org.sapia.corus.client.common.NameValuePair;
import org.sapia.corus.client.services.configurator.Configurator.PropertyScope;
import org.sapia.corus.client.services.configurator.Property;
import org.sapia.corus.client.services.configurator.Tag;
import org.sapia.corus.client.services.db.DbMap;
import org.sapia.corus.client.services.db.DbModule;
import org.sapia.corus.client.services.db.persistence.ClassDescriptor;
import org.sapia.corus.client.services.event.EventDispatcher;
import org.sapia.corus.configurator.PropertyChangeEvent.Type;
import org.sapia.corus.db.HashDbMap;
import org.sapia.ubik.util.Collects;

@RunWith(MockitoJUnitRunner.class)
public class ConfiguratorImplTest {
  
  @Mock
  private DbMap<String, ConfigProperty> serverProperties;
  
  @Mock
  private DbMap<String, ConfigProperty> processProperties;
  
  @Mock
  private DbMap<String, ConfigProperty> tagsProperties;
  
  private DbMap<String, ConfigProperty> internalConfig;
 
  @Mock
  private EventDispatcher dispatcher;
  
  private DbModule db;
  
  private ConfiguratorImpl configurator;
  
  @Before
  public void setUp() {
    
    db = new DbModule() {
      @Override
      public String getRoleName() {
        return DbModule.ROLE;
      }
      
      @Override
      public <K, V> DbMap<K, V> getDbMap(Class<K> keyType, Class<V> valueType,
          String name) {
        return new HashDbMap<>(new ClassDescriptor<>(valueType));
      }
    };
    
    internalConfig = new HashDbMap<>(new ClassDescriptor<>(ConfigProperty.class));
    
    configurator = new ConfiguratorImpl();
    configurator.setServerProperties(new PropertyStore(serverProperties));
    configurator.setProcessProperties(new PropertyStore(processProperties));
    configurator.setTags(tagsProperties);
    configurator.setDispatcher(dispatcher);
    configurator.setInternalConfig(internalConfig);
    configurator.setDb(db);
  }

  @Test
  public void testAddProcessProperty() {
    configurator.addProperty(PropertyScope.PROCESS, "test", "testValue", new HashSet<String>());
    
    verify(processProperties).put(eq("test"), eq(new ConfigProperty("test", "testValue")));
    verify(dispatcher).dispatch(any(PropertyChangeEvent.class));
    
    verify(dispatcher).dispatch(eq(new PropertyChangeEvent("test", "testValue", PropertyScope.PROCESS, Type.ADD)));
  }
  
  @Test
  public void testAddProcessProperty_categories() {
    configurator.addProperty(PropertyScope.PROCESS, "test", "testValue", Collects.arrayToSet("cat1", "cat2"));
    
    assertEquals("testValue", configurator.getProcessPropertiesByCategory().get("cat1").getProperty("test"));
    assertEquals("testValue", configurator.getProcessPropertiesByCategory().get("cat2").getProperty("test"));

    
    verify(processProperties, never()).put(eq("test"), eq(new ConfigProperty("test", "testValue")));
    verify(dispatcher).dispatch(eq(new PropertyChangeEvent("test", "testValue", "cat1", PropertyScope.PROCESS, Type.ADD)));
    verify(dispatcher).dispatch(eq(new PropertyChangeEvent("test", "testValue", "cat2", PropertyScope.PROCESS, Type.ADD)));
  }
  
  @Test
  public void testAddProcessProperties() {
    Properties props = new Properties();
    props.setProperty("test1", "value1");
    props.setProperty("test2", "value2");
    
    configurator.addProperties(PropertyScope.PROCESS, props, new HashSet<String>(), false);
    
    verify(processProperties, never()).remove(any(String.class));
    verify(processProperties).put(eq("test1"), eq(new ConfigProperty("test1", "value1")));
    verify(processProperties).put(eq("test2"), eq(new ConfigProperty("test2", "value2")));
    verify(dispatcher).dispatch(eq(new PropertyChangeEvent("test1", "value1", PropertyScope.PROCESS, Type.ADD)));
    verify(dispatcher).dispatch(eq(new PropertyChangeEvent("test2", "value2", PropertyScope.PROCESS, Type.ADD)));
  }
  
  @Test
  public void testAddProcessProperties_categories() {
    Properties props = new Properties();
    props.setProperty("test1", "value1");
    props.setProperty("test2", "value2");
    
    configurator.addProperties(PropertyScope.PROCESS, props, Collects.arrayToSet("cat1", "cat2"), false);

    assertEquals("value1", configurator.getProcessPropertiesByCategory().get("cat1").getProperty("test1"));
    assertEquals("value2", configurator.getProcessPropertiesByCategory().get("cat2").getProperty("test2"));
    
    verify(processProperties, never()).remove(any(String.class));
    verify(processProperties, never()).put(anyString(), any(ConfigProperty.class));
    
    verify(dispatcher).dispatch(eq(new PropertyChangeEvent("test1", "value1", "cat1", PropertyScope.PROCESS, Type.ADD)));
    verify(dispatcher).dispatch(eq(new PropertyChangeEvent("test1", "value1", "cat2", PropertyScope.PROCESS, Type.ADD)));
    verify(dispatcher).dispatch(eq(new PropertyChangeEvent("test2", "value2", "cat1", PropertyScope.PROCESS, Type.ADD)));
    verify(dispatcher).dispatch(eq(new PropertyChangeEvent("test2", "value2", "cat2", PropertyScope.PROCESS, Type.ADD)));
  }

  @Test
  public void testAddServerProperty() {
    configurator.addProperty(PropertyScope.SERVER, "test", "testValue", new HashSet<String>());
    
    verify(serverProperties).put(eq("test"), eq(new ConfigProperty("test", "testValue")));
    verify(dispatcher).dispatch(eq(new PropertyChangeEvent("test", "testValue", PropertyScope.SERVER, Type.ADD)));
  }
  
  @Test
  public void testAddServerProperties() {
    Properties props = new Properties();
    props.setProperty("test1", "value1");
    props.setProperty("test2", "value2");
    
    configurator.addProperties(PropertyScope.SERVER, props, new HashSet<String>(), false);
    
    verify(serverProperties, never()).remove(any(String.class));
    verify(serverProperties).put(eq("test1"), eq(new ConfigProperty("test1", "value1")));
    verify(serverProperties).put(eq("test2"), eq(new ConfigProperty("test2", "value2")));
    
    verify(dispatcher).dispatch(eq(new PropertyChangeEvent("test1", "value1", PropertyScope.SERVER, Type.ADD)));
    verify(dispatcher).dispatch(eq(new PropertyChangeEvent("test2", "value2", PropertyScope.SERVER, Type.ADD)));

  }
  
  @Test
  public void testGetProcessProperty() {
    when(processProperties.get(anyString())).thenReturn(new ConfigProperty("test", "value"));
    
    configurator.getProperty("test", new ArrayList<String>());
    
    verify(processProperties).get(eq("test"));
  }
  
  @Test
  public void testGetServerProperty() {
    when(serverProperties.get(anyString())).thenReturn(new ConfigProperty("test", "value"));
    
    configurator.getProperty("test", new ArrayList<String>());
    
    verify(serverProperties).get(eq("test"));
  }

  @Test
  public void testRemoveProcessProperty() {
    when(processProperties.keys()).thenReturn(Collects.arrayToList("test1", "test2").iterator());
    when(processProperties.get("test1")).thenReturn(new ConfigProperty("test1", "value1"));
    when(processProperties.get("test2")).thenReturn(new ConfigProperty("test2", "value2"));
    
    configurator.removeProperty(PropertyScope.PROCESS, ArgFactory.any(), new HashSet<Arg>());
    
    verify(processProperties).remove(eq("test1"));
    verify(processProperties).remove(eq("test2"));
    verify(dispatcher).dispatch(eq(new PropertyChangeEvent("test1", "value1", PropertyScope.PROCESS, Type.REMOVE)));
    verify(dispatcher).dispatch(eq(new PropertyChangeEvent("test2", "value2", PropertyScope.PROCESS, Type.REMOVE)));
  }
  
  @Test
  public void testRemoveProcessProperty_categories() {
    configurator.store("cat1", true).addProperty("test", "value");
    configurator.store("cat2", true).addProperty("test", "value");

    assertEquals("value", configurator.store("cat2", false).getProperty("test"));
    configurator.removeProperty(PropertyScope.PROCESS, ArgFactory.any(), Collects.arrayToSet(ArgFactory.parse("cat1")));
    assertEquals("value", configurator.store("cat2", false).getProperty("test"));

    verify(processProperties, never()).remove(eq("test"));
    assertNull(configurator.store("cat1", false).getProperty("test"));
    assertEquals("value", configurator.store("cat2", false).getProperty("test"));
    verify(dispatcher).dispatch(eq(new PropertyChangeEvent("test", "value", "cat1", PropertyScope.PROCESS, Type.REMOVE)));
    verify(dispatcher, never()).dispatch(eq(new PropertyChangeEvent("test", "value", "cat2", PropertyScope.PROCESS, Type.REMOVE)));
  }

  @Test
  public void testRemoveServerProperty() {
    when(serverProperties.keys()).thenReturn(Collects.arrayToList("test1", "test2").iterator());
    when(serverProperties.get("test1")).thenReturn(new ConfigProperty("test1", "value1"));
    when(serverProperties.get("test2")).thenReturn(new ConfigProperty("test2", "value2"));
    
    configurator.removeProperty(PropertyScope.SERVER, ArgFactory.any(), new HashSet<Arg>());
    
    verify(serverProperties).remove(eq("test1"));
    verify(serverProperties).remove(eq("test2"));
    verify(dispatcher).dispatch(eq(new PropertyChangeEvent("test1", "value1", PropertyScope.SERVER, Type.REMOVE)));
    verify(dispatcher).dispatch(eq(new PropertyChangeEvent("test2", "value2", PropertyScope.SERVER, Type.REMOVE)));
  }
  
  @Test
  public void testGetProcessProperties() {
    when(processProperties.keys()).thenReturn(Collects.arrayToList("test1", "test2").iterator());
    when(processProperties.get("test1")).thenReturn(new ConfigProperty("test1", "value1"));
    when(processProperties.get("test2")).thenReturn(new ConfigProperty("test2", "value2"));

    Properties props = configurator.getProperties(PropertyScope.PROCESS, new ArrayList<String>());

    assertEquals("value1", props.getProperty("test1"));
    assertEquals("value2", props.getProperty("test2"));
  }
  
  @Test
  public void testGetProcessProperties_categories_first_category_match() {
    configurator.store("cat1", true).addProperty("test", "value1");
    configurator.store("cat2", true).addProperty("test2", "value2");
    
    when(processProperties.keys()).thenReturn(Collects.arrayToList("test").iterator());
    when(processProperties.get("test")).thenReturn(new ConfigProperty("test", "value"));

    Properties props = configurator.getProperties(PropertyScope.PROCESS, Collects.arrayToList("cat1", "cat2"));

    assertEquals("value1", props.getProperty("test"));
  }
  
  @Test
  public void testGetProcessProperties_categories_last_category_match() {
    configurator.store("cat1", true).addProperty("test1", "value1");
    configurator.store("cat2", true).addProperty("test", "value2");
    
    when(processProperties.keys()).thenReturn(Collects.arrayToList("test").iterator());
    when(processProperties.get("test")).thenReturn(new ConfigProperty("test", "value"));

    Properties props = configurator.getProperties(PropertyScope.PROCESS, Collects.arrayToList("cat1", "cat2"));

    assertEquals("value2", props.getProperty("test"));
  }
  
  @Test
  public void testGetProcessProperties_categories_no_category_match() {
    configurator.store("cat1", true).addProperty("test1", "value1");
    configurator.store("cat2", true).addProperty("test2", "value2");
    
    when(processProperties.keys()).thenReturn(Collects.arrayToList("test").iterator());
    when(processProperties.get("test")).thenReturn(new ConfigProperty("test", "value"));

    Properties props = configurator.getProperties(PropertyScope.PROCESS, Collects.arrayToList("cat1", "cat2"));

    assertEquals("value", props.getProperty("test"));
  }

  @Test
  public void testGetServerProperties() {
    when(serverProperties.keys()).thenReturn(Collects.arrayToList("test1", "test2").iterator());
    when(serverProperties.get("test1")).thenReturn(new ConfigProperty("test1", "value1"));
    when(serverProperties.get("test2")).thenReturn(new ConfigProperty("test2", "value2"));

    Properties props = configurator.getProperties(PropertyScope.SERVER, new ArrayList<String>());

    assertEquals("value1", props.getProperty("test1"));
    assertEquals("value2", props.getProperty("test2"));
  }
  
  @Test
  public void testGetProcessPropertyList() {
    when(processProperties.keys()).thenReturn(Collects.arrayToList("test1", "test2").iterator());
    when(processProperties.get("test1")).thenReturn(new ConfigProperty("test1", "value1"));
    when(processProperties.get("test2")).thenReturn(new ConfigProperty("test2", "value2"));

    List<Property> props = configurator.getPropertiesList(PropertyScope.PROCESS, new ArrayList<String>());
    
    assertTrue(props.contains(new Property("test1", "value1", null)));
    assertTrue(props.contains(new Property("test2", "value2", null)));
  }
  
  @Test
  public void testGetProcessPropertyList_categories_all() {
    
    configurator.store("cat1", true).addProperty("test1", "value1");
    configurator.store("cat2", true).addProperty("test2", "value2");
    
    when(processProperties.keys()).thenReturn(Collects.arrayToList("test").iterator());
    when(processProperties.get("test")).thenReturn(new ConfigProperty("test", "value"));

    List<Property> props = configurator.getPropertiesList(PropertyScope.PROCESS, Collects.arrayToList("cat1", "cat2"));
    
    assertTrue(props.contains(new Property("test", "value", null)));
    assertTrue(props.contains(new Property("test1", "value1", "cat1")));
    assertTrue(props.contains(new Property("test2", "value2", "cat2")));
  }
  
  @Test
  public void testGetProcessPropertyList_categories_no_match() {
    
    configurator.store("cat1", true).addProperty("test1", "value1");
    configurator.store("cat2", true).addProperty("test2", "value2");
    
    when(processProperties.keys()).thenReturn(Collects.arrayToList("test").iterator());
    when(processProperties.get("test")).thenReturn(new ConfigProperty("test", "value"));

    List<Property> props = configurator.getPropertiesList(PropertyScope.PROCESS, Collects.arrayToList("cat3", "cat4"));
    
    assertTrue(props.contains(new Property("test", "value", null)));
    assertFalse(props.contains(new Property("test1", "value1", "cat1")));
    assertFalse(props.contains(new Property("test2", "value2", "cat2")));
  }
  
  @Test
  public void testGetProcessPropertyList_categories_partial_match() {
    
    configurator.store("cat1", true).addProperty("test1", "value1");
    configurator.store("cat2", true).addProperty("test", "value3");
    
    when(processProperties.keys()).thenReturn(Collects.arrayToList("test").iterator());
    when(processProperties.get("test")).thenReturn(new ConfigProperty("test", "value"));

    List<Property> props = configurator.getPropertiesList(PropertyScope.PROCESS, Collects.arrayToList("cat1", "cat2"));
    
    assertFalse(props.contains(new Property("test", "value", null)));
    assertTrue(props.contains(new Property("test1", "value1", "cat1")));
    assertTrue(props.contains(new Property("test", "value3", "cat2")));
  }
  
  @Test
  public void testGetProcessAllPropertyList() {
    
    configurator.store("cat1", true).addProperty("test3", "value3");
    configurator.store("cat2", true).addProperty("test4", "value4");
    
    when(processProperties.keys()).thenReturn(Collects.arrayToList("test1", "test2").iterator());
    when(processProperties.get("test1")).thenReturn(new ConfigProperty("test1", "value1"));
    when(processProperties.get("test2")).thenReturn(new ConfigProperty("test2", "value2"));

    List<Property> props = configurator.getAllPropertiesList(PropertyScope.PROCESS);
    
    assertTrue(props.contains(new Property("test1", "value1", null)));
    assertTrue(props.contains(new Property("test2", "value2", null)));
    assertTrue(props.contains(new Property("test3", "value3", "cat1")));
    assertTrue(props.contains(new Property("test4", "value4", "cat2")));

  }
  
  @Test
  public void testGetServerPropertyList() {
    when(serverProperties.keys()).thenReturn(Collects.arrayToList("test1", "test2").iterator());
    when(serverProperties.get("test1")).thenReturn(new ConfigProperty("test1", "value1"));
    when(serverProperties.get("test2")).thenReturn(new ConfigProperty("test2", "value2"));

    List<Property> props = configurator.getPropertiesList(PropertyScope.SERVER, new ArrayList<String>());
    
    assertTrue(props.contains(new Property("test1", "value1", null)));
    assertTrue(props.contains(new Property("test2", "value2", null)));
  }

  @Test
  public void testAddTag() {
    configurator.addTag("test");
    verify(tagsProperties).put("test", new ConfigProperty("test", "test"));
  }

  @Test
  public void testClearTags() {
    configurator.clearTags();
    verify(tagsProperties).clear();
  }

  @Test
  public void testGetTags() {
    when(tagsProperties.keys()).thenReturn(Collects.arrayToList("test").iterator());

    Set<Tag> tags = configurator.getTags();
    
    assertTrue(tags.contains(new Tag("test")));
  }

  @Test
  public void testRemoveTagString() {
    configurator.removeTag("test");
    verify(tagsProperties).remove("test");
  }

  @Test
  public void testRemoveTagArg() {
    when(tagsProperties.keys()).thenReturn(Collects.arrayToList("test").iterator());
    configurator.removeTag(ArgFactory.any());
    verify(tagsProperties).remove("test");
  }

  @Test
  public void testAddTags() {
    configurator.addTags(Collects.arrayToSet("test1", "test2"));
    verify(tagsProperties).put("test1", new ConfigProperty("test1", "test1"));
    verify(tagsProperties).put("test2", new ConfigProperty("test2", "test2"));
  }
  
  @Test
  public void testReplaceTags() {
    when(tagsProperties.get("test1")).thenReturn(new ConfigProperty("test1", "test1"));
    configurator.addTags(Collects.arrayToSet("test1"));
    configurator.renameTags(Collects.arrayToList(new NameValuePair("test1", "test2")));
    verify(tagsProperties).put("test1", new ConfigProperty("test1", "test1"));
    verify(tagsProperties).remove("test1");
    verify(tagsProperties).put("test2", new ConfigProperty("test2", "test2"));
  }

}
