package org.sapia.dataset.io.table;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class RowTest {
  
  private Table table;
  private Row row;

  @Before
  public void setUp() {
    table = new Table();
    table.header("h0", 2);
    table.header("h1", 2);
    row = table.row();
  }

  @Test
  public void testCell() {
    row.cell("c0").cell("c1");
    assertEquals(2, row.getCellCount());
    assertEquals("c0", row.getCell(0).getValue());
    assertEquals("c1", row.getCell(1).getValue());
  }

  @Test
  public void testFill() {
    row.fill();
    assertEquals(2, row.getCellCount());
  }

  @Test
  public void testFillWithCount() {
    row.cell("c1").fill();
    assertEquals(2, row.getCellCount());
  }

  @Test
  public void testRow() {
    row.row();
    assertEquals(2, table.getRowCount());
  }

  @Test
  public void testGetCell() {
    row.cell("c1").cell("c2");
    assertEquals("c1", row.getCell(0).getValue());
    assertEquals("c2", row.getCell(1).getValue());
  }

}
