package org.sapia.corus.db.persistence;

import static org.junit.Assert.*;

import org.junit.Test;

public class ClassDescriptorTest {

  @Test
  public void testGetDescriptorForName() {
    ClassDescriptor<TestPersistentObject> cd = new ClassDescriptor<TestPersistentObject>(TestPersistentObject.class);
    System.out.println(cd);
    assertTrue("No descriptor found for field 'name'", cd.getFieldForName("name") != null);
    assertTrue("No descriptor found for field 'id'", cd.getFieldForName("id") != null);
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
