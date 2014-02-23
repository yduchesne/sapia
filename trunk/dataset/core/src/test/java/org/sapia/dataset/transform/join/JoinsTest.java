package org.sapia.dataset.transform.join;

import org.junit.Before;
import org.junit.Test;
import org.sapia.dataset.ColumnSets;
import org.sapia.dataset.Dataset;
import org.sapia.dataset.Datasets;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.Vectors;
import org.sapia.dataset.util.Data;

public class JoinsTest {
  
  private Dataset left, right;
  
  @Before
  public void setUp() {
    left = Datasets.dataset(
        ColumnSets.columnSet(
            "col0", Datatype.STRING, 
            "col1", Datatype.STRING, 
            "fkey0", Datatype.STRING,
            "fkey1", Datatype.STRING
        ),
        Data.list(
            Vectors.vector("01", "001", "id0", "id1"),
            Vectors.vector("01", "002", "id0", "id1"),
            Vectors.vector("02", "003", "id2", "id3"),
            Vectors.vector("02", "004", "id2", "id3")
        )
    );
    
    right = Datasets.dataset(
        ColumnSets.columnSet(
            "col2", Datatype.STRING, 
            "col3", Datatype.STRING, 
            "key0", Datatype.STRING,
            "key1", Datatype.STRING
        ),
        Data.list(
            Vectors.vector("03", "005", "id0", "id1"),
            Vectors.vector("03", "006", "id0", "id1"),
            Vectors.vector("04", "007", "id2", "id3"),
            Vectors.vector("04", "008", "id2", "id3")
        )
    );
  }
  
  @Test
  public void testJoinDatasetDatasetJoin() {
    Join join = new Join(
        left.getColumnSet().includes("fkey0", "fkey1"), 
        right.getColumnSet().includes("key0", "key1")
    );
    Dataset joined = Joins.join(left, right, join);
    System.out.println(Datasets.toString(joined));
  }

}
