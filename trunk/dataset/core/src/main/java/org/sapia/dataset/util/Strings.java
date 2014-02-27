package org.sapia.dataset.util;

import java.util.Collection;

import org.sapia.dataset.func.ArgFunction;

/**
 * Holds various string-related utility methods.
 * 
 * @author yduchesne
 *
 */
public class Strings {
  
  public static final String BLANK = "";
  
  private Strings() {
  }
  
  /**
   * @param array an array to convert to a {@link String}.
   * @param func the {@link ArgFunction} that acts as the converter for each item 
   * in the array.
   * @return the resulting {@link String}.
   */
  public static <F, T> String toString(T[] array, ArgFunction<T, String> func){
    StringBuilder s = new StringBuilder("[");
    int i = 0;
    for (T t : array) {
      if (i > 0) {
        s.append(", ");
      }
      s.append(func.call(t));
      i++;
    }
    return s.append("]").toString();
  }
  
  /**
   * @param objects the objects to concatenate as a string.
   * @return the {@link String} resulting from the concatenation.
   */
  public static String concat(Object...objects) {
    StringBuilder s = new StringBuilder();
    for (Object o : objects) {
      s.append(o == null ? "null" : o);
    }
    return s.toString();
  }
  
  /**
   * @param collection a {@link Collection} to convert to a {@link String}.
   * @param func the {@link ArgFunction} that acts as the converter for each item 
   * in the collection.
   * @return the resulting {@link String}.
   */
  public static <T> String toString(Collection<T> collection, ArgFunction<T, String> func) {
    StringBuilder s = new StringBuilder("[");
    int i = 0;
    for (T t : collection) {
      if (i > 0) {
        s.append(", ");
      }
      s.append(func.call(t));
      i++;
    }
    return s.append("]").toString();
  }

  /**
   * Allows building a string representation for a given array of arguments
   * consisting of name/value pairs, of the following form:
   * 
   * <code>name-1,value1,name2,value2[...,nameN,valueN]</code>
   * 
   * @param nameValuePairs an array of arguments consisting of name/value pairs.
   * @return the {@link String} that was built.
   */
  public static String toString(Object...nameValuePairs) {
    StringBuilder builder = new StringBuilder();
    builder.append("[");
    for (int i = 0; i < nameValuePairs.length; i+=2) {
      if (i > 0) {
        builder.append(", ");
      }
      if (i + 1 >= nameValuePairs.length) {
        throw new IllegalArgumentException("Must have even number of value for toString() arguments");
      }
      builder.append(nameValuePairs[i]).append("=");
      if (nameValuePairs[i + 1] != null) {
        builder.append(nameValuePairs[i + 1]);
      }  else {
        builder.append("null");
      }
    }
    return builder.append("]").toString();
  }
  
  /**
   * @param s a {@link String} to center.
   * @param totalLen the total length (in number of characters) available.
   * @return the centered {@link String}.
   */
  public static String center(String s, int totalLen) {
    if (s.length() >= totalLen) {
      return s;
    }
    
    int pad = (totalLen - s.length()) / 2;
    
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < pad; i++) {
      sb.append(' ');      
    }
    sb.append(s);
    pad = totalLen - s.length() - pad;
    for (int i = 0; i < pad; i++) {
      sb.append(' ');      
    }
    return sb.toString();
  }
  
  /**
   * @param pattern a {@link String} pattern to repeat.
   * @param repetitions the number of times to repeat the given pattern.
   * @return a {@link String} holding the given pattern, in the given amount
   * of repetitions.
   */
  public static String repeat(String pattern, int repetitions) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < repetitions; i++) {
      builder.append(pattern);
    }
    return builder.toString();
  }
  
  /**
   * Pads a string to the right with another string.
   * 
   * @param toPad a {@link String} to pad.
   * @param pad the padding {@link String} to use.
   * @param maxLen the maximum length that the resulting string should have.
   * @return the padded {@link String}.
   */
  public static String rpad(String toPad, String pad, int maxLen) {
    StringBuilder builder = new StringBuilder();
    builder.append(toPad);
    while (builder.length() < maxLen) {
      builder.append(pad);
    }
    if (builder.length() > maxLen) {
      builder.delete(maxLen, builder.length());
    }
    return builder.toString();
  }
  
  /**
   * Pads a string to the left with another string.
   * 
   * @param toPad a {@link String} to pad.
   * @param pad the padding {@link String} to use.
   * @param maxLen the maximum length that the resulting string should have.
   * @return the padded {@link String}.
   */
  public static String lpad(String toPad, String pad, int maxLen) {
    StringBuilder builder = new StringBuilder();
    while (builder.length() < maxLen - toPad.length()) {
      builder.append(pad);
    }
    builder.append(toPad);
    if (builder.length() > maxLen) {
      builder.delete(maxLen, builder.length());
    }
    return builder.toString();
  }
  
  /**
   * @param s a {@link String} to check.
   * @return <code>true</code> if the given {@link String} is <code>null</code> or has
   * length 0.
   */
  public static boolean isNullOrEmpty(String s) {
    return s == null || s.length() == 0;
  }

  /**
   * @param s a {@link String} whose first letter should be capitalized.
   * @return the {@link String} resulting from the capitalization.
   */
  public static String capitalizeFirst(String s) {
    StringBuilder toReturn = new StringBuilder(s.length());
    for (int i = 0; i < s.length(); i++) {
      if (i == 0) {
        toReturn.append(Character.toUpperCase(s.charAt(i)));
      } else {
        toReturn.append(s.charAt(i));
      }
    }
    return toReturn.toString();
  }
}
