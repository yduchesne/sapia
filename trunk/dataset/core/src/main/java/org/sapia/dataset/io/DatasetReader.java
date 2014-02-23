package org.sapia.dataset.io;

import java.io.Reader;
import java.io.IOException;

import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.Dataset;

/**
 * Specifies the behavior for reading data and loading it into a {@link Dataset}.
 * 
 * @author yduchesne
 *
 */
public interface DatasetReader {

  /**
   * @param columns the {@link ColumnSet} corresponding to the columns of the expected data set.
   * @param reader the Reader consisting of the source data.
   * @return the {@link Dataset} that was created.
   * @throws IOException if an error occurs while loading the dataset.
   */
  public Dataset read(ColumnSet columns, Reader reader) throws IOException;

  /**
   * @param columns the {@link ColumnSet} corresponding to the columns of the expected data set.
   * @param reader the Reader consisting of the source data.
   * @param skipLines the number of lines to skip form the underlying data source.
   * @return the {@link Dataset} that was created.
   * @throws IOException if an error occurs while loading the dataset.
   */
  public Dataset read(ColumnSet columns, Reader reader, int skipLines) throws IOException;
}
