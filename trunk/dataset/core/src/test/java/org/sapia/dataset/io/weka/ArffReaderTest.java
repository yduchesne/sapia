package org.sapia.dataset.io.weka;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;
import org.sapia.dataset.util.Checks;

public class ArffReaderTest {
  
  @Test
  public void testLoadDataset() throws Exception {
    ArffReader reader = new ArffReader();
    InputStream is = getClass().getResourceAsStream("Test.arff");
    Checks.notNull(is, "No resource found: Test.arff");
    try {
      reader.read(new BufferedReader(new InputStreamReader(is)));
    } finally {
      is.close();
    }
  }

}
