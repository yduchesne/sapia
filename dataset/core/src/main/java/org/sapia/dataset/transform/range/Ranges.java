package org.sapia.dataset.transform.range;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sapia.dataset.Column;
import org.sapia.dataset.Dataset;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.Vector;
import org.sapia.dataset.func.ArgFunction;
import org.sapia.dataset.help.Doc;
import org.sapia.dataset.impl.DefaultDataset;
import org.sapia.dataset.impl.DefaultVector;
import org.sapia.dataset.util.Checks;
import org.sapia.dataset.util.Data;
import org.sapia.dataset.util.Strings;

/**
 * Performs replacement of continuous values with corresponding ranges.
 * 
 * @author yduchesne
 *
 */
@Doc("Performs replacement of continuous values with corresponding ranges")
public class Ranges {

  private Ranges() {
  }
  
  /**
   * @param dataset the dataset whose given column values must be converted to a range.
   * @param partitionSize the size of each range partition.
   * @param columnNames the names of the columns whose values should be transformed into ranges.
   * @return a new {@link Dataset}, with the desired column values converted to ranges.
   */
  @Doc("Replaces continuous values with corresponding ranges, returns the thus processed dataset")
  public static Dataset range(
      @Doc("dataset to process") Dataset dataset, 
      @Doc("size of each range partitions") int partitionSize, 
      @Doc("names of the columns whose values should be replaced by ranges") String...columnNames) {

    Map<Column, List<RangeRef>> rangeValuesByColumn = new HashMap<>(columnNames.length);
    
    // building range values, for each column.
    for (String cn : columnNames) {
      Column col = dataset.getColumnSet().get(cn);
      Checks.isTrue(
          col.getType() == Datatype.NUMERIC || col.getType() == Datatype.DATE, 
          "Invalid type for column %s. Got %s, expected either: %s", 
          col.getName(), col.getType(), Strings.toString(Data.array(Datatype.NUMERIC, Datatype.DATE), 
              new ArgFunction<Datatype, String>() {
                @Override
                public String call(Datatype arg) {
                  return arg.name();
                }
              }
          )
      );

      Vector columnValues = dataset.getColumn(cn);
      List<RowValue> rowValues = new ArrayList<>(columnValues.size());
      for (int i = 0; i < columnValues.size(); i++) {
        rowValues.add(new RowValue(i, col, columnValues.get(i)));
      }
      Collections.sort(rowValues);
      
      // partitioning ---------------------------------------------------------
      List<List<RowValue>> partitions = Data.partition(rowValues, partitionSize);
      
      // substituting each value with its corresponding range -----------------
      List<RangeRef> ranges = new ArrayList<>(rowValues.size());
      
      for (List<RowValue> partition : partitions) {
        Range<Object> range = new Range<Object>(
            col.getType().comparator(), 
            Data.first(partition).get(), 
            Data.last(partition).get()
        );
        for (int i = 0; i < partition.size(); i++) {
          RowValue val = partition.get(i);
          ranges.add(new RangeRef(val.index, range));
        }
      }
      Collections.sort(ranges, new Comparator<RangeRef>() {
        @Override
        public int compare(RangeRef o1, RangeRef o2) {
          return o1.rowIndex - o2.rowIndex;
        }
      });
      rangeValuesByColumn.put(col, ranges);
    }
    
    // populating new dataset with range values, for relevant columns ---------
    List<Vector> newRows = new ArrayList<>();
    for (int i = 0; i < dataset.size(); i++) {
      Vector row = dataset.getRow(i);
      Object[] newRowValues = new Object[dataset.getColumnSet().size()];
      for (Column col : dataset.getColumnSet()) {
        List<RangeRef> ranges = rangeValuesByColumn.get(col);
        if (ranges != null) {
          newRowValues[col.getIndex()] = ranges.get(i).getRange();
        } else {
          newRowValues[col.getIndex()] = row.get(col.getIndex());
        }
      }
      newRows.add(new DefaultVector(newRowValues));
    }
    
    return new DefaultDataset(dataset.getColumnSet(), newRows);
    
  }
  
  // ==========================================================================

  static class RangeRef {
    private int           rowIndex;
    private Range<Object> range;
    
    RangeRef(int rowIndex, Range<Object> range) {
      this.rowIndex = rowIndex;
      this.range = range;
    }
    
    Range<Object> getRange() {
      return range;
    }
    
    int getRowIndex() {
      return rowIndex;
    }
  }
  
  // --------------------------------------------------------------------------
  
  static class RowValue implements Comparable<RowValue> {
    
    private int    index;
    private Column column;
    private Object value;
    
    RowValue(int index, Column col, Object value) {
      this.index  = index;
      this.column = col;
      this.value  = value;
    }
    
    int getIndex() {
      return index;
    }
    
    Column getColumn() {
      return column;
    }
    
    Object get() {
      return value;
    }
    
    @Override
    public String toString() {
      return Strings.toString("index", index, "value", value);
    }
    
    @Override
    public int compareTo(RowValue other) {
      return column.getType().comparator().compare(this.value,  other.value);
    }
    
  }

}
