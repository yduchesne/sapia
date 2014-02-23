package org.sapia.dataset.computation;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.sapia.dataset.ColumnSets;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.value.DoubleValue;

public class ComputationResultsTest {
  
  private ComputationResults context;
  
  @Before
  public void setUp() {
    context = ComputationResults.newInstance(
        ColumnSets.columnSet("col0", Datatype.NUMERIC, "col1", Datatype.STRING)
    );
  }

  @Test
  public void testGet() {
    ComputationResult result = context.get("test");
    result.set(context.getColumnSet().get("col0"), new DoubleValue(1));
    result = context.get("test");
    assertEquals(new DoubleValue(1), result.get(context.getColumnSet().get("col0")));
  }
}
