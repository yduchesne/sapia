package org.sapia.corus.db.persistence;

import static org.junit.Assert.*;

import org.junit.Test;
import org.sapia.corus.client.services.db.persistence.ClassDescriptor;
import org.sapia.corus.client.services.db.persistence.NoSuchFieldException;

public class ClassDescriptorTest {

  @Test
  public void testGetDescriptorForName() {
    ClassDescriptor<TestPersistentObject> cd = new ClassDescriptor<TestPersistentObject>(TestPersistentObject.class);
    cd.getFieldForName("name");
    cd.getFieldForName("id");
  }
  
  @Test
  public void testGetDescriptorForTransientAccessor() {
    ClassDescriptor<TestPersistentObject> cd = new ClassDescriptor<TestPersistentObject>(TestPersistentObject.class);
    try{
      cd.getFieldForName("key");
      fail("Field 'key' should not be persistent attribute");
    }catch(NoSuchFieldException e){}
      
  }
  
  @Test
  public void testGetDescriptorForIndex() {
    ClassDescriptor<TestPersistentObject> cd = new ClassDescriptor<TestPersistentObject>(TestPersistentObject.class);
    assertEquals("active", cd.getFieldForIndex(0).getName());
    assertEquals("id", cd.getFieldForIndex(1).getName());
    assertEquals("name", cd.getFieldForIndex(2).getName());
  }

  @Test
  public void testGetDescriptors() {
    ClassDescriptor<TestPersistentObject> cd = new ClassDescriptor<TestPersistentObject>(TestPersistentObject.class);
    assertEquals(3, cd.getFields().size());
  }

}
