package org.sapia.dataset.util;

import java.util.Collection;

/**
 * Holds various assertion  methods.
 * 
 * @author yduchesne
 *
 */
public class Checks {

  private Checks() {
  }
  
  public static <T> T notNull(T toCheck, String msg) {
    isFalse(toCheck == null, msg);
    return toCheck;
  }
  
  public static <T> T notNull(T toCheck, String msg, Object...args) {
    isFalse(toCheck == null, msg, args);
    return toCheck;
  }
  
  public static <T> void bounds(int index, T[] array) {
    isTrue(index >= 0 && index < array.length, "Invalid index: %s. Got %s items", index, array.length);
  }
  
  public static <T> void bounds(int index, T[] array, String msg, Object...args) {
    isTrue(index >= 0 && index < array.length, msg, args);
  }
  
  public static <T> void bounds(int index, Collection<T> collection) {
    isTrue(index >= 0 && index < collection.size(), "Invalid index: %s. Got %s items", index, collection.size());
  }
  
  public static <T> void bounds(int index, Collection<T> collection, String msg, Object...args) {
    isTrue(index >= 0 && index < collection.size(), msg, args);
  }
  
  public static void isTrue(boolean condition, String msg) throws IllegalArgumentException {
    if (!condition) {
      throw new IllegalArgumentException(msg);
    }
  }
  
  public static void isTrue(boolean condition, String msg, Object...args) throws IllegalArgumentException {
    if (!condition) {
      throw new IllegalArgumentException(String.format(msg, args));
    }
  }
  
  public static void isFalse(boolean condition, String msg) throws IllegalArgumentException {
    isTrue(!condition, msg);
  }
  
  public static void isFalse(boolean condition, String msg, Object...args) throws IllegalArgumentException {
    isTrue(!condition, msg, args);
  }
  
  public static void illegalState(boolean condition, String msg) throws IllegalStateException {
    if (condition) {
      throw new IllegalStateException(msg);
    }
  }
  
  public static void illegalState(boolean condition, String msg, Object...args) throws IllegalStateException {
    if (condition) {
      throw new IllegalStateException(String.format(msg, args));
    }
  }
}
