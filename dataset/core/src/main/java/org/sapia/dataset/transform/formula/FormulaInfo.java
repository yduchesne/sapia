package org.sapia.dataset.transform.formula;

import org.sapia.dataset.RowResult;

class FormulaInfo {
  
  private int                 columnIndex;
  private Formula <RowResult> function;
  
  FormulaInfo(int columnIndex, Formula<RowResult> function) {
    this.columnIndex = columnIndex;
    this.function    = function;
  } 
  
  int getColumnIndex() {
    return columnIndex;
  }
  
  Object apply(RowResult row) {
    return function.call(row);
  }
}
