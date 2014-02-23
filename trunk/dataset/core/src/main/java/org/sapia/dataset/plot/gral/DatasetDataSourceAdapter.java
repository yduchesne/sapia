package org.sapia.dataset.plot.gral;

import java.util.Date;

import java.util.Iterator;

import org.sapia.dataset.Column;
import org.sapia.dataset.Dataset;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.value.Values;

import de.erichseifert.gral.data.DataListener;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.data.statistics.Statistics;

/**
 * Adapts a {@link Dataset} to the {@link DataSource} interface.
 * 
 * @author yduchesne
 *
 */
public class DatasetDataSourceAdapter implements DataSource {
  
  private Dataset dataset;
  
  /**
   * @param dataset the {@link Dataset} that this instance should wrap.
   */
  public DatasetDataSourceAdapter(Dataset dataset) {
    this.dataset = dataset;
  }
  
  @Override
  public void addDataListener(DataListener listener) {
  }
  
  @Override
  public void removeDataListener(DataListener listener) {
  }

  @Override
  public Iterator<Comparable<?>> iterator() {
    return new Iterator<Comparable<?>>() {
      private int col = 0;
      private int row = 0;

      public boolean hasNext() {
        return col < dataset.getColumnSet().size() && row < dataset.size();
      }

      public Comparable<?> next() {
        Comparable<?> value = get(col, row);
        if (++col >= dataset.getColumnSet().size()) {
          col = 0;
          ++row;
        }
        return value;
      }
      
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }
 
  @Override
  public Comparable<?> get(int column, int row) {
    Column col = dataset.getColumnSet().get(column);
    switch (col.getType()) {
      case DATE:
        Date date = (Date) dataset.getRow(row).get(column);
        if (date != null) {
          return date.getTime();
        } else {
          return 0;
        }
      case NUMERIC:
        return Values.doubleValue(dataset.getRow(row).get(column));
      case STRING:
        return (String) dataset.getRow(row).get(column);
      case GEOMETRY:
        throw new IllegalStateException("Columns of type GEOMETRY cannot be plotted");
      default:
        throw new IllegalStateException("Datatype not handled: " + col.getType());
    }
  }
  
  @Override
  public Row getRow(int index) {
    return new Row(this, index);
  }
  
  @Override
  public int getRowCount() {
    return dataset.size();
  }
  
  @Override
  public Statistics getStatistics() {
    return new Statistics(this);
  }
  
  @Override
  public boolean isColumnNumeric(int index) {
    return dataset.getColumnSet().get(index).getType() == Datatype.NUMERIC;
  }
  
  @Override
  public de.erichseifert.gral.data.Column getColumn(int index) {
    return new de.erichseifert.gral.data.Column(this, index);
  }
  
  @Override
  public int getColumnCount() {
    return dataset.getColumnSet().size();
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Class<? extends Comparable<?>>[] getColumnTypes() {
    Class<? extends Comparable<?>>[] columnTypes = new Class[dataset.getColumnSet().size()];
    for (int i = 0; i < columnTypes.length; i++) {
      Column col = dataset.getColumnSet().get(i);
      switch (col.getType()) {
        case DATE:
          columnTypes[i] = Date.class;
          break;
        case NUMERIC:
          columnTypes[i] = Double.class;
          break;
        case STRING:
          columnTypes[i] = Double.class;
          break;
        case GEOMETRY:
          throw new IllegalStateException("Columns of type GEOMETRY cannot be plotted");
        default:
          throw new IllegalStateException("Datatype not handled: " + col.getType());
      }
    }
    return columnTypes;
  }
}
