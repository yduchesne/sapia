package org.sapia.dataset.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.sapia.dataset.algo.Criteria;
import org.sapia.dataset.func.ArgFunction;
import org.sapia.dataset.help.Doc;

/**
 * Provides utility methods pertaining to data structures.
 * 
 * @author yduchesne
 *
 */
public class Data {
  
  private Data() {
  }

  /**
   * @param input an {@link Iterable} to filter.
   * @param condition the {@link Criteria} to apply to each item in the input: all items
   * for which the {@link Criteria} returns <code>true</code> will be added to the list
   * that will be returned.
   * @return a {@link List} with the filtered input.
   */
  public static <T> List<T> filter(Iterable<T> input, Criteria<T> condition) {
    List<T> toReturn = new ArrayList<>();
    for (T i : input) {
      if (condition.matches(i)) {
        toReturn.add(i);
      }
    }
    return toReturn;
  }

  /**
   * @param input the {@link List}.
   * @param func the {@link ArgFunction} to use to transform the given input.
   * @return the transformed {@link List}.
   */
  public static <F, T> List<T> transform(List<F> input, ArgFunction<F, T> func) {
    List<T> toReturn = new ArrayList<>(input.size());
    for (F i : input) {
      toReturn.add(func.call(i));
    }
    return toReturn;
  }
  
  /**
   * @param input the {@link Iterable}.
   * @param func the {@link ArgFunction} to use to transform the given input.
   * @return the transformed {@link List}.
   */
  public static <F, T> List<T> transform(Iterable<F> input, ArgFunction<F, T> func) {
    List<T> toReturn = new ArrayList<>();
    for (F i : input) {
      toReturn.add(func.call(i));
    }
    return toReturn;
  }

  /**
   * @param input an input {@link Iterable} to partition into lists of the given size.
   * @param partitionSize the max size of each list into which the given collection will be partitioned.
   * input into the expected ones in the output list.
   * @return the {@link List} of resulting partitions, themselves given as lists.
   */
  public static <T> List<List<T>> partition(Iterable<T> input, int partitionSize) {
    return transformAndPartition(input, partitionSize, new ArgFunction<T, T>() {
      @Override
      public T call(T arg) {
        return arg;
      }
    });
  }
  
  /**
   * @param input an input {@link Iterable} to partition into lists of the given size.
   * @param partitionSize the max size of each list into which the given collection will be partitioned.
   * @param func the {@link ArgFunction} to call in order to transform the items in the
   * input into the expected ones in the output list.
   * @return the {@link List} of resulting partitions, themselves given as lists.
   */
  public static <F, T> List<List<T>> transformAndPartition(Iterable<F> input, int partitionSize, ArgFunction<F, T> func) {
    Checks.isFalse(partitionSize <= 0, "Partition size must be greater than or equal to 0");
    Iterator<F> itr = input.iterator();
    if (!itr.hasNext()) {
      return new ArrayList<>();
    } 
    List<List<T>> partitions = new ArrayList<>();
    List<T> currentPartition = null;
    for (F f : input) {
      if (currentPartition == null) {
        currentPartition = new ArrayList<>();
      }  else if (currentPartition.size() >= partitionSize) {
        partitions.add(currentPartition);
        currentPartition = new ArrayList<>();
      }
      currentPartition.add(func.call(f));
    }
    if (currentPartition != null && !currentPartition.isEmpty()) {
      partitions.add(currentPartition);
    }
    return partitions;
  }
  
  /**
   * @param lst a {@link List} whose first element should be retrieved.
   * @return the first element in the given list.
   */
  public static <T> T first(List<T> lst) {
    Checks.isFalse(lst.isEmpty(), "List is empty");
    return lst.get(0);
  }
  
  /**
   * @param lst a {@link List} whose last element should be retrieved.
   * @return the last element in the given list.
   */
  public static <T> T last(List<T> lst) {
    Checks.isFalse(lst.isEmpty(), "List is empty");
    return lst.get(lst.size() - 1);
  }
  
  /**
   * @param toSlice the {@link List} to slice.
   * @param max the index up to which to slice - exclusive.
   * @return a {@link List} corresponding to the desired slice.
   */
  public static <T> List<T> slice(List<T> toSlice, int max) {
    List<T> sliced = new ArrayList<>();
    for (int i = 0; i < toSlice.size() && i < max; i++) {
      sliced.add(toSlice.get(i));
    }
    return sliced;
  }
  
  /**
   * @param toSlice the {@link List} to slice.
   * @param start the index at which to start slicing.
   * @param max the index up to which to slice - exclusive.
   * @return a {@link List} corresponding to the desired slice.
   */
  @Doc("Returns a slice from the given list")
  public static <T> List<T> slice(List<T> toSlice, int start, int max) {
    
    List<T> sliced = new ArrayList<>(max - start);
    if (max >= toSlice.size()) {
      max = toSlice.size();
    }
    if (start >= max) {
      start = max - 1;
    }
    for (int i = start; i < toSlice.size() && i < max; i++) {
      sliced.add(toSlice.get(i));
    }
    return sliced;
  }
  
  /**
   * @param items an {@link Iterator} of given items.
   * @return the {@link Set} holding the items returned by the iterator.
   */
  @Doc("Returns a set holding the objects provided by the given iterator")
  public static <T> Set<T> set(Iterator<T> items) {
    Set<T> toReturn = new HashSet<>();
    while (items.hasNext()) {
      toReturn.add(items.next());
    }
    return toReturn;
  }
  
  /**
   * @param items an {@link Iterator} of given items.
   * @return the {@link List} holding the items returned by the iterator.
   */
  @Doc("Returns a list holding the objects provided by the given iterator")
  public static <T> List<T> list(Iterator<T> items) {
    List<T> toReturn = new ArrayList<>();
    while (items.hasNext()) {
      toReturn.add(items.next());
    }
    return toReturn;
  }
  
  /**
   * @param items some items.
   * @return a {@link List} wrapping the given items.
   */
  @SafeVarargs
  @Doc("Returns a list for the given varargs")
  public static <T> List<T> list(T...items) {
    return Arrays.asList(items);
  }
  
  /**
   * @param items some items.
   * @return a {@link List} wrapping the given items.
   */
  @SafeVarargs
  @Doc("Returns a list for the given integer varargs")
  public static List<Integer> listOfInts(int...items) {
    List<Integer> list = new ArrayList<>(items.length);
    for (int i : items) {
      list.add(new Integer(i));
    }
    return list;
  }

  /**
   * @param items some items.
   * @return a {@link Set} wrapping the given items.
   */
  @SafeVarargs
  @Doc("Returns a set for the given varargs")
  public static <T> Set<T> set(T...items) {
    Set<T> set = new HashSet<>();
    for (T i : items) {
      set.add(i);
    }
    return set;
  }
  
  /**
   * @param items some items.
   * @return a {@link Set} wrapping the given items.
   */
  @SafeVarargs
  @Doc("Returns a set for the given integer varargs")
  public static Set<Integer> setOfInts(int...items) {
    Set<Integer> set = new HashSet<>();
    for (int i : items) {
      set.add(i);
    }
    return set;
  }
  
  /**
   * @param items some items.
   * @return an {@link Iterator} wrapping the given items.
   */
  @SafeVarargs
  @Doc("Returns an iterator over the given vargargs")
  public static <T> Iterator<T> iterator(final T...items) {
    return new Iterator<T>() {
      private int index;
      @Override
      public boolean hasNext() {
        return index < items.length;
      }
      
      @Override
      public T next() {
        if (index > items.length - 1) {
          throw new NoSuchElementException();
        }
        return items[index++];
      }
      
      @Override
      public void remove() {
      }
    };
  }
  
  /**
   * @param values some values.
   * @return a new {@link Tuple} holding the given values.
   */
  public static Tuple tuple(Object...values) {
    return new Tuple(values);
  }
  
  /**
   * @param items some items.
   * @return an array wrapping the given items.
   */
  @SafeVarargs
  @Doc("Returns an array for the given vargargs")
  public static <T> T[] array(T...items) {
    return items;
  }

  /**
   * @param items a list of items.
   * @return the array corresponding to the given list.
   */
  @Doc("Returns an array for the given list of items")
  public static Object[] array(List<Object> items) {
    return items.toArray(new Object[items.size()]);
  }
  
  /**
   * @param criteria the array of values whose presence in the given collection should be checked.
   * @param collection a {@link Collection}.
   * @return <code>true</code> if any one of the given criteria is found in the collection.
   */
  @Doc("Tests if a given collection contains any of the items provided in an array")
  public static <C> boolean containsAny(C[] criteria, Collection<C> collection) {
    for (C c : criteria) {
      if (collection.contains(c)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * @param criteria the array of values whose presence in the given collection should be checked.
   * @param collection a {@link Collection}.
   * @return <code>true</code> if any one of the given criteria is found in the collection.
   */
  @Doc("Tests if a given collection contains all the items provided in an array")
  public static <C> boolean containsAll(C[] criteria, Collection<C> collection) {
    for (C c : criteria) {
      if (!collection.contains(c)) {
        return false;
      }
    }
    return true;
  }
  
  /**
   * 
   * @param args the arguments, consisting of a sequence of key/value pairs.
   * @return the {@link Map} corresponding to the given arguments.
   */
  @Doc("Builds a map out of the given array of key/value pairs")
  public static Map<Object, Object> map(Object...args) {
    Map<Object, Object> map = new HashMap<Object, Object>();
    for (int i = 0; i < args.length; i++) {
      if (i + 1 < args.length) {
        map.put(args[i], args[i + 1]);
      }
    }
    return map;
  }

}
