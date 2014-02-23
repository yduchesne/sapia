package org.sapia.dataset.transform.join;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.sapia.dataset.Vector;
import org.sapia.dataset.Vectors;
import org.sapia.dataset.transform.join.VectorTable.VectorType;
import org.sapia.dataset.util.Numbers;

public class JoinVectorTest {
  
  private Vector left, right;
  private Vector join;

  @Before
  public void setUp() {
    left  = Vectors.vector(0, 1, 2);
    right = Vectors.vector(3, 4);
    join = new JoinVector(
        new VectorTable(
            new int[] {0, 1, 2, 0, 1}, 
            new VectorType[]{
                VectorType.LEFT,
                VectorType.LEFT,
                VectorType.LEFT,
                VectorType.RIGHT,
                VectorType.RIGHT
            }
        ),
        left, 
        right
    );
  }

  @Test
  public void testGet() {
    for (int i : Numbers.range(5)) {
      assertEquals(i, join.get(i));
    }
  }

  @Test
  public void testIterator() {
    int count = 0;
    for (Object value : join) {
      assertEquals(count++, ((Integer)value).intValue());
    }
    assertEquals(5, count);
  }

  @Test
  public void testSize() {
    assertEquals(5, join.size());
  }

  @Test
  public void testSubset() {
    Vector subset = join.subset(new int[]{3,4});
    assertEquals(3, ((Integer)subset.get(0)).intValue());
    assertEquals(4, ((Integer)subset.get(1)).intValue());
  }

  @Test
  public void testToArray() {
    Object[] values = join.toArray();
    for (int i : Numbers.range(5)) {
      assertEquals(join.get(i), values[i]);
    }
  }

}
