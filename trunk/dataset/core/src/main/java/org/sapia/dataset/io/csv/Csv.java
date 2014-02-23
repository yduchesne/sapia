package org.sapia.dataset.io.csv;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.Dataset;
import org.sapia.dataset.help.Doc;
import org.sapia.dataset.help.Hide;
import org.sapia.dataset.help.SettingsDoc;
import org.sapia.dataset.util.Checks;
import org.sapia.dataset.util.Settings;

/**
 * A utility class for building {@link Dataset}s out of CSV files.
 * 
 * @author yduchesne
 *
 */
@Doc("A builder class for CSV datasets.")
public class Csv {
  
  private static final Settings SETTINGS = Settings.obj()
      .setting().name("file").type(File.class)
        .description("a CSV file")
        .mandatory()
      .setting().name("file_name").type(String.class)
        .description("name of a CSV file")
        .mandatory()
      .setting().name("guess_columns").type(Boolean.class)
        .description("if true, indicates that dataset column names and types should be guessed from the content")
      .setting().name("quote_char").type(String.class)
        .description("quote character to use - none used by default")
      .setting().name("separator").type(String.class)
        .description("separator character to use (defaults to comma - ',')")
      .setting().name("skip_lines").type(Integer.class)
        .description("the number of lines to skip from the top of the CSV - none by default")
      .setting().name("column_set").type(ColumnSet.class)
        .description("ColumnSet describing the CSV file's columns (does not have to be specified if guess_columns == true)")
        .mandatory()
      .finish();

  private Character quoteChar;
  private char      separator  = ',';
  private Reader    reader;
  private File      file;
  private ColumnSet columns;
  private int       skipLines;
  
  private Csv() {
  }
  
  /**
   * @param file the {@link File} from which to read the data.
   * @return this instance.
   */
  @Hide
  public Csv file(File file) {
    Checks.isTrue(reader == null, "Both file and reader cannot be set");
    this.file = file;
    return this;
  }
  
  /** 
   * @param lines the number of lines to skip when loading the dataset.
   * @return this instance.
   */
  @Hide
  public Csv skipLines(int lines) {
    Checks.isTrue(lines >= 0, "Invalid number of lines to skip (must be a positive number): %s", lines);
    this.skipLines = lines;
    return this;
  }
  
  /**
   * @param reader the {@link Reader} from which to read the data.
   * @return this instance.
   */
  @Hide
  public Csv reader(Reader reader) {
    Checks.isTrue(file == null, "Both file and reader cannot be set");
    this.reader = reader;
    return this;
  }
 
  /**
   * @param columns the {@link Csv} instance to build out of this.
   * @return this instance.
   */
  @Hide
  public Csv columns(ColumnSet columns) {
    this.columns = columns;
    return this;
  }
  
  /**
   * @param quote the quote character to use (none is used by default).
   * @return this instance.
   */
  @Hide
  public Csv quoteChar(char quote) {
    quoteChar = new Character(quote);
    return this;
  }
  
  /**
   * @param separator the separator character to use (defaults to comma).
   * @return this instance.
   */
  @Hide
  public Csv separator(char separator) {
    this.separator = separator;
    return this;
  }
  
  /**
   * @return a new {@link Dataset}.
   * @throws IOException if an IO error occurs while reading the data to build the dataset.
   */
  @Hide
  public Dataset build() throws IOException {
    Checks.isTrue(reader != null || file != null, "Either file or reader must be set");
    if (file != null) {
      return new CsvReader(separator, quoteChar).read(columns, new FileReader(file), skipLines);
    } else {
      return new CsvReader(separator, quoteChar).read(columns, reader, skipLines);
    }
  }
  
  /**
   * @return a new instance of this class.
   */
  @Hide
  public static Csv obj() {
    return new Csv();
  }
  
  /**
   * @param values the {@link Map} of setting values to use.
   * @return a new {@link Dataset}.
   * @throws IOException if an IO error occurs attempting to load the dataset.
   */
  @Doc("Builds a new dataset, using the provided settings")
  public static Dataset read(@SettingsDoc("SETTINGS") @Doc("Note that either file or file_name must be specified") Map<String, Object> values) throws IOException {
    File file;
    if (values.containsKey("file_name")) {
      String fileName = SETTINGS.get("file_name").get(values, String.class);
      file = new File(fileName);
    } else {
      file = SETTINGS.get("file").get(values, File.class);
    }
    Checks.isTrue(file.exists(), "File not found: ", file.getAbsolutePath());
    Checks.isFalse(file.isDirectory(), "File is a directory: ", file.getAbsolutePath());
    Checks.isTrue(file.canRead(), "Cannot read file: ", file.getAbsolutePath());
    

    Character quoteChar = null;
    if (values.containsKey("quote_char")) {
      String quoteCharValue = SETTINGS.get("quote_char").get(values, String.class);
      if (quoteCharValue != null) {
        quoteChar = quoteCharValue.charAt(0);
      }
    }
    String separator = SETTINGS.get("separator").get(values, ",", String.class);
    
    if (values.containsKey("guess_columns") && SETTINGS.get("guess_columns").get(values, Boolean.FALSE, Boolean.class)) {
      FileReader reader = new FileReader(file);
      try {
        return new CsvReader(new Character(separator.charAt(0)), quoteChar).read(reader);
      } finally {
        reader.close();
      }
    } else {
      ColumnSet columns = SETTINGS.get("column_set").get(values, ColumnSet.class);
      int skipLines = SETTINGS.get("skip_lines").get(values, new Integer(0), Integer.class);
      FileReader reader = new FileReader(file);
      try {
        return new CsvReader(new Character(separator.charAt(0)), quoteChar).read(columns, reader, skipLines);
      } finally {
        reader.close();
      }
    }
  }
 
}
