package org.sapia.dataset.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class KVPairTest {
  
  private KVPair<String, Integer> kv;
  
  @Before
  public void setUp() {
    kv = new KVPair<String, Integer>("1", new Integer(1));
  }

  @Test
  public void testGetKey() {
    assertEquals("1", kv.getKey());
  }

  @Test
  public void testGetValue() {
    assertEquals(new Integer(1), kv.getValue());
  }

  @Test
  public void testObj() {
    KVPair<String, Integer> kv = KVPair.obj("1", new Integer(1));
    assertEquals("1", kv.getKey());
    assertEquals(new Integer(1), kv.getValue());
  }

  @Test
  public void testEquals() {
    KVPair<String, Integer> other = KVPair.obj("1", new Integer(1));
    assertEquals(this.kv, other);
  }

  @Test
  public void testNotEquals() {
    KVPair<String, Integer> other = KVPair.obj("1", new Integer(0));
    assertNotSame(this.kv, other);
  }

}
