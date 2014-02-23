package org.sapia.dataset.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * Holds utility methods related to behavior around the {@link Object} class.
 * 
 * @author yduchesne
 *
 */
public class Objects {

  public static final int PRIME = 31;

  private Objects() {
  }

  /**
   * Performs equality check, safely handling <code>null</code>s.
   * 
   * @param o1 the first object to compare.
   * @param o2 the second object to compare.
   * @return <code>true</code> if both objects are equal.
   */
  public static boolean safeEquals(Object o1, Object o2) {
    if (o1 == null && o2 == null) {
      return true;
    }
    if (o1 == null || o2 == null) {
      return false;
    }
    return o1.equals(o2);
  }
  
  /**
   * Performs invocation of the {@link Object#hashCode()} method on the given
   * object, safely handling <code>null</code>s.
   * 
   * @param obj an {@link Object} whose hash code is to be returned.
   * @return the hash code that was obtained, or 0 if the given object is <code>null</code>.
   */
  public static int safeHashCode(Object obj) {
    return obj == null ? 0 : obj.hashCode();
  }
  
  /**
   * Computes a "compounded" hash code, for a whole array of objects.
   * Safely handles <code>null</code>s.
   * 
   * @param obj one or more object for which to compute a hash code.
   * @return the hash code that was computed.
   */
  public static int safeHashCode(Object...obj) {
    int hashCode = 0;
    if (obj == null) {
      return hashCode;
    }
    for (Object o : obj) {
      hashCode += safeHashCode(o) * PRIME;
    }
    return hashCode;
  }
  
  /**
   * Computes a "compounded" hash code, for a whole collection of objects.
   * Safely handles <code>null</code>s.
   * 
   * @param obj one or more object for which to compute a hash code.
   * @return the hash code that was computed.
   */
  public static int safeHashCode(Collection<?> items) {
    int hashCode = 0;
    if (items == null) {
      return hashCode;
    }
    for (Object i : items) {
      hashCode += safeHashCode(i) * PRIME;
    }
    return hashCode;
  }
  
  /**
   * Computes a "compounded" hash code, for a whole iterator of objects.
   * Safely handles <code>null</code>s.
   * 
   * @param obj one or more object for which to compute a hash code.
   * @return the hash code that was computed.
   */
  public static int safeHashCode(Iterator<?> items) {
    int hashCode = 0;
    if (items == null) {
      return hashCode;
    } 
    while (items.hasNext()) {
      Object i = items.next();
      hashCode += safeHashCode(i) * PRIME;
    }
    return hashCode;
  }

}
