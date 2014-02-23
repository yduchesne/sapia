package org.sapia.dataset.io.table;

import org.sapia.dataset.io.table.ColumnStyle.Alignment;
import org.sapia.dataset.util.Checks;
import org.sapia.dataset.util.Strings;

/**
 * Corresponds to a cell in a {@link Row} of a {@link Table}.
 * 
 * @author yduchesne
 *
 */
public class Cell {

  private int         index;
  private ColumnStyle style;
  private String      value;

  Cell(int index, String value, ColumnStyle style) {
    Checks.notNull(value, "Value cannot be null");
    this.index = index;
    this.value = value;
    this.style = style;
  }
  
  /**
   * @return this instance's index.
   */
  public int getIndex() {
    return index;
  }
  
  /**
   * @return the cell's display width.
   */
  public int getWidth() {
    return style.getWidth();
  }
  
  /**
   * @return the cell's value.
   */
  public String getValue() {
    return value;
  }
  
  /**
   * @return the cell's {@link ColumnStyle}.
   */
  public ColumnStyle getStyle() {
    return style;
  }
  
  @Override
  public String toString() {
    if (value.length() > style.getWidth()) {
      return value.substring(0, style.getWidth());
    } else if (style.getAlignment() == Alignment.CENTER) {
      return Strings.center(value, style.getWidth());
    } else if (style.getAlignment() == Alignment.LEFT) {
      return Strings.rpad(value, " ", style.getWidth());
    } else if (style.getAlignment() == Alignment.RIGHT) {
      return Strings.lpad(value, " ", style.getWidth());
    } else {
      throw new IllegalStateException("Unknown alignment type: " + style.getAlignment());
    }
  }

}
