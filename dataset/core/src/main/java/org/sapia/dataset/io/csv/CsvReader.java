package org.sapia.dataset.io.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.sapia.dataset.Column;
import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.Dataset;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.Vector;
import org.sapia.dataset.impl.DefaultColumn;
import org.sapia.dataset.impl.DefaultColumnSet;
import org.sapia.dataset.impl.DefaultDataset;
import org.sapia.dataset.impl.DefaultVector;
import org.sapia.dataset.io.DatasetReader;
import org.sapia.dataset.io.helpers.ColumnTypeGuesser;
import org.sapia.dataset.io.helpers.Line;
import org.sapia.dataset.io.helpers.LineBuffer;
import org.sapia.dataset.util.Checks;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Implements a {@link DatasetReader} that reads from CSV files.
 * 
 * @author yduchesne
 *
 */
public class CsvReader implements DatasetReader {
  
  private char      separator = ',';
  private Character quoteChar;
  
  public CsvReader() {
  }
  
  public CsvReader(Character separator) {
    if (separator == null) {
      separator = ',';
    } else {
      this.separator = separator;
    }
  }
  
  public CsvReader(Character separator, Character quoteChar) {
    this(separator);
    this.quoteChar = quoteChar;
  }
  
  @Override
  public Dataset read(ColumnSet columns, Reader reader) throws IOException {
    return read(columns, reader, 0);
  }
  
  /**
   * This method attempts to create a {@link Dataset} straight out of a CSV input, assuming that the 
   * first line of the input consists of column names. It also attempts to determine the type of each
   * column as it reads the data.
   * 
   * @param reader some CSV input, provided as a {@link Reader}.
   * @return the {@link Dataset} that was parsed out from the input.
   * @throws IOException if an error occurs attempting to read the CSV input.
   */
  public Dataset read(Reader reader) throws IOException {
    CSVReader csv = null;
    if (quoteChar != null) {
      csv = new CSVReader(reader, separator, quoteChar.charValue());
    } else {
      csv = new CSVReader(reader, separator);
    }
    
    List<Vector>      rows        = new ArrayList<>();
    String[]          columnNames = null;
    Datatype[]        columnTypes = null;
    ColumnTypeGuesser guesser     = new ColumnTypeGuesser();
    LineBuffer        buffer      = new LineBuffer();
    ColumnSet         columnSet   = null;
    
    try {
      String[] line;
      int lineNumber = 0;
      while ((line = csv.readNext()) != null) {
        if (lineNumber == 0) {
          columnNames = line;
        } else if (columnTypes == null) {
          CsvLine csvLine = new CsvLine(line);
          columnTypes = guesser.guessColumnType(csvLine);
          if (columnTypes == null) {
            buffer.append(csvLine);
          } else {
            buffer.append(csvLine);
            List<Column> columns = new ArrayList<>();
            for (int i = 0; i < columnNames.length; i++) {
              columns.add(new DefaultColumn(i, columnTypes[i], columnNames[i]));
            }
            columnSet = new DefaultColumnSet(columns);
            for (Line l : buffer.getLines()) {
              Object[] values = new Object[columnSet.size()];
              for (Column col : columnSet) {
                String value = l.get(col.getIndex());
                if (value == null || value.trim().length() == 0) {
                  values[col.getIndex()] = null;
                } else {
                  values[col.getIndex()] = col.getParser().parse(value);
                }
              }
              rows.add(new DefaultVector(values));
            }
          }
        } else {
          Object[] values = new Object[columnSet.size()];
          for (Column col : columnSet) {
            String value = line[col.getIndex()];
            if (value == null || value.trim().length() == 0) {
              values[col.getIndex()] = null;
            } else {
              values[col.getIndex()] = col.getParser().parse(line[col.getIndex()]);
            }
          }
          rows.add(new DefaultVector(values));
        }
        lineNumber++;
      }
      if (columnTypes == null) {
        throw new IOException("Column data types could not be determined from input");
      }
      return new DefaultDataset(columnSet, rows);
    } finally {
      csv.close();
    } 
    
  }
  
  public Dataset read(ColumnSet columns, Reader reader, int skipLines) throws IOException {
    CSVReader csv = null;
    if (quoteChar != null) {
      csv = new CSVReader(reader, separator, quoteChar.charValue());
    } else {
      csv = new CSVReader(reader, separator);
    }
    List<Vector> rows = new ArrayList<>();
    try {
      String[] line;
      int lineNumber = 0;
      while ((line = csv.readNext()) != null) {
        if (skipLines == 0 || lineNumber >= skipLines) {
          Checks.isTrue(line.length == columns.size(), 
              "Error at line %s: expected CSV to have %s columns, got: %s", 
              lineNumber + 1, columns.size(), line.length
          );
          Object[] values = new Object[columns.size()];
          for (Column col : columns) {
            values[col.getIndex()] = col.getParser().parse(line[col.getIndex()]);
          }
          rows.add(new DefaultVector(values));
        }
        lineNumber++;
      }
      return new DefaultDataset(columns, rows);
    } finally {
      csv.close();
    } 
  }
}
