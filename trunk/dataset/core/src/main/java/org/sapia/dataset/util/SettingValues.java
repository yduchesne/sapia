package org.sapia.dataset.util;

import java.util.HashMap;
import java.util.List;

/**
 * Holds setting values.
 * 
 * @see Settings
 * 
 * @author yduchesne
 *
 */
public class SettingValues extends HashMap<String, Object> {
  
  private static final long serialVersionUID = 1L;

  /**
   * @param name the name of the setting to which the given value corresponds.
   * @param value a value.
   * @return this instance.
   */
  public SettingValues set(String name, Object value) {
    super.put(name, value);
    return this;
  }

  /**
   * Builds an instance of this class, given a list of the form:
   * <p>
   * <code>name1, value1, name2, value[,nameN,valueN...]</code>
   * 
   * @param nameValuePairs a list of name/value pairs.
   * @return the {@link SettingValues} created from the given name/value pairs.
   */
  public static SettingValues valueOf(List<? extends Object> nameValuePairs) {
    SettingValues values = new SettingValues();
    for (int i = 0; i < nameValuePairs.size(); i += 2) {
      String name = (String) nameValuePairs.get(i);
      Checks.isFalse(i + 1 >= nameValuePairs.size(), "No value available for %s", name);
      Object value = nameValuePairs.get(i + 1);
      values.set(name, value);
    }
    return values;

  }
  
  /**
   * Builds an instance of this class, given an array of the form:
   * <p>
   * <code>name1, value1, name2, value[,nameN,valueN...]</code>
   * 
   * @param nameValuePairs an array of name/value pairs.
   * @return the {@link SettingValues} created from the given name/value pairs.
   */
  public static SettingValues valueOf(Object...nameValuePairs) {
    SettingValues values = new SettingValues();
    for (int i = 0; i < nameValuePairs.length; i += 2) {
      String name = (String) nameValuePairs[i];
      Checks.isFalse(i + 1 >= nameValuePairs.length, "No value available for %s", name);
      Object value = nameValuePairs[i + 1];
      values.set(name, value);
    }
    return values;
  }
  
  /**
   * @return a new instance of this class.
   */
  public static SettingValues obj() {
    return new SettingValues();
  }

}
