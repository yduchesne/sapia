package org.sapia.soto.me.model;

import org.sapia.soto.me.ConfigurationException;
import org.sapia.soto.me.util.PropertyResolver;
import org.sapia.soto.me.xml.ObjectCreationCallback;

/**
 * This class implements the <code>soto:param</code> tag for the SotoMe container.
 *
 * @author Jean-Cédric Desrochers
 */
public class Parameter extends BaseModel implements ObjectCreationCallback {

  public static final String TAG_NAME = "name";
  public static final String TAG_VALUE = "value";
  
  private PropertyResolver _propResolver;
  
  /** The name of this parameter. */
  private String _name;

  /** The value of this parameter. */
  private String _value;

  /**
   * Creates a new Parameter instance.
   */
  public Parameter(PropertyResolver aPropResolver) {
    _propResolver = aPropResolver;
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

  /**
   * Returns the value value.
   *
   * @return The value value.
   */
  public String getValue() {
    return _value;
  }

  /**
   * Changes the value of the value.
   *
   * @param aValue The new value value.
   */
  public void setValue(String aValue) {
    _value = aValue;
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectHandler#handleObject(java.lang.String, java.lang.Object)
   */
  public void handleObject(String aName, Object anObject) throws ConfigurationException {
    if (TAG_NAME.equals(aName)) {
      assertObjectType(String.class, aName, anObject);
      setName((String) anObject);

    } else if (TAG_VALUE.equals(aName)) {
      assertObjectType(String.class, aName, anObject);
      setValue((String) anObject);

    } else {
      throwUnrecognizedObject(aName, anObject);
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    if (_name == null) {
      throw new ConfigurationException("The name attribute of this Parameter is null - verify the tag <soto:param>");
    }
    
    if (_value != null) {
      return this;
    } else {
      String value = _propResolver.getProperty(_name);
      if (value == null) {
        return "${"+_name+"}";
      } else {
        return value;
      }
    }
  }
}
