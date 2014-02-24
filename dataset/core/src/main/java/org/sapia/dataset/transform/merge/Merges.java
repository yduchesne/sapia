package org.sapia.dataset.transform.merge;

import java.util.Arrays;
import java.util.List;

import org.sapia.dataset.Dataset;
import org.sapia.dataset.help.Doc;
import org.sapia.dataset.util.Checks;

/**
 * Provides dataset merging methods.
 * 
 * @author yduchesne
 *
 */
@Doc("Provides dataset merging methods")
public class Merges {

  private Merges() {
  }
  
  /**
   * @param datasets one or more {@link Dataset}s to merge.
   * @return the {@link Dataset} resulting from the merge.
   */
  @Doc("Merges one or more datasets into one")
  public static Dataset merge(Dataset...datasets) {
    return merge(Arrays.asList(datasets));
  }
  
  /**
   * @param datasets a non-empty {@link List} of {@link Dataset}s to merge.
   * @return the {@link Dataset} resulting from the merge.
   */
  @Doc("Merges one or more datasets into one")
  public static Dataset merge(List<Dataset> datasets) {
    Checks.isFalse(datasets.isEmpty(), "Dataset list cannot be empty (one or more dataset(s) to merge must be provided)");
    return new MergedRowsDataset(datasets.get(0).getColumnSet(), datasets);
  }
}
