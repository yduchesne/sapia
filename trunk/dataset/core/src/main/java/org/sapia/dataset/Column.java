package org.sapia.dataset;

import org.sapia.dataset.format.Format;
import org.sapia.dataset.parser.Parser;

/**
 * Holds information about a column in a {@link ColumnSet}.
 * 
 * @author yduchesne
 *
 */
public interface Column {
  
  /**
   * @return this instance {@link NominalSet} - which will be empty if this instance does not support
   * nominal values.
   */
 public NominalSet getNominalValues();
  
  /**
   * @return this instance's {@link Parser}.
   */
  public Parser getParser();
  
  /**
   * @param parser a {@link Parser}.
   */
  public void setParser(Parser parser);
  
  /**
   * @return this instance's {@link Format}.
   */
  public Format getFormat();
  
  /**
   * @param formatter a {@link Format}.
   */
  public void setFormat(Format formatter);
  
  /**
   * @return the index of the corresponding column in a {@link ColumnSet}.
   */
  public int getIndex();
  
  /**
   * @return the column's name.
   */
  public String getName();
  
  /**
   * @return the column's data type.
   */
  public Datatype getType();
  
  /**
   * @param newIndex the index to assign to the copy.
   * @return a new {@link Column} that's a copy of this instance, but
   * with the given index.
   */
  public Column copy(int newIndex);
  
}
