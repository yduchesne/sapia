package org.sapia.dataset.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sapia.dataset.Column;
import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.conf.Conf;
import org.sapia.dataset.io.table.Table;
import org.sapia.dataset.util.Checks;
import org.sapia.dataset.util.Data;

/**
 * Default implementation of the {@link ColumnSet} interface.
 * 
 * @author yduchesne
 *
 */
public class DefaultColumnSet implements ColumnSet {
  
  private List<Column>        columns;
  private Map<String, Column> columnsByName;
  
  public DefaultColumnSet(Column...columns) {
    this(Arrays.asList(columns));
  }

  public DefaultColumnSet(List<Column> columns) {
    this.columns = new ArrayList<>(columns);
    Collections.sort(this.columns, new Comparator<Column>() {
      public int compare(Column arg0, Column arg1) {
        return arg0.getIndex() - arg1.getIndex();
      }
    });
    
    columnsByName = new HashMap<String, Column>();
    for (Column i : columns) {
      columnsByName.put(i.getName(), i);
    }
    this.columns = Collections.unmodifiableList(columns);
  }
  
  @Override
  public int[] getColumnIndices() {
    int[] indices = new int[columns.size()];
    for (int i = 0; i < indices.length; i++) {
      indices[i] = columns.get(i).getIndex();
    }
    return indices;
  }
  
  @Override
  public int[] getColumnIndices(String... columnNames) {
    int[] indices = new int[columnNames.length];
    for (int i = 0; i < columnNames.length; i++) {
      indices[i] = get(columnNames[i]).getIndex();
    }
    return indices;
  }
  
  @Override
  public Datatype[] getColumnTypes(String... columnNames) {
    Datatype[] types = new Datatype[columnNames.length];
    for (int i = 0; i < columnNames.length; i++) {
      types[i] = get(columnNames[i]).getType();
    }
    return types;
  }

  @Override
  public Column get(int colIndex) throws IllegalArgumentException {
    Checks.bounds(colIndex, columns, "Invalid column index: %s. Got %s columns", colIndex, columns.size());
    return columns.get(colIndex);
  }
  
  @Override
  public Column get(String name) throws IllegalArgumentException {
    return Checks.notNull(columnsByName.get(name), "Invalid column name: %s", name);
  }
  
  @Override
  public ColumnSet includes(String... names) throws IllegalArgumentException {
    List<Column> infos = new ArrayList<>(names.length);
    for (String name : names) {
      infos.add(get(name));
    }
    return new DefaultColumnSet(infos);
  }
  
  @Override
  public ColumnSet includes(List<String> names) throws IllegalArgumentException {
    return includes(names.toArray(new String[names.size()]));
  }
  
  @Override
  public ColumnSet excludes(String... names) throws IllegalArgumentException {
    List<Column> cols = new ArrayList<>(names.length);
    Set<String> excludes = Data.set(names);
    for (Column c : this.columns) {
      if (!excludes.contains(c.getName())) {
        cols.add(c);
      }
    }
    return new DefaultColumnSet(cols);
  }
  
  @Override
  public ColumnSet excludes(List<String> names) throws IllegalArgumentException {
    return excludes(names.toArray(new String[names.size()]));
  }
  
  @Override
  public int size() {
    return columns.size();
  }
  
  @Override
  public List<Column> getColumns() {
    return columns;
  }
  
  @Override
  public List<String> getColumnNames() {
    List<String> names = new ArrayList<>(columns.size());
    for (Column c : columns) {
      names.add(c.getName());
    }
    return names;
  }
  
  @Override
  public Iterator<Column> iterator() {
    return columns.iterator();
  }
  
  @Override
  public ColumnSet detach() {
    List<Column> newColumns = new ArrayList<>(columns.size());
    for (Column c : columns) {
      newColumns.add(new DefaultColumn(newColumns.size(), c.getType(), c.getName()));
    }
    return new DefaultColumnSet(newColumns);
  }
  
  @Override
  public boolean contains(List<String> names) {
    return contains(names.toArray(new String[names.size()]));
  }
  
  @Override
  public boolean contains(String...names) {
    for (int i = 0; i < names.length; i++) {
      Column col = this.columnsByName.get(names[i]);
      if (col == null || col.getIndex() != i) {
        return false;
      } 
    }
    return true;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ColumnSet) {
      ColumnSet other = (ColumnSet) obj;
      if (columns.size() == other.size()) {
        for (int i = 0; i < columns.size(); i++) {
          Column thisCol  = columns.get(i);
          Column otherCol = other.get(i);
          if (!thisCol.equals(otherCol) || thisCol.getIndex() != otherCol.getIndex()) {
            return false;
          }
        }
        return true;
      }
      return false;
    }
    return false;
  }
  
  @Override
  public String toString() {
    Table table = Table.obj();
    table.header("name", Conf.getCellWidth()).getStyle().alignRight();
    table.header("type", Conf.getCellWidth()).getStyle().alignCenter();
    for (Column c : columns) {
      table.row().cell(c.getName()).cell(c.getType().name());
    }
    return table.toString();  
  }
}
