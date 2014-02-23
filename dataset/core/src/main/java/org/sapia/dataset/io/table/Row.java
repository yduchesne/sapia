package org.sapia.dataset.io.table;

import java.util.ArrayList;
import java.util.List;

import org.sapia.dataset.util.Checks;

/**
 * Holds {@link Cell}s of a {@link Table}.
 * 
 * @author yduchesne
 *
 */
public class Row {

  private Table  table;
  private List<Cell> cells  = new ArrayList<>();
  
  Row(Table owner) {
    this.table = owner;
  }
  
  /**
   * @param value a cell value.
   * @return this instance.
   */
  public Row cell(String value) {
    Checks.isFalse(
        cells.size() - 1 >= table.getHeaderCount(), 
        "Cannot have more cells (%s) than headers (%s)", 
        cells.size() - 1, table.getHeaderCount()
    );
    Header h = table.getHeader(cells.size());
    Cell   c = new Cell(cells.size(), value, h.getStyle().width(value.length()));
    cells.add(c);
    return this;
  }
  
  /**
   * @return fills this instance's remaining slots with empty-valued cells.
   */
  public Row fill() {
    int max = table.getHeaderCount() - cells.size();
    for (int i = 0; i < max; i++) {
      cell("");
    }
    return this;
  }
  
  /**
   * @return fills this instance with the given number of empty cells.
   */
  public Row fill(int count) {
    int max = table.getHeaderCount() - cells.size();
    for (int i = 0; i < count && i < max; i++) {
      cell("");
    }
    return this;
  }
  
  /**
   * @return creates a new {@link Row} in this instance's {@link Table} and
   * returns it.
   */
  public Row row() {
    return table.row();
  }
  
  /**
   * @param index a cell index.
   * @return the {@link Cell} at the given index.
   */
  public Cell getCell(int index) {
    Checks.isTrue(index < cells.size(), "Invalid cell index: %s (got %s cells)", index, cells.size());
    return cells.get(index);
  }

  /**
   * @return the number of cells that this instance holds.
   */
  public int getCellCount() {
    return cells.size();
  }
  
}
