package org.sapia.dataset.io.weka;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.Dataset;
import org.sapia.dataset.Vector;
import org.sapia.dataset.util.Checks;

public class WekaDatasetAdapterTest {
  
  private static Dataset dataset;
  
  @BeforeClass
  public static void setUp() throws Exception {
    ArffReader reader = new ArffReader();
    InputStream is = WekaDatasetAdapterTest.class.getResourceAsStream("Test.arff");
    Checks.notNull(is, "No resource found: Test.arff");
    try {
      dataset = reader.read(new BufferedReader(new InputStreamReader(is)));
    } finally {
      is.close();
    }
  }

  @Test
  public void testGetColumnInt() {
  }

  @Test
  public void testGetColumnString() {
  }

  @Test
  public void testGetColumnSet() {
    ColumnSet cols = dataset.getColumnSet();
    System.out.println(cols);
  }

  @Test
  public void testGetColumnSubsetIntCriteriaOfObject() {
  }

  @Test
  public void testGetColumnSubsetStringCriteriaOfObject() {
  }

  @Test
  public void testGetRow() {
    for (int i = 0; i < dataset.size(); i++) {
      dataset.getRow(i);
    }
  }

  @Test
  public void testGetSubset() {
  }

  @Test
  public void testIndex() {
  }

  @Test
  public void testIterator() {
    int index = 0;
    for (Vector row : dataset) {
      Vector expected = dataset.getRow(index++);
      assertEquals(expected, row);
    }
  }

  @Test
  public void testSize() {
    assertTrue(dataset.size() > 0);
  }

}
