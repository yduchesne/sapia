package org.sapia.dataset.transform.pivot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.sapia.dataset.Column;
import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.Index;
import org.sapia.dataset.RowSet;
import org.sapia.dataset.RowSets;
import org.sapia.dataset.Vector;
import org.sapia.dataset.VectorKey;
import org.sapia.dataset.conf.Conf;
import org.sapia.dataset.func.NoArgFunction;
import org.sapia.dataset.impl.IndexDatasetAdapter;
import org.sapia.dataset.io.table.Header;
import org.sapia.dataset.io.table.Row;
import org.sapia.dataset.io.table.Table;
import org.sapia.dataset.util.Tree;
import org.sapia.dataset.util.Tree.Node;

class PivotDataset extends IndexDatasetAdapter {
  
  PivotDataset(Index index) {
    super(index);
  }
  
  @Override
  public String toString() {
    
    Tree<Key, RowSet> datatree = new Tree<>(new NoArgFunction<Map<Key, Node<Key, RowSet>>>() {
      @Override
      public Map<Key, Node<Key, RowSet>> call() {
        return new TreeMap<PivotDataset.Key, Tree.Node<Key, RowSet>>();
      }
    });
    
    Collection<VectorKey> keys = getKeys();
    for (VectorKey key : keys) {
      List<Key> path = new ArrayList<>(key.size());
      for (int i = 0; i < key.size(); i++) {
        path.add(new Key(key.getColumnSet().get(i), key.get(i)));
      }
      datatree.bind(path, getRowset(key));
    }
    
    final Table table = new Table();
    
    table.fill(getIndexedColumnSet().size(), Conf.getCellWidth());
    for (int i = 0; i < getIndexedColumnSet().size() - 1; i++) {
      Header h = table.getHeader(getIndexedColumnSet().get(i).getIndex());
      h.getStyle().separator(" ").alignLeft();
    }
    table.getHeader(getIndexedColumnSet().get(getIndexedColumnSet().size() - 1).getIndex()).getStyle().alignLeft();
    
    for (Column col : getColumnSet().excludes(getIndexedColumnSet().getColumnNames())) {
      Header h = table.header(col.getName(), Conf.getCellWidth());
      h.getStyle().alignRight();
    }
    
    datatree.acceptBreadthFirst(new Tree.Visitor<PivotDataset.Key, RowSet>() {
      public void visit(Node<Key, RowSet> node) {
        if (node.getLevel() > 0) {
          if (!node.isLeaf()) {
            Column col = node.getKey().column;
            table.row().fill(node.getLevel() - 1).cell(col.getFormat().formatValue(col.getType(), node.getKey().value).trim()).fill();
          } else {
            Row r = table.row().fill(node.getLevel() - 1);
            Column pivotCol = node.getKey().column;
            r.cell(pivotCol.getFormat().formatValue(pivotCol.getType(), node.getKey().value).trim());
            ColumnSet summaryColumns = getColumnSet().excludes(getIndexedColumnSet().getColumnNames());
            Vector sum = RowSets.sum(summaryColumns.get(0).getIndex(), node.getValue());
            
            for (Column col : summaryColumns) {
              r.cell(col.getFormat().formatValue(col.getType(), sum.get(col.getIndex())));
            }
          }
        }
      }
    });
    return table.toString();
  }

  
  private static class Key implements Comparable<Key> {
    
    private Column column;
    private Object value;
    
    private Key(Column column, Object value) {
      this.column = column;
      this.value  = value;
    }
    
    @Override
    public int compareTo(Key other) {
      return column.getType().strategy().compareTo(value, other.value);
    }
    
  }
}
