package org.sapia.dataset.io.table;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DataTableTest {
  
  private Table table;
  
  @Before
  public void setUp() {
    table = new Table();
  }

  @Test
  public void testHeader() {
    table.header("0", 1);
    table.header("1", 1);
    assertEquals(table.getHeader(0).getValue(), "0");
    assertEquals(table.getHeader(1).getValue(), "1");
  }

  @Test
  public void testFill() {
    table.fill(5, 1);
    assertEquals(5, table.getHeaderCount());
    for (int i = 0; i < 5; i++) {
      assertEquals("", table.getHeader(i).getValue());
    }
  }

  @Test
  public void testRow() {
    table.header("h1", 1);
    table.row().row();
    assertEquals(2, table.getRowCount());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testRowNoHeaders() {
    table.row().row();
  }

  @Test
  public void testGetRow() {
    table.header("h1", 1);
    table.row().row();
    assertEquals(0, table.getRow(0).getCellCount());
    assertEquals(0, table.getRow(1).getCellCount());
  }

}
