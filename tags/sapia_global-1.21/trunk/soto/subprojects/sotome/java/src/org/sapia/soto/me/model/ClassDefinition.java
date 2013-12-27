package org.sapia.soto.me.model;

import javolution.text.TextBuilder;

import org.sapia.soto.me.ConfigurationException;

/**
 * This class implements the <code>def</code> tag for the SotoMe container.
 *
 * @author Jean-Cédric Desrochers
 */
public class ClassDefinition extends BaseModel {

  public static final String TAG_CLASS = "class";
  public static final String TAG_NAME  = "name";
  
  /** The class of this definition. */
  private String _class;
  
  /** The name associated with the class. */
  private String _name;

  /**
   * Creates a new ClassDefinition instance.
   */
  public ClassDefinition() {
  }
  
  /**
   * Returns the class name value.
   *
   * @return The class name value.
   */
  public String getClassName() {
    return _class;
  }

  /**
   * Changes the value of the class.
   *
   * @param aClassName The new class value.
   */
  public void setClassName(String aClassName) {
    _class = aClassName;
  }

  /**
   * Returns the name value.
   *
   * @return The name value.
   */
  public String getName() {
    return _name;
  }

  /**
   * Changes the value of the name.
   *
   * @param aName The new name value.
   */
  public void setName(String aName) {
    _name = aName;
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectHandler#handleObject(java.lang.String, java.lang.Object)
   */
  public void handleObject(String aName, Object anObject) throws ConfigurationException {
    if (TAG_CLASS.equals(aName)) {
      assertObjectType(String.class, aName, anObject);
      setClassName((String) anObject);

    } else if (TAG_NAME.equals(aName)) {
      assertObjectType(String.class, aName, anObject);
      setName((String) anObject);
      
    } else {
      throwUnrecognizedObject(aName, anObject);
    }
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    TextBuilder builder = new TextBuilder();
    builder.append("[name=").append(_name).
            append(" class=").append(_class).
            append("]");
    return builder.toString();
  }
}
