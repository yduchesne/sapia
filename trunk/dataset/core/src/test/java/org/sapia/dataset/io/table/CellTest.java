package org.sapia.dataset.io.table;

import static org.junit.Assert.*;

import org.junit.Test;

public class CellTest {

  @Test
  public void testToStringMaxWidth() {
    Cell c = new Cell(0, "aaa", new ColumnStyle().width(2));
    assertEquals("aa", c.toString());
  }
  
  @Test
  public void testToString() {
    Cell c = new Cell(0, "aa", new ColumnStyle().width(3));
    assertEquals("aa ", c.toString());
  }

}
