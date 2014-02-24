package org.sapia.dataset.value;

/**
 * A utility class to work with <code>null</code>s.
 * 
 * @author yduchesne
 *
 */
public final class NullValue implements Value {
  
  private static final NullValue INSTANCE = new NullValue();

  private NullValue() {
  }
  
  @Override
  public double get() {
    return 0;
  }
  
  @Override
  public boolean equals(Object obj) {
    return obj == null || obj instanceof NullValue;
  }
  
  @Override
  public int hashCode() {
    return 0;
  }
  
  /**
   * @param obj an object to test for nullity.
   * @return <code>true</code> if the given object is considered <code>null</code>.
   */
  public static boolean isNull(Object obj) {
    return obj == null || obj instanceof NullValue;
  }
  
  /**
   * @param obj an object to test for nullity.
   * @return <code>true</code> if the given object is not considered <code>null</code>.
   */
  public static boolean isNotNull(Object obj) {
    return !isNull(obj);
  }
  
  /**
   * @return a {@link NullValue}.
   */
  public static NullValue getInstance() {
    return INSTANCE;
  }
  
  @Override
  public String toString() {
    return "?";
  }
}
