package org.sapia.dataset.transform.join;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.sapia.dataset.Column;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.impl.DefaultColumn;
import org.sapia.dataset.transform.join.JoinColumn;

public class JoinColumnTest {
  
  private JoinColumn column;
  private Column     delegate;
  
  @Before
  public void setUp() {
    delegate = new DefaultColumn(1, Datatype.STRING, "test");
    column = new JoinColumn(5, delegate);
  }

  @Test
  public void testGetIndex() {
    assertEquals(5, column.getIndex());
  }

  @Test
  public void testGetFormat() {
    assertEquals(delegate.getFormat(), column.getFormat());
  }

  @Test
  public void testGetName() {
    assertEquals(delegate.getName(), column.getName());
  }

  @Test
  public void testGetParser() {
    assertEquals(delegate.getParser(), column.getParser());
  }

  @Test
  public void testGetType() {
    assertEquals(delegate.getType(), column.getType());
  }

}
