package org.sapia.regis.loader;

public class CustomPropertyTag extends PropertyTag {
  
  public static CustomPropertyTag create(String aName, String aValue) {
    CustomPropertyTag tag = new CustomPropertyTag();
    tag.setName(aName);
    tag.setValue(aValue);
    return tag;
  }
  
  public void setText(String text) {
    setValue(text);
  }
}
