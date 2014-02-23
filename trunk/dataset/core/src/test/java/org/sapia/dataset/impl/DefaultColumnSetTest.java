package org.sapia.dataset.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.sapia.dataset.Column;
import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.util.Data;

public class DefaultColumnSetTest {
  
  private DefaultColumnSet columnSet;
  
  @Before
  public void setUp() {
    columnSet = new DefaultColumnSet(
      new DefaultColumn(0, Datatype.STRING, "col0"),  
      new DefaultColumn(1, Datatype.STRING, "col1"),
      new DefaultColumn(2, Datatype.STRING, "col2")  
    );
  }
  

  @Test
  public void testGetColumnIndices() {
    Set<Integer> indices = Data.setOfInts(columnSet.getColumnIndices());
    assertTrue(
        "Expected indices to be 0, 1, 2. Got: " + indices, 
        indices.containsAll(Data.set(new Integer[]{new Integer(0), new Integer(1), new Integer(2)}))
    );
  }

  @Test
  public void testGetByIndex() {
    assertEquals("col0", columnSet.get(0).getName());
    assertEquals("col1", columnSet.get(1).getName());
    assertEquals("col2", columnSet.get(2).getName());    
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testGetByInvalidIndex() {
    columnSet.get(3);
  }

  @Test
  public void testGetByName() {
    assertEquals("col0", columnSet.get("col0").getName());
    assertEquals("col1", columnSet.get("col1").getName());
    assertEquals("col2", columnSet.get("col2").getName());    
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetByInvalidName() {
    columnSet.get("col3");
  }
  
  @Test
  public void testIncludes() {
    ColumnSet subset = columnSet.includes("col1");
    assertEquals("col1", subset.get(0).getName());
    assertEquals(1, subset.get(0).getIndex());
    assertEquals(1, subset.size());
  }
  
  @Test
  public void testExcludes() {
    ColumnSet subset = columnSet.excludes("col1");
    assertEquals("col0", subset.get(0).getName());
    assertEquals("col2", subset.get(1).getName());
  }

  @Test
  public void testIterator() {
    List<Column> columns = Data.list(columnSet.iterator());
    assertEquals("col0", columns.get(0).getName());
    assertEquals("col1", columns.get(1).getName());
    assertEquals("col2", columns.get(2).getName());    
  }
  
  public void testEquals() {
    ColumnSet others = new DefaultColumnSet(
        new DefaultColumn(0, Datatype.STRING, "col0"),  
        new DefaultColumn(1, Datatype.STRING, "col1"),
        new DefaultColumn(2, Datatype.STRING, "col2")  
      );
    assertEquals(columnSet, others);
  }
  
  public void testNotEquals() {
    ColumnSet others = new DefaultColumnSet(
        new DefaultColumn(2, Datatype.STRING, "col0"),  
        new DefaultColumn(1, Datatype.STRING, "col1"),
        new DefaultColumn(0, Datatype.STRING, "col2")  
      );
    assertNotSame(columnSet, others);
  }

}
