package org.sapia.dataset;

import java.util.Collection;
import java.util.List;

/**
 * Extends the {@link Dataset} interface by specifying additional methods
 * given access to the underlying index structure.
 * 
 * @author yduchesne
 *
 */
public interface IndexedDataset extends Dataset {

  /**
   * @return the {@link ColumnSet} holding the {@link Column}s corresponding
   * to the columns on which this index is built.
   */
  public ColumnSet getIndexedColumnSet();

  /**
   * @return the {@link List} of {@link VectorKey}s.
   */
  public Collection<VectorKey> getKeys();
  
  /**
   * @param key a {@link VectorKey}.
   * @return the {@link RowSet} holding the {@link Vector}s corresponding
   * to the rows that match the given key.
   */
  public RowSet getRowSet(String[] columNames, Object[] values);
  
  /**
   * @param key a {@link VectorKey}.
   * @return the {@link RowSet} holding the {@link Vector}s corresponding
   * to the rows that match the given key.
   */
  public RowSet getRowset(VectorKey key);
  
}
