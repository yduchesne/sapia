package org.sapia.dataset;

import java.util.ArrayList;
import java.util.List;

import org.sapia.dataset.conf.Conf;
import org.sapia.dataset.help.Hide;
import org.sapia.dataset.impl.DefaultColumn;
import org.sapia.dataset.impl.DefaultDataset;
import org.sapia.dataset.impl.DefaultRowSet;
import org.sapia.dataset.io.table.Row;
import org.sapia.dataset.io.table.Table;
import org.sapia.dataset.util.Checks;

public class Datasets {
  
  /**
   * Builds {@link List} of {@link Column} instances.
   */
  public static final class ColumnBuilder {

    private List<Column> columns = new ArrayList<>();
    
    private String   name;
    private int      index;
    private Datatype type;
    
    private ColumnBuilder() {
    }
    
    /**
     * @param name a column name.
     * @return this instance.
     */
    public ColumnBuilder name(String name) {
      this.name = name;
      return this;
    }
    
    /**
     * @param type a column {@link Datatype}. 
     * @return this instance.
     */
    public ColumnBuilder type(Datatype type) {
      this.type = type;
      return this;
    }
    
    /**
     * @param name a column name.
     * @param type a {@link Datatype}.
     * @return a {@link ColumnBuilder}.
     */
    public ColumnBuilder column(String name, Datatype type) {
      Checks.notNull(name, "Column name required");
      Checks.notNull(type, "Column type required");
      DefaultColumn col = new DefaultColumn(index++, type, name);
      columns.add(col);
      return this;
    }
    
    /**
     * @return a new {@link Column}.
     */
    public ColumnBuilder column() {
      Checks.notNull(name, "Column name not set");
      Checks.notNull(type, "Column type not set");
      DefaultColumn col = new DefaultColumn(index++, type, name);
      columns.add(col);
      name = null;
      type = null;
      return this;
    }

    /**
     * @return the {@link List} of {@link Column} instances that was built.
     */
    public List<Column> build() {
      return columns;
    }
  }
  
  // --------------------------------------------------------------------------
  
  /**
   * A builder of {@link Dataset}s.
   */
  public static final class DatasetBuilder {
    
    List<Column> columns; 
    List<Vector>     rows    = new ArrayList<>();
    
    private DatasetBuilder(List<Column> columns) {
      this.columns = columns;
    }
    
    /**
     * @param cols a {@link ColumnBuilder}.
     * @return this instance.
     */
    public DatasetBuilder columns(ColumnBuilder cols) {
      columns = cols.build();
      return this;
    }
    
    /**
     * @param row a row {@link Vector}.
     * @return this instance.
     */
    public DatasetBuilder row(Vector row) {
      Checks.isTrue(row.size() == columns.size(), "Invalid row length: got %s values, but dataset has %s columns", row.size(), columns.size());
      
      for (int i = 0; i < row.size(); i++) {
        Checks.isTrue(
            columns.get(i).getType().strategy().isAssignableFrom(row.get(i)), 
            "Value %s  cannot be assigned to column of type %s", row.get(i), 
            columns.get(i).getType()
        );
      }
      rows.add(row);
      
      return this;
    }
    
    /**
     * @return a new {@link Dataset}.
     */
    public Dataset build() {
      Checks.notNull(columns, "Columns not defined");
      Dataset dataset = new DefaultDataset(columns, rows);
      rows = new ArrayList<>();
      return dataset;
    }
  }
  
 
  
  // ==========================================================================
  // Ctor and Factory methods
  
  private Datasets() {
  }
  
  /**
   * Returns a new {@link ColumnBuilder}.
   */
  @Hide
  public static ColumnBuilder columns() {
    return new ColumnBuilder();
  }

  /**
   * @return a new {@link DatasetBuilder}.
   */
  @Hide
  public static DatasetBuilder dataset(ColumnBuilder columns) {
    return new DatasetBuilder(columns.build());
  }
  
  /**
   * @param columns a {@link ColumnSet}
   * @param arrayOfVectorValues an array of other arrays that correspond
   * to vector values.
   * @return a new {@link Dataset}.
   */
  @Hide
  public static Dataset dataset(ColumnSet columns, Object[][] arraysOfVectorValues) {
    return new DefaultDataset(columns, RowSets.rowSet(arraysOfVectorValues));
  }
  
  /**
   * @param columns a {@link ColumnSet}
   * @param listsOfVectorValues a list containing other lists that each
   * consist of the values for a given {@link Vector}.
   * @return a new {@link Dataset}.
   */
  @Hide
  public static Dataset dataset(ColumnSet columns, List<Vector> rows) {
    return new DefaultDataset(columns, new DefaultRowSet(rows));
  }
  
  /**
   * @param dataset the {@link Dataset} whose string representation should be returned.
   * @return a {@link String} corresponding to the dataset's content.
   */
  @Hide
  public static String toString(Dataset dataset) {
    
    Table dtable = Table.obj();
    for (Column col : dataset.getColumnSet()) {
      dtable.header(col.getName(), Conf.getCellWidth());
    }
    
    for (Vector row : dataset) {
      Row drow = dtable.row();
      for (Column col : dataset.getColumnSet()) {
        drow.cell(col.getFormat().formatValue(col.getType(), row.get(col.getIndex())));
      }
    }
    return dtable.toString();
  }
  
  /**
   * @param dataset a {@link Dataset}.
   * @return the last row {@link Vector} in the given dataset.
   */
  public static Vector lastRow(Dataset dataset) {
    Checks.isFalse(dataset.size() == 0, "Dataset is empty");
    return dataset.getRow(dataset.size() - 1);
  }
  
  /**
   * @param dataset a {@link Dataset}.
   * @return the first row {@link Vector} in the given dataset.
   */
  public static Vector firstRow(Dataset dataset) {
    Checks.isFalse(dataset.size() == 0, "Dataset is empty");
    return dataset.getRow(0);
  }

}
