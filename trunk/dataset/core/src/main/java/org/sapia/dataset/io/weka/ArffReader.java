package org.sapia.dataset.io.weka;

import java.io.IOException;
import java.io.Reader;

import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.Dataset;
import org.sapia.dataset.io.DatasetReader;

import weka.core.Instances;

/**
 * Reads ARFF-formatted datasets.
 * 
 * @author yduchesne
 *
 */
public class ArffReader implements DatasetReader {

  /**
   * This method calls {@link #read(Reader)}. The given {@link ColumnSet} is thus ignored
   * (Weka's ARFF file format supports attribute descriptions built in).
   */
  @Override
  public Dataset read(ColumnSet columns, Reader reader, int skipLines)
      throws IOException {
    return read(columns, reader, 0);
  }

  /**
   * This method calls {@link #read(Reader)}. The given {@link ColumnSet} is thus ignored
   * (Weka's ARFF file format supports attribute descriptions built in).
   */
  @Override
  public Dataset read(ColumnSet columns, Reader reader) throws IOException {
    return read(reader);
  }

  /**
   * @param reader a {@link Reader} to read from.
   * @return the {@link Dataset} holding the data provided by the given {@link Reader}.
   * @throws IOException if an IO problem occurs while reading the data.
   */
  public Dataset read(Reader reader) throws IOException {
    Instances data = new Instances(reader);
    return new WekaDatasetAdapter(data);
  }
}
