package org.sapia.dataset.transform.formula;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sapia.dataset.Column;
import org.sapia.dataset.Dataset;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.help.Doc;
import org.sapia.dataset.impl.DefaultColumn;
import org.sapia.dataset.impl.DefaultColumnSet;
import org.sapia.dataset.util.Tuple;

/**
 * The formulas to hold.
 */
public class Formulas {
  
  private Formulas() {
  }

  /**
   * @param dataset the {@link Dataset} to which to add formulas.
   * @param formulas {@link Tuple} instances holding: a) the {@link Datatype} of the formula's result; 
   * b) the column name of the formula; c) the {@link Formula} itself.
   * 
   * @return a new {@link Dataset}, with new columns added for the given formulas.
   */
  @Doc("returns a dataset that adds the given formulas to the provided dataset")
  public static Dataset addFormulas(
      @Doc("a dataset to which to add formulas") Dataset dataset, 
      @Doc("one or more tuples, each holding: " + 
       "a) a formula column name; b) a formula column type; " + 
       "c) the ArgFunction instance corresponding to the formula") Tuple...formulas) {
    return addFormulas(dataset, Arrays.asList(formulas));
  }
  
  /**
   * @param dataset the {@link Dataset} to which to add formulas.
   * @param formulas {@link Tuple} instances holding: a) the {@link Datatype} of the formula's result; 
   * b) the column name of the formula; c) the {@link Formula} itself.
   * 
   * @return a new {@link Dataset}, with new columns added for the given formulas.
   */
  @SuppressWarnings("unchecked")
  @Doc("returns a dataset that adds the given formulas to the provided dataset")
  public static Dataset addFormulas(
      @Doc("a dataset to which to add formulas") Dataset dataset, 
      @Doc("one or more tuples, each holding: " + 
          "a) a formula column name; b) a formula column type; " + 
          "c) the ArgFunction instance corresponding to the formula") List<Tuple> formulas) {
    List<Column>      columns = new ArrayList<>();
    List<FormulaInfo> forms   = new ArrayList<>();
    
    columns.addAll(dataset.getColumnSet().getColumns());
    
    for (int i = 0; i < formulas.size(); i++) {
      Tuple f = formulas.get(i);
      forms.add(new FormulaInfo(dataset.getColumnSet().size() + i, f.get(Formula.class)));
      columns.add(new DefaultColumn(dataset.getColumnSet().size() + i, f.getNotNull(Datatype.class), f.getNotNull(String.class)));
    }
    
    DefaultColumnSet cs = new DefaultColumnSet(columns);
    FormulaDataset   ds = new FormulaDataset(dataset, cs, forms);
    return ds;
  }
}
