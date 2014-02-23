package org.sapia.dataset.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sapia.dataset.conf.Conf;
import org.sapia.dataset.func.ArgFunction;

/**
 * An instance of this class holds {@link Setting} instances.
 * 
 * @author yduchesne
 *
 */
public class Settings {
  
  /**
   * An instance of this class defines a named parameters, indicating the type
   * of that parameters, and wether it is mandatory or not.
   */
  public static class Setting  {
    
    private Settings owner;
    private String   name, description;
    private Class<?> type;
    private boolean  mandatory;
    
    public Setting(Settings owner) {
      this.owner = owner;
    }
    
    public String getDescription() {
      return description;
    }
    
    public Setting description(String desc) {
      this.description = desc;
      return this;
    }
    
    public String getName() {
      return name;
    }
    
    public Setting name(String name) {
      this.name = name;
      return this;
    }
    
    public Class<?> getType() {
      return type;
    }
    
    public Setting type(Class<?> type) {
      this.type = type;
      return this;
    }
    
    public boolean isMandatory() {
      return mandatory;
    }
    
    public Setting mandatory() {
      mandatory = true;
      return this;
    }

    /**
     * Adds this instance to its {@link Settings} and returns
     * a new {@link Setting} instance, to be populated by the caller.
     * 
     * @return a new {@link Setting}.
     */
    public Setting setting() {
      finish();
      return new Setting(owner);
    }
    
    /**
     * Adds this instance to its {@link Settings}.
     * 
     * @return this instance's {@link Settings}.
     */
    public Settings finish() {
      Checks.notNull(name,        "Setting name not set");
      Checks.notNull(description, "Setting description not set");
      Checks.notNull(type,        "Setting type not set");
      owner.declarations.put(name, this);
      owner.orderedSettings.add(this);
      owner.initialized = true;
      return owner;
    }
    
    /**
     * @param values a {@link Map} of values.
     * @param expected the {@link Class} corresponding to the type of value that's expected.
     * @return the value corresponding to this instance's name, or <code>null</code> if
     * none is found, and this instance's <code>mandatory</code> flag is set to <code>false</code>.
     * @throws IllegalArgumentException if this instance's <code>mandatory</code> flag is set to <code>true</code>
     * and no value is found.
     */
    @SuppressWarnings("unchecked")
    public <V> V get(Map<String, Object> values, Class<V> expected) {
      Object value = values.get(name);
      if (value == null) {
        Checks.isFalse(mandatory, "No setting value found: %s", name);
        return null;
      } else {
        return (V) value;
      }    
    }
    
    /**
     * @param values a {@link Map} of values.
     * @param defaultVal the default value to return if none is found in the 
     * given map.
     * @param expected the {@link Class} corresponding to the type of value that's expected.
     * @return the value corresponding to this instance's name, or the given default
     * value if none is found and this instance's <code>mandatory</code> flag is set to <code>false</code>.
     * @throws IllegalArgumentException if this instance's <code>mandatory</code> flag is set to <code>true</code>
     * and no value is found.
     */
    @SuppressWarnings("unchecked")
    public <V> V get(Map<String, Object> values, V defaultVal, Class<V> expected) {
      Object value = values.get(name);
      if (value == null) {
        return defaultVal;
      } else {
        return (V) value;
      }    
    }

    /**
     * @param values the {@link Map} of values holding setting values.
     * @param enumClass the enum {@link Class} corresponding to the type of enum
     * that is expected.
     * @return the enum value corresponding to this setting.
     * @throws IllegalArgumentException if no enum value is found for this instance.
     */
    public <V extends Enum<?>> V getConstant(Map<String, Object> values, Class<V> enumClass) {
      Object value = values.get(name);
      if (value == null) {
        Checks.isFalse(mandatory, "No setting value found: %s", name);
        return null;
      } else {
        if (value instanceof String) {
          String constantName = ((String) value).trim().toUpperCase();
          for (V enumValue : enumClass.getEnumConstants()) {
            if (enumValue.name().equals(constantName)) {
              return enumValue;
            }
          }
          throw new IllegalArgumentException(
            String.format("Invalid value for: %s. Got %; should be one of: %s",
              name,
              ((String) value).trim(),
              Strings.toString(enumClass.getEnumConstants(), new ArgFunction<Enum<?>, String>() {
                @Override
                public String call(Enum<?> arg) {
                  return arg.name().toLowerCase();
                }
              })
            )
          );
        }
        throw new IllegalArgumentException(
          "Expected string value for: " + name + ". Should be one of: " + 
          Strings.toString(enumClass.getEnumConstants(), new ArgFunction<Enum<?>, String>() {
            @Override
            public String call(Enum<?> arg) {
              return arg.name().toLowerCase();
            }
          })
        );
      }    
    }

    /**
     * @param values the {@link Map} of values holding setting values.
     * @param defaultVal the default enum value if none is found.
     * @param enumClass the enum {@link Class} corresponding to the type of enum
     * that is expected.
     * @return the enum value corresponding to this setting, or the default enum value
     * is none is found.
     */
    public <V extends Enum<?>> V getConstant(Map<String, Object> values, V defaultVal, Class<V> enumClass) {
      Object value = values.get(name);
      if (value == null) {
        Checks.isFalse(mandatory, "No setting value found: %s", name);
        return defaultVal;
      } else {
        if (value instanceof String) {
          String constantName = ((String) value).trim().toUpperCase();
          for (V enumValue : enumClass.getEnumConstants()) {
            if (enumValue.name().equals(constantName)) {
              return enumValue;
            }
          }
          throw new IllegalArgumentException(
            String.format("Invalid value for: %s. Got %; should be one of: %s",
              name,
              ((String) value).trim(),
              Strings.toString(enumClass.getEnumConstants(), new ArgFunction<Enum<?>, String>() {
                @Override
                public String call(Enum<?> arg) {
                  return arg.name().toLowerCase();
                }
              })
            )
          );
        }
        throw new IllegalArgumentException(
          "Expected string value for: " + name + ". Should be one of: " + 
          Strings.toString(enumClass.getEnumConstants(), new ArgFunction<Enum<?>, String>() {
            @Override
            public String call(Enum<?> arg) {
              return arg.name().toLowerCase();
            }
          })
        );
      }    
    }

    
    /**
     * @param values a {@link Map} of values.
     * @param defaultVal the default value to return if none is found in the 
     * given map.
     * @param expected the {@link Class} corresponding to the type of value that's expected.
     * @return the value corresponding to this instance's name, or the given default
     * value if none is found and this instance's <code>mandatory</code> flag is set to <code>false</code>.
     * @throws IllegalArgumentException if this instance's <code>mandatory</code> flag is set to <code>true</code>
     * and no value is found.
     */
    @SuppressWarnings("unchecked")
    public <V> V[] getMulti(Map<String, Object> values, V[] defaultVal, Class<V[]> expected) {
      Object value = values.get(name);
      if (value == null) {
        Checks.isFalse(mandatory, "No setting value found: %s", name);
        return defaultVal;
      } else {
        return (V[]) value;
      }    
    }
    
    public String toString() {
      return Strings.toString(
          "name", name, 
          "type", type.isArray() ? "" + type.getComponentType() + "[]" : type, 
          "mandatory", mandatory, 
          "desc", description);
    }
    
  }
 
  // --------------------------------------------------------------------------

  private Settings             parent;
  private boolean              initialized;
  private Map<String, Setting> declarations    = new HashMap<>();
  private List<Setting>        orderedSettings = new ArrayList<>();
  
  public Settings() {
  }
  
  /**
   * @param parent the parent {@link Settings} to use.
   */
  public Settings(Settings parent) {
    this.parent = parent;
  }
  
  /**
   * @return a new {@link Setting} that is meant to be configured by the caller.
   */
  public Setting setting() {
    if (initialized) {
      throw new IllegalStateException("This instance has been fully initialized: no more settings can be declared");
    }
    return new Setting(this);
  }

  /**
   * @param name the desired {@link Setting}'s name.
   * @return the {@link Setting} corresponding to the given name.
   * @throws IllegalArgumentException if no {@link Setting} is found for the given name.
   */
  public Setting get(String name) throws IllegalArgumentException {
    Setting s = declarations.get(name);
    if (s == null && parent != null) {
      s = parent.declarations.get(name);
    }
    return Checks.notNull(s, "No setting found for: %s", name);
  }
  
  /**
   * @param name a setting name.
   * @return <code>true</code> if the setting with the given name exists.
   */
  public boolean exists(String name) {
    return declarations.containsKey(name) || (parent != null && parent.declarations.containsKey(name));
  }
  
  /**
   * Helper method for creating {@link SettingValues} - internally calls {@link SettingValues#valueOf(Object...)}.
   * 
   * @param values the values to use.
   * @return the {@link SettingValues} corresponding to the given values.
   */
  public static SettingValues settings(Object...values) {
    return SettingValues.valueOf(values);
  }
  
  /**
   * Helper method for creating {@link SettingValues} - internally calls {@link SettingValues#valueOf(List)}.
   * 
   * @param values the values to use.
   * @return the {@link SettingValues} corresponding to the given values.
   */
  public static SettingValues settings(List<? extends Object> values) {
    return SettingValues.valueOf(values);
  }
  
  /**
   * @return a new instance of this class.
   */
  public static Settings obj() {
    return new Settings();
  }
  
  /**
   * @param parent the parent settings.
   * @return a new instance of this class.
   */
  public static Settings obj(Settings parent) {
    return new Settings(parent);
  }
  
  @Override
  public String toString() {
    return toString(0);
  }
  
  public String toString(int indent) {
    StringBuilder builder = new StringBuilder();
    builder
      .append(Strings.repeat(" ", indent))
      .append(
          String.format(
              "%-20s  %-25s  %-8s  ", 
              "name", "type", "required" 
          )
      )
      .append("description")
      .append(System.lineSeparator())
      .append(Strings.repeat(" ", indent))
      .append(Strings.repeat("-", Conf.getDisplayWidth() - indent))
      .append(System.lineSeparator());

    List<Setting> theSettings = new ArrayList<>();
    if (parent != null) {
      theSettings.addAll(parent.orderedSettings);
    }
    theSettings.addAll(orderedSettings);
        
    for (Setting s : theSettings) {
      builder
        .append(Strings.repeat(" ", indent))
        .append(
            String.format(
                "%-20s  %-25s  %-8s  ", 
                s.name, s.type.isArray() ? "" + s.type.getComponentType().getName() + "[]" : s.type.getName(), 
                s.mandatory
            )
        )
        .append(s.description)
        .append(System.lineSeparator());
    }
    builder.append(System.lineSeparator());
    return builder.toString();
  }
}
