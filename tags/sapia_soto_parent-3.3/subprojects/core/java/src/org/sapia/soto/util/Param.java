package org.sapia.soto.util;

/**
 * Models a name-value pair.
 * 
 * @author Yanick Duchesne
 *         <dl>
 *         <dt><b>Copyright: </b>
 *         <dd>Copyright &#169; 2002-2003 <a
 *         href="http://www.sapia-oss.org">Sapia Open Source Software </a>. All
 *         Rights Reserved.</dd>
 *         </dt>
 *         <dt><b>License: </b>
 *         <dd>Read the license.txt file of the jar or visit the <a
 *         href="http://www.sapia-oss.org/license.html">license page </a> at the
 *         Sapia OSS web site</dd>
 *         </dt>
 *         </dl>
 */
public class Param {
  private String _name;
  private Object _value;

  /**
   * Constructor for Param.
   */
  public Param() {
    super();
  }

  /**
   * Sets this instance's name.
   * 
   * @param name a name.
   */
  public void setName(String name) {
    _name = name;
  }

  /**
   * Returns this instance's name.
   * 
   * @return a name.
   */
  public String getName() {
    return _name;
  }

  /**
   * Sets this instance's value.
   * 
   * @param value a value.
   */
  public void setValue(Object value) {
    _value = value;
  }

  /**
   * Returns this instance's value.
   * 
   * @return value.
   */
  public Object getValue() {
    return _value;
  }
}
