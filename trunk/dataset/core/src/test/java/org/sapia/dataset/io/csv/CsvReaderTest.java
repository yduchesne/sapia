package org.sapia.dataset.io.csv;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;
import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.ColumnSets;
import org.sapia.dataset.Dataset;
import org.sapia.dataset.Datatype;

public class CsvReaderTest {
  
  private String content = 
      "col1,col2,col3" + System.lineSeparator()
      + "1,s1,2013-10-31 11:10:21.345" + System.lineSeparator()
      + "1,s2,2013-10-31 11:10:22.345" + System.lineSeparator()
      + "1,s3,2013-10-31 11:10:23.345"; 
  
  private String contentWithNull = 
      "col1,col2,col3" + System.lineSeparator()
      + "1,s1," + System.lineSeparator()
      + "1,,2013-10-31 11:10:22.345" + System.lineSeparator()
      + "1,s3,2013-10-31 11:10:23.345"; 
  
  @Test
  public void testGuessCsvContent() throws IOException {
    StringReader reader = new StringReader(content);
    CsvReader csv = new CsvReader();
    Dataset ds = csv.read(reader);
    ColumnSet cs = ds.getColumnSet();
    
    assertEquals(Datatype.NUMERIC, cs.get("col1").getType());
    assertEquals(Datatype.STRING, cs.get("col2").getType());
    assertEquals(Datatype.DATE, cs.get("col3").getType());
    assertEquals(3, ds.size());
  }
  
  @Test
  public void testGuessCsvContentWithNullValues() throws IOException {
    StringReader reader = new StringReader(contentWithNull);
    CsvReader csv = new CsvReader();
    Dataset ds = csv.read(reader);
    ColumnSet cs = ds.getColumnSet();
    
    assertEquals(Datatype.NUMERIC, cs.get("col1").getType());
    assertEquals(Datatype.STRING, cs.get("col2").getType());
    assertEquals(Datatype.DATE, cs.get("col3").getType());
    assertEquals(3, ds.size());
  }

  @Test
  public void testReadCsvWithSpecifiedColumnSetSkipFirstLine() throws IOException {
    StringReader reader = new StringReader(content);
    ColumnSet columns = ColumnSets.columnSet(
        "col1", Datatype.NUMERIC,
        "col2", Datatype.STRING,
        "col3", Datatype.DATE
    );
    
    CsvReader csv = new CsvReader();
    Dataset ds = csv.read(columns, reader, 1);
    assertEquals(3, ds.size());
  }

}
