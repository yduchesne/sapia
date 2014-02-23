package org.sapia.dataset.io.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.sapia.dataset.Column;
import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.Dataset;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.Vector;
import org.sapia.dataset.impl.DefaultColumn;
import org.sapia.dataset.impl.DefaultColumnSet;
import org.sapia.dataset.impl.DefaultDataset;
import org.sapia.dataset.impl.DefaultVector;
import org.sapia.dataset.util.Checks;

/**
 * This class provides the logic for creating {@link Dataset}s out of JDBC {@link ResultSet}s.
 * <p>
 * A {@link Dataset} can be created by calling the {@link #dataset(ResultSet)} static method,
 * passing in the {@link ResultSet} from which to create a dataset.
 * <p>
 * One can also elect to provide a {@link Connection} and a SQL select statement, and let an
 * instance of this class perform {@link ResultSet} dataset creation, as follows:
 * 
 * <pre>
 * try {
 *    Dataset ds = Jdbc.newInstance()
 *      .connection(someDbConnection)
 *      .select("SELECT * FROM customers WHERE name ILIKE '%a' ORDER BY cust_id")
 *      .build();
 *    return ds;
 * } finally {
 *    someDbConnection.close();
 * }
 * </pre>
 *   
 * @author yduchesne
 *
 */
public class Jdbc {

  private Connection connection;
  private String     select;
  
  private Jdbc() {
  }
  
  /**
   * @param connection the database {@link Connection} to use.
   * @return
   */
  public Jdbc connection(Connection connection) {
    this.connection = connection;
    return this;
  }
  
  /**
   * @param sql the SQL select statement to use, and whose corresponding resultset
   * will be used to build a {@link Dataset}.
   * @return this instance.
   */
  public Jdbc select(String sql) {
    this.select = sql;
    return this;
  }
  
  /**
   * This method builds a {@link Dataset} using the configured database {@link Connection} and
   * select statement.
   * <b>IMPORTANT</b>: this method does not close the {@link Connection} upon exiting: it is the responsability
   * of the calling code to do so.
   * 
   * @return a new {@link Dataset}.
   * @throws SQLException
   */
  public Dataset build() throws SQLException {
    Checks.notNull(select, "Select statement not set");
    Checks.notNull(connection, "Database connection not set");
    PreparedStatement statement = connection.prepareStatement(select);
    try {
      ResultSet resultset = statement.executeQuery();
      return dataset(resultset);
    } finally {
      try {
        statement.close();
      } catch (SQLException e) {
        // noop
      }
    }
  }
  
  /**
   * This method creates a {@link Dataset} out of the given {@link ResultSet}.
   * <p>
   * It closes the resultet prior to returning.
   * 
   * @param resultset a {@link ResultSet}.
   * @return the {@link Dataset} that created from the given resultset.
   * @throws SQLException
   */
  public static Dataset dataset(ResultSet resultset) throws SQLException {
    try {
      return createDataset(resultset);
    } finally {
      try {
        resultset.close();
      } catch (SQLException e) {
        // noop
      }
    }
  }
  
  /**
   * @return a new instance of this class.
   */
  public static Jdbc obj() {
    return new Jdbc();
  }
  
  private static Dataset createDataset(ResultSet resultset) throws SQLException {
    
    int columnCount = resultset.getMetaData().getColumnCount();
    List<Column> columns = new ArrayList<>();
    
    for (int index = 0, i = 0; i < columnCount; i++) {
      String name = resultset.getMetaData().getColumnName(i);
      Datatype type;
      int    jdbcType = resultset.getMetaData().getColumnType(i);
      switch (jdbcType) {
        case Types.BIGINT:
        case Types.DOUBLE:
        case Types.DECIMAL:
        case Types.FLOAT:
        case Types.INTEGER:
        case Types.NUMERIC:
          type = Datatype.NUMERIC;
          break;
        case Types.CHAR:
        case Types.VARCHAR:
        case Types.LONGNVARCHAR:
        case Types.LONGVARCHAR:
        case Types.BOOLEAN:
          type = Datatype.STRING;
          break;
        case Types.DATE:
        case Types.TIME:
        case Types.TIMESTAMP:
          type = Datatype.DATE;
        default:
          continue;
      }
      columns.add(new DefaultColumn(index++, type, name));
    }
    
    ColumnSet columnSet = new DefaultColumnSet(columns);
    
    List<Vector> rows = new ArrayList<>();
    while (resultset.next()) {
      Object[] values = new Object[columnSet.size()];
      for (Column col : columnSet) {
        switch (col.getType()) {
          case DATE:
            values[col.getIndex()] = resultset.getDate(col.getName());
            break;
          case NUMERIC:
            values[col.getIndex()] = resultset.getDouble(col.getName());
            break;
          case STRING:
            values[col.getIndex()] = resultset.getString(col.getName());
            break;
          default:
            throw new IllegalArgumentException(String.format("Datatype %s not handled for column %s", col.getType(), col.getName()));
        }
      }
      rows.add(new DefaultVector(values));
    }
    
    return new DefaultDataset(columnSet, rows);
  }
}
