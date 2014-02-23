package org.sapia.dataset.io.table;

import java.util.ArrayList;
import java.util.List;

import org.sapia.dataset.io.ContentBuffer;
import org.sapia.dataset.util.Checks;

/**
 * Holds {@link Header}s and {@link Row}s of {@link Cell}s.
 * 
 * @author yduchesne
 *
 */
public class Table {

  private List<Header> headers = new ArrayList<>();
  private List<Row>    rows    = new ArrayList<>();
 
  /**
   * @return a new instance of this class.
   */
  public static Table obj() {
    return new Table();
  }
  
  /**
   * @param name the header value.
   * @param width the header width (value will be trimmed to fit within this width
   * if need be).
   * @return the {@link Header} that was created.
   */
  public Header header(String value, int width) {
    Header h = new Header(headers.size(), value, new ColumnStyle().width(width));
    headers.add(h);
    return h;
  }
  
  /**
   * @param index an index.
   * @return the {@link Header} at the given index.
   */
  public Header getHeader(int index) {
    return headers.get(index);
  }
  
  /**
   * @param count the number of empty headers to create.
   * @param width the width of headers.
   * @return this instance.
   */
  public Table fill(int count, int width) {
    for (int i = 0; i < count; i++) {
      headers.add(new Header(headers.size(), "", new ColumnStyle().width(width)));
    }
    return this;
  }
  
  /**
   * @return the number of headers that this instance holds.
   */
  public int getHeaderCount() {
    return headers.size();
  }
  
  /**
   * @return a new {@link Row}.
   */
  public Row row() { 
    Checks.isFalse(this.headers.isEmpty(), "Table has no headers defined. You must create the headers first - see the header(...) method");
    Row r = new Row(this);
    rows.add(r);
    return r;
  }
  
  /**
   * @param an index.
   * @return the {@link Row} at the given index.
   */
  public Row getRow(int index) {
    return rows.get(index);
  }
  
  /**
   * @return the number of rows that this instance holds.
   */
  public int getRowCount() {
    return rows.size();
  }
  
  @Override
  public String toString() {
    ContentBuffer buf = ContentBuffer.obj();
    for (Header h : headers) {
      if (h.getIndex() > 0) {
        buf.chars(h.getStyle().getLeftPadding());
      }
      buf.chars(h.toString());
      if (h.getIndex() < headers.size() - 1) {
        buf.chars(h.getStyle().getRightPadding()).chars(h.getStyle().getSeparator());
      }
    }
    buf.line();
    buf.repeatln("-", buf.getLastLineLength());
    for (Row r : rows) {
      for (int i = 0; i < headers.size(); i++) {
        Cell c = r.getCell(i);
        if (c.getIndex() > 0) {
          buf.chars(c.getStyle().getLeftPadding());
        }
        buf.chars(c.toString());
        if (c.getIndex() < headers.size() - 1) {
          buf.chars(c.getStyle().getRightPadding()).chars(headers.get(i).getStyle().getSeparator());
        }
      }
      buf.line();
    }
    return buf.toString();
  }
}
