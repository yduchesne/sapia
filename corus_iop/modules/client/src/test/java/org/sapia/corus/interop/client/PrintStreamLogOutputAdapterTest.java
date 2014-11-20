package org.sapia.corus.interop.client;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PrintStreamLogOutputAdapterTest {
  
  @Mock
  private StdLogOutput output;
  private PrintStreamLogOutputAdapter adapter;
  
  @Before
  public void setUp() {
    adapter = new PrintStreamLogOutputAdapter(output);
  }

  @Test
  public void testPrintBoolean() {
    adapter.print(true);
    assertEquals(4, adapter.getBuffer().length());
    adapter.println();
    assertEquals(0, adapter.getBuffer().length());
  }

  @Test
  public void testPrintChar() {
    adapter.print('c');
    assertEquals(1, adapter.getBuffer().length());
    adapter.println();
    assertEquals(0, adapter.getBuffer().length());
  }

  @Test
  public void testPrintInt() {
    adapter.print(1);
    assertEquals(1, adapter.getBuffer().length());
    adapter.println();
    assertEquals(0, adapter.getBuffer().length());
  }

  @Test
  public void testPrintLong() {
    adapter.print(1L);
    assertEquals(1, adapter.getBuffer().length());
    adapter.println();
    verify(output).log("1");
    assertEquals(0, adapter.getBuffer().length());
  }

  @Test
  public void testPrintFloat() {
    adapter.print(1f);
    assertEquals(3, adapter.getBuffer().length());
    adapter.println();
    verify(output).log("1.0");
    assertEquals(0, adapter.getBuffer().length());
  }

  @Test
  public void testPrintDouble() {
    adapter.print(1d);
    assertEquals(3, adapter.getBuffer().length());
    adapter.println();
    verify(output).log("1.0");
    assertEquals(0, adapter.getBuffer().length());
  }

  @Test
  public void testPrintCharArray() {
    adapter.print(new char[] { 'c' });
    assertEquals(1, adapter.getBuffer().length());
    adapter.println();
    verify(output).log("c");
    assertEquals(0, adapter.getBuffer().length());
  }

  @Test
  public void testPrintln() {
    adapter.println();
    verify(output).log("");
  }

  @Test
  public void testPrintlnBoolean() {
    adapter.println(true);
    verify(output).log("true");
  }

  @Test
  public void testPrintlnChar() {
    adapter.println("c");
    verify(output).log("c");
  }

  @Test
  public void testPrintlnInt() {
    adapter.println(1);
    verify(output).log("1");
  }

  @Test
  public void testPrintlnLong() {
    adapter.println(1L);
    verify(output).log("1");
  }

  @Test
  public void testPrintlnFloat() {
    adapter.println(1f);
    verify(output).log("1.0");
  }

  @Test
  public void testPrintlnDouble() {
    adapter.println(1d);
    verify(output).log("1.0");
  }

  @Test
  public void testPrintlnCharArray() {
    adapter.println(new char[] { 'c' });
    verify(output).log("c");
  }

  @Test
  public void testAppendChar() {
    adapter.append('c');
    assertEquals(1, adapter.getBuffer().length());
  }

  @Test
  public void testAppendCharSequence() {
    adapter.append("1");
    assertEquals(1, adapter.getBuffer().length()); 
  }

  @Test
  public void testAppendCharSequenceIntInt() {
    adapter.append("1", 0, 1);
    assertEquals(1, adapter.getBuffer().length()); 
  }

}
