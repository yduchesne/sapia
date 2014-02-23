package org.sapia.dataset.impl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.sapia.dataset.Column;
import org.sapia.dataset.Datatype;

public class DefaultColumnTest {
  
  private DefaultColumn col;
  
  @Before
  public void setUp() {
    col = new DefaultColumn(0, Datatype.NUMERIC, "col0");
  }

  @Test
  public void testDefaultColumnInfo() {

  }


  @Test
  public void testGetIndex() {
    assertEquals(0, col.getIndex());
  }

  @Test
  public void testGetName() {
    assertEquals("col0", col.getName());
  }

  @Test
  public void testGetType() {
    assertEquals(Datatype.NUMERIC, col.getType());
  }

  @Test
  public void testEquals() {
    Column equals = new DefaultColumn(0, Datatype.NUMERIC, "col0");
    assertEquals(col, equals);
  }
  
  @Test
  public void testNotEqualsName() {
    Column equals = new DefaultColumn(0, Datatype.NUMERIC, "col1");
    assertNotSame(col, equals);
  }
  
  @Test
  public void testNotEqualsType() {
    Column equals = new DefaultColumn(0, Datatype.STRING, "col0");
    assertNotSame(col, equals);
  }
  
  @Test
  public void testCopy() {
    Column copy = col.copy(10);
    assertEquals(10, copy.getIndex());
    assertEquals(col.getFormat(), copy.getFormat());
    assertEquals(col.getName(), copy.getName());
    assertEquals(col.getParser(), copy.getParser());
    assertEquals(col.getType(), copy.getType());
  }

}
