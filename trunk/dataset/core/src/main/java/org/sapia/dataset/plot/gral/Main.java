package org.sapia.dataset.plot.gral;

import static org.sapia.dataset.Datasets.dataset;

import java.util.ArrayList;
import java.util.List;

import org.sapia.dataset.ColumnSets;
import org.sapia.dataset.Dataset;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.Vector;
import org.sapia.dataset.Vectors;
import org.sapia.dataset.plot.Plot;
import org.sapia.dataset.plot.Plots;
import org.sapia.dataset.util.Data;
import org.sapia.dataset.util.SettingValues;

public class Main  {

  public Main() {
     
    Dataset dataset = dataset(
        ColumnSets.columnSet(
          "x", Datatype.NUMERIC,
          "category", Datatype.STRING
        ), 
        Data.list(
            Vectors.vector(3, "silver"),
            Vectors.vector(2, "bronze"),
            Vectors.vector(3, "silver"),
            Vectors.vector(4, "gold"),
            Vectors.vector(2, "bronze"),
            Vectors.vector(2, "bronze"),
            Vectors.vector(5, "diamond")
        )
        
    );
    
    Plot plot = Plots.pieplot(
        dataset, 
        SettingValues.valueOf(
            "title", "Example", 
            "column", "x"
        )
    );
    plot.display();
  }
  
  public static void main(String[] args) {
    Main main = new Main();
  }
}
