package org.sapia.dataset.util;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class DataTest {
  
  @Test
  public void testPartitionOddPartitions() {
    List<Integer> values = Data.listOfInts(0, 1, 2, 3, 4);
    
    List<List<Integer>> result = Data.partition(values, 2);
    
    assertEquals(3, result.size());
    
    assertEquals(0, result.get(0).get(0).intValue());
    assertEquals(1, result.get(0).get(1).intValue());
    
    assertEquals(2, result.get(1).get(0).intValue());
    assertEquals(3, result.get(1).get(1).intValue());
    
    assertEquals(4, result.get(2).get(0).intValue());
  }
  
  @Test
  public void testPartitionEvenPartitions() {
    List<Integer> values = Data.listOfInts(0, 1, 2, 3);
    
    List<List<Integer>> result = Data.partition(values, 2);
    
    assertEquals(2, result.size());
    
    assertEquals(0, result.get(0).get(0).intValue());
    assertEquals(1, result.get(0).get(1).intValue());
    
    assertEquals(2, result.get(1).get(0).intValue());
    assertEquals(3, result.get(1).get(1).intValue());
  }
  
  @Test
  public void testSliceListWithMax() {
    List<Integer> values = Data.listOfInts(0, 1, 2, 3, 4);
    List<Integer> slice  = Data.slice(values, 2);
    assertEquals(2, slice.size());
    assertEquals(new Integer(0), slice.get(0));
    assertEquals(new Integer(1), slice.get(1));
  }
  
  @Test
  public void testSliceListWithStartMax() {
    List<Integer> values = Data.listOfInts(0, 1, 2, 3, 4);
    List<Integer> slice  = Data.slice(values, 1, 3);
    assertEquals(2, slice.size());
    assertEquals(new Integer(1), slice.get(0));
    assertEquals(new Integer(2), slice.get(1));
  }

  @Test
  public void testSetWithIterator() {
    Set<Integer> set = Data.set(Data.listOfInts(1, 2, 3).iterator());
    assertTrue(set.containsAll(Data.list(1, 2, 3)));
  }

  @Test
  public void testListWithIterator() {
    List<Integer> list = Data.list(Data.setOfInts(1, 2, 3).iterator());
    assertTrue(list.containsAll(Data.set(1, 2, 3)));
  }

  @Test
  public void testListWithArray() {
    ArrayList<Integer> expected = new ArrayList<>();
    expected.add(new Integer(0));
    expected.add(new Integer(1));
    expected.add(new Integer(2));
    assertTrue(Data.list(new Integer(0), new Integer(1), new Integer(2)).containsAll(expected));
  }

  @Test
  public void testSetWithArray() {
    Set<Integer> expected = new HashSet<>();
    expected.add(new Integer(0));
    expected.add(new Integer(1));
    expected.add(new Integer(2));
    assertTrue(Data.set(new Integer(0), new Integer(1), new Integer(2)).containsAll(expected));
  }

  @Test
  public void testIterator() {
    Iterator<Integer> iterator = Data.listOfInts(0, 1, 2, 3).iterator();
    for (int i : Numbers.range(4)) {
      assertEquals(new Integer(i), iterator.next());
    }
  }

  @Test
  public void testArrayListOfObject() {
    List<Object> list = new ArrayList<>();
    for (int i : Numbers.range(4)) {
      list.add(new Integer(i));
    }
    
    Object[] array = Data.array(list);
    for (int i : Numbers.range(4)) {
      assertEquals(new Integer(i), array[i]);
    }
  }

  @Test
  public void testContainsAny() {
    List<Object> list = new ArrayList<>();
    for (int i : Numbers.range(4)) {
      list.add(new Integer(i));
    }
    for (int i : Numbers.range(4)) {
      assertTrue(Data.containsAny(new Object[] {i}, list));
    }
  }
  
  @Test
  public void testContainsAnyFalse() {
    List<Object> list = new ArrayList<>();
    for (int i : Numbers.range(4)) {
      list.add(new Integer(i));
    }
    assertFalse(Data.containsAny(new Object[] {4}, list));
  }

  @Test
  public void testContainsAll() {
    List<Object> list = new ArrayList<>();
    for (int i : Numbers.range(4)) {
      list.add(new Integer(i));
    }
    assertTrue(Data.containsAll(list.toArray(new Integer[list.size()]), list));
  }
  
  @Test
  public void testContainsAllFalse() {
    List<Object> list = new ArrayList<>();
    for (int i : Numbers.range(3)) {
      list.add(new Integer(i));
    }
    assertTrue(Data.containsAll(list.toArray(new Integer[list.size()]), list));
  }
  
  @Test
  public void testMap() {
    Map<Object, Object> map = Data.map("k1", "v1", "k2", "v2", "k3");
    assertEquals("v1", map.get("k1"));
    assertEquals("v2", map.get("k2"));
    assertNull(map.get("k3"));
  }

}
