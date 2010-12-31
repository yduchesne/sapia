package org.sapia.util.xml.confix.test;

public class EnumValue {

  public enum TestType {
    VOID,
    NOT_AWARE,
    SEEDLESS;
  }
  
  private TestType _type;
  private String _description;
  
  /**
   * Creates a new {@link EnumValue} instance.
   */
  public EnumValue() {
  }

  /**
   * Returns the type attribute.
   *
   * @return The type value.
   */
  public TestType getType() {
    return _type;
  }

  /**
   * Changes the value of the attributes type.
   *
   * @param aType The new value of the type attribute.
   */
  public void setType(TestType aType) {
    _type = aType;
  }

  /**
   * Returns the description attribute.
   *
   * @return The description value.
   */
  public String getDescription() {
    return _description;
  }

  /**
   * Changes the value of the attributes description.
   *
   * @param aDescription The new value of the description attribute.
   */
  public void setDescription(String aDescription) {
    _description = aDescription;
  }
  
}
