package org.sapia.dataset;

import org.sapia.dataset.util.Strings;

/**
 * 
 * @author yduchesne
 *
 */
public class Nominal {

  private String name;
  private int    value;
  
  /**
   * @param name the name of this instance.
   * @param value the value of this instance.
   */
  public Nominal(String name, int value) {
    this.name  = name;
    this.value = value;
  }
  
  /**
   * @return this instance's name.
   */
  public String getName() {
    return name;
  }
  
  /**
   * @return this instance's value.
   */
  public int getValue() {
    return value;
  }
  
  @Override
  public String toString() {
    return Strings.concat("[", name, ":", value, "]");
  }
  
  @Override
  public int hashCode() {
    return value;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Nominal) {
      Nominal other = (Nominal) obj;
      return other.name.equals(name) && other.value == value;
    }
    return false;
  }
}
