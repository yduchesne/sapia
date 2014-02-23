package org.sapia.dataset.io.weka;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.sapia.dataset.Dataset;
import org.sapia.dataset.util.Checks;

/**
 * A utility class to build {@link Dataset}s out of {@link File}s or {@link Reader} 
 * instances.
 * 
 * @author yduchesne
 *
 */
public class Arff {

  private Reader reader;
  private File   file;
  
  private Arff() {
  }
  
  /**
   * @param reader the {@link Reader} to read the data from.
   * @return this instance.
   */
  public Arff reader(Reader reader) {
    Checks.isTrue(file == null, "Cannot provide file and Reader at same time");
    this.reader = reader;
    return this;
  }
  
  /**
   * @param file the {@link File} to read the data from.
   * @return this instance.
   */
  public Arff file(File file) {
    Checks.isTrue(reader == null, "Cannot provide file and Reader at same time");
    this.file = file;
    return this;
  }
  
  /**
   * Builds a new {@link Dataset}, given this instance's configuration.
   * 
   * @return a new {@link Dataset}.
   * @throws IOException
   */
  public Dataset build() throws IOException {
    Checks.isFalse(reader == null && file == null, "File or Reader must be provided");
    ArffReader arff = new ArffReader();
    if (reader != null) {
      return arff.read(reader);
    } else {
      return arff.read(new FileReader(file));
    }
  }
  
  /**
   * @return a new instance of this class.
   */
  public static Arff obj() {
    return new Arff();
  }
}
