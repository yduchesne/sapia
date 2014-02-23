package org.sapia.dataset;

import static org.junit.Assert.*;

import org.junit.Test;

public class ColumnSetsTest {

  @Test
  public void testRename() {
    ColumnSet columns = ColumnSets.columnSet("prevalence of HIV (among people of 18 y/o or more)", Datatype.NUMERIC);
    ColumnSet renamed = ColumnSets.rename(columns);
    assertEquals("pre_of_hiv", renamed.get(0).getName());
  }

}
