package org.sapia.dataset;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.sapia.dataset.impl.DefaultVector;
import org.sapia.dataset.util.Numbers;

public class VectorKeyTest {
  
  private ColumnSet columns;
  private VectorKey key;
  
  @Before
  public void setUp() {
    columns = ColumnSets.columnSet(
        "col0", Datatype.NUMERIC,
        "col1", Datatype.NUMERIC
    );
    
    key = new VectorKey(
       columns, 
       new Object[] { new Integer(0), new Integer(1) }
    );
  }

  @Test
  public void testGetColumnSet() {
    assertEquals(columns, key.getColumnSet());
  }

  @Test
  public void testGet() {
    
  }

  @Test
  public void testGetValues() {
    Object[] values = key.getValues();
    for (int i : Numbers.range(values.length)) {
      assertEquals(values[i], key.get(i));
    }
  }

  @Test
  public void testSize() {
    assertEquals(2, key.size());
  }

  @Test
  public void testEqualsObject() {
    VectorKey other = new VectorKey(
        columns, 
        new DefaultVector(new Object[] { new Integer(0), new Integer(1) })
     );
    
    assertEquals(other, key);
  }

  @Test
  public void testCompareToEqual() {
    VectorKey other = new VectorKey(
        columns, 
        new DefaultVector(new Object[] { new Integer(0), new Integer(1) })
     );
    assertTrue(key.compareTo(other) == 0);
  }
  
  @Test
  public void testCompareToGreater() {
    VectorKey other = new VectorKey(
        columns, 
        new DefaultVector(new Object[] { new Integer(0), new Integer(0) })
     );
    assertTrue(key.compareTo(other) > 0);
  }
  
  @Test
  public void testCompareToLower() {
    VectorKey other = new VectorKey(
        columns, 
        new DefaultVector(new Object[] { new Integer(1), new Integer(1) })
     );
    assertTrue(key.compareTo(other) < 0);
  }

}
