package org.sapia.dataset.transform.slice;

import org.sapia.dataset.Dataset;
import org.sapia.dataset.conf.Conf;
import org.sapia.dataset.help.Doc;
import org.sapia.dataset.util.Checks;

/**
 * Provides slicing operations.
 * 
 * @author yduchesne
 *
 */
@Doc("Provides slicing operations (obtaining parts of the rows of a dataset)")
public class Slices {
  
  private static final int QUARTILE_PORTION_COUNT = 4;
  
  private static final int QUINTILE_PORTION_COUNT = 5;

  private Slices() {
  }
  
  /**
   * @param dataset returns the top N percent of the rows in the given dataset.
   * @param percent the actual percentage to use (0.5, 0.95, etc.).
   * @return the {@link Dataset} corresponding to the top N percent rows.
   */
  @Doc("Returns the top n percent of the rows in the given dataset, as a new dataset")
  public static Dataset top(
      @Doc("the dataset to slice") Dataset dataset, 
      @Doc("a percentage (0.8, 0.95, etc.)") double percent) {
    Checks.isTrue(percent >= 0 && percent <= 1, "Percentage must be between 0 and 1, inclusively. Got: %s", percent);
    int end = (int) (percent * dataset.size());
    return slice(dataset, 0, end);
  }
  
  /**
   * @param dataset returns the bottom N percent of the rows in the given dataset.
   * @param percent the actual percentage to use (0.5, 0.95, etc.).
   * @return the {@link Dataset} corresponding to the bottom N percent rows.
   */
  @Doc("Returns the bottom n percent of the rows in the given dataset, as a new dataset")
  public static Dataset bottom(
      @Doc("the dataset to slice") Dataset dataset, 
      @Doc("a percentage (0.8, 0.95, etc.)") double percent) {
    Checks.isTrue(percent >= 0 && percent <= 1, "Percentage must be between 0 and 1, inclusively. Got: %s", percent);
    int start = dataset.size() - (int) (percent * dataset.size());
    return slice(dataset, start, dataset.size());
  }
  
  /**
   * @param dataset the {@link Dataset} whose head should be returned.
   * @return a new {@link Dataset}, holding the rows of the given dataset that correspond
   * to its head.
   */
  @Doc("Returns the head of the given dataset, as a new dataset")
  public static Dataset head(Dataset dataset) {
    return slice(dataset, 0, Conf.getHeadLength());
  }
 
  /**
   * @param dataset the {@link Dataset} whose head should be returned.
   * @return a new {@link Dataset}, holding the rows of the given dataset that correspond
   * to its tail.
   */
  @Doc("Returns the tail of the given dataset, as a new dataset")
  public static Dataset tail(Dataset dataset) {
    return slice(dataset, dataset.size() - Conf.getTailLength(), dataset.size());
  }
  
  /**
   * @param dataset a {@link Dataset} to slice.
   * @param start the row index at which to start slicing (inclusive).
   * @param end the row index at which to start slicing (exclusive).
   * @return a new {@link Dataset}, corresponding to the desired slice.
   */
  @Doc("Slices the given dataset, returning the resulting slice as a dataset itself")
  public static Dataset slice(
      @Doc("the dataset to slice") Dataset dataset, 
      @Doc("the start index") int start, 
      @Doc("the end index") int end) {
    Checks.isTrue(end >= start, "End index must be greater than or equal to start index");
    int theEnd = end < dataset.size() ? end : dataset.size();
    int theStart = start >= 0 ? start : 0;
    return new SliceDataset(dataset, theStart, theEnd);
  }
  
  /**
   * @param dataset the {@link Dataset} whose desired quartile slice should be returned.
   * @param quartileNumber (a number indicating which quartile is desired: 1, 2, 3, or 4).
   * @return a new {@link Dataset}, corresponding to the desired quartile.
   */
  @Doc("Returns the slice corresponding to a given quartile")
  public static Dataset quartile(
      @Doc("the dataset to slice") Dataset dataset, 
      @Doc("a quartile number (1-4)") int quartileNumber) {
    return partition(dataset, quartileNumber, QUARTILE_PORTION_COUNT);
  }
  
  /**
   * @param dataset the {@link Dataset} whose desired quintile slice should be returned.
   * @param quintile (a number indicating which quintile is desired: 1, 2, 3, 4, or 5).
   * @return a new {@link Dataset}, corresponding to the desired quartile.
   */
  @Doc("Returns the slice corresponding to a given quartile")
  public static Dataset quintile(
      @Doc("the dataset to slice") Dataset dataset, 
      @Doc("the quintile number (1-5)") int quintileNumber) {
    return partition(dataset, quintileNumber, QUINTILE_PORTION_COUNT);
  }
  
  /**
   * @param dataset the {@link Dataset} whose desired portion slice should be returned.
   * @param partitionNumber the portion number (1 to partitionCount - see next param).
   * @param partitionCount the total number of partitions to divide into.
   * @return a new {@link Dataset}, corresponding to the desired partition.
   */
  @Doc("Returns a proportion of the given dataset (given a number of parts into which the dataset should be subdivided)")
  public static Dataset partition(
      @Doc("the dataset to slice") Dataset dataset, 
      @Doc("the partition number (1 to <partition count>) - see next param") int partitionNumber, 
      @Doc("the partition count: the number of portions to divide the dataset into") int partitionCount) {
    Checks.isTrue(
        partitionNumber > 0 && partitionNumber <= partitionCount, 
        "Expected between 1 and %s, got: %s", 
        partitionNumber, partitionCount);
    int portionLen = dataset.size() / partitionCount;
    int start      = (partitionNumber - 1) * portionLen;
    int end        = start + portionLen;
    if (partitionNumber == partitionCount) {
      end = dataset.size();
    } 
    return slice(dataset, start, end);
  }
 
}
