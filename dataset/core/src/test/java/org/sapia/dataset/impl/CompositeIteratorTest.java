package org.sapia.dataset.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.sapia.dataset.util.Data;
import org.sapia.dataset.util.Numbers;

public class CompositeIteratorTest {

  private CompositeIterator<String> iterator;
  
  @Before
  public void setUp() {
    List<Iterator<String>> iteratorList = new ArrayList<>();
    for (int i : Numbers.range(0, 5)) {
      List<String> values = new ArrayList<String>();
      for (int j : Numbers.range(0, 5)) {
        values.add(new StringBuilder().append(i).append(j).toString());
      }
      iteratorList.add(values.iterator());
    }
    iterator = new CompositeIterator<>(iteratorList.iterator());
  }
  
  @Test
  public void testIterate() {
    Set<String> values = new HashSet<>();
    while(iterator.hasNext()) {
      String value = iterator.next();
      values.add(value);
    }
    for (int i : Numbers.range(0, 5)) {
      for (int j : Numbers.range(0, 5)) {
        String value = new StringBuilder().append(i).append(j).toString();
        assertTrue(values.contains(value));
      }
    }
  }
  
  @Test
  public void testIterateEmpty() {
    List<Iterator<String>> iteratorList = new ArrayList<>();
    CompositeIterator<String> empty = new CompositeIterator<>(iteratorList.iterator());
    assertFalse(empty.hasNext());
  }
  
  @Test
  public void testIterateFirstEmpty() {
    List<Iterator<String>> iteratorList = new ArrayList<>();
    iteratorList.add(new ArrayList<String>().iterator());
    iteratorList.add(Data.iterator("1", "2", "3"));
    CompositeIterator<String> iterator = new CompositeIterator<>(iteratorList.iterator());
    Set<String> set = Data.set(iterator);
    assertTrue("Expected iterator to return 1, 2, 3", set.containsAll(Data.set("1", "2", "3")));
  }
  
  @Test
  public void testIterateLastEmpty() {
    List<Iterator<String>> iteratorList = new ArrayList<>();
    iteratorList.add(Data.iterator("1", "2", "3"));
    iteratorList.add(new ArrayList<String>().iterator());
    CompositeIterator<String> iterator = new CompositeIterator<>(iteratorList.iterator());
    Set<String> set = Data.set(iterator);
    assertTrue("Expected iterator to return 1, 2, 3", set.containsAll(Data.set("1", "2", "3")));
  }

}
