package org.sapia.dataset.transform.sort;

import java.util.Comparator;

import org.sapia.dataset.Column;
import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.Vector;

/**
 * Compares {@link Vector} instances.
 * 
 * @author yduchesne
 *
 */
public class VectorComparator implements Comparator<Vector> {

  private ColumnSet columns;
  
  /**
   * @param columns the {@link ColumnSet} indicating on which columns the comparison is to be done.
   */
  public VectorComparator(ColumnSet columns) {
    this.columns = columns;
  }
  
  @Override
  public int compare(Vector o1, Vector o2) {
    int cmp = 0;
    for (Column col : columns) {
      Object v1 = o1.get(col.getIndex());
      Object v2 = o2.get(col.getIndex());
      cmp = col.getType().strategy().compareTo(v1, v2);
      if (cmp != 0) {
        break;
      } 
    }
    return cmp;
  }
}
