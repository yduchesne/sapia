package org.sapia.dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.sapia.dataset.help.Doc;
import org.sapia.dataset.help.SettingsDoc;
import org.sapia.dataset.impl.DefaultColumn;
import org.sapia.dataset.impl.DefaultColumnSet;
import org.sapia.dataset.util.Checks;
import org.sapia.dataset.util.Settings;

/**
 * Provides {@link ColumnSet}-related utilities.
 * 
 * @author yduchesne
 *
 */
public class ColumnSets {

  private static final int COLUMN_NAME_LEN = 3;
  
  private static final Settings COLUMN_SETTINGS = Settings.obj()
      .setting().name("name").type(String.class).description("column name").mandatory()
      .setting().name("type").type(Datatype.class).description("column data type").mandatory()
      .finish();
  
  private ColumnSets() {
  }
  
  /**
   * A convenience for creating multiple {@link ColumnSet}s by passing the required parameters
   * for each intended column set in a single array.
   * <p>
   * Example:
   * <pre>
   * ColumnSet columns = columnSet("firstName", Datatype.STRING, "age", Datatype.NUMERIC)
   * </pre>
   * This method does an <code>instance of</code> check to determine which is which.
   * <p>
   * The following types of values, for each intended column, must specified:
   * <ul>
   *   <li> the column's {@link Datatype}.
   *   <li> the column's name.
   * </ul>
   * 
   * @param columnParameters an array of column parameters.
   * @return a new {@link ColumnSet}.
   */
  public static ColumnSet columnSet(Object...columnParameters) {
    Checks.isTrue(columnParameters.length > 0, "No column parameter provided");
    
    List<Datatype>   types   = new ArrayList<>();
    List<String>     names   = new ArrayList<>();
    List<Column> columns = new ArrayList<>();
    
    for (Object c : columnParameters) {
      if (c instanceof Datatype) {
        types.add((Datatype) c);
      } else if (c instanceof String) {
        names.add((String) c);
      }
    }
    
    Checks.isTrue(types.size() == names.size(), "Must have same number of column types as column names");
    
    for (int i = 0; i < types.size(); i++) {
      columns.add(new DefaultColumn(i, types.get(i), names.get(i)));  
    }
    return new DefaultColumnSet(columns);
  }
  
  /**
   * @param columnSettings the {@link List} of {@link Map}s holding settings for each columns.
   * @return a new {@link ColumnSet}.
   */
  @Doc("Returns a column set, given a list of column settings")
  public static ColumnSet columnSet(
      @SettingsDoc("COLUMN_SETTINGS") @Doc("a list of column settings") List<Map<String, Object>> columnSettings) {
    List<Column> columns = new ArrayList<>();
    int index = 0;
    for (Map<String, Object> values : columnSettings) {
      String   name = COLUMN_SETTINGS.get("name").get(values, String.class);
      Datatype type = COLUMN_SETTINGS.get("type").get(values, Datatype.class); 
      columns.add(new DefaultColumn(index, type, name));
      index++;
    }
    return new DefaultColumnSet(columns);
  }
  
  /**
   * Renames the columns in the given {@link ColumnSet}, following a best-effort algorithm.
   * @param columnSet the {@link ColumnSet} to rename.
   * @return the {@link ColumnSet} with the renamed columns.
   */
  public static ColumnSet rename(ColumnSet columnSet) {
    List<Column> newColumns = new ArrayList<>(columnSet.size());
    for (int i = 0; i < columnSet.size(); i++) {
      Column c = columnSet.get(i);
      String currentName = c.getName();
      
      // remove all that is between parenthesis
      int found = currentName.indexOf("(");
      if (found > 0) {
        currentName = currentName.substring(0, found);
      } 
      
      // tokenize
      StringBuilder newName = new StringBuilder();
      StringTokenizer tkz = new StringTokenizer(currentName, " .,'\";:");
      int tokenCount = 0;
      while (tkz.hasMoreTokens()) {
        String token = tkz.nextToken();
        
        if (token.length() >= COLUMN_NAME_LEN) {
          token = token.substring(0,  COLUMN_NAME_LEN);
        }
        if (tokenCount > 0) {
          newName.append("_");
        }
        newName.append(token.toLowerCase());
        tokenCount++;
      }
      newColumns.add(new DefaultColumn(i, c.getType(), newName.toString()));
    }
    return new DefaultColumnSet(newColumns);
  }
}
