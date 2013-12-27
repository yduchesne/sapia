package org.sapia.soto.util;

/**
 * This class holds name-to-class definitions that are mapped to a given
 * namespace.
 * 
 * @see org.sapia.soto.util.Namespace
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
public class Def {
  
  public static final String ANY = "*";
  
  private String _class;
  private String _name;
  
  /**
   * Constructor for Def.
   */
  public Def() {
  }

  /**
   * Sets name of the class to which this definition corresponds.
   * 
   * @param clazz
   *          a class name.
   */
  public void setClass(String clazz) {
    _class = clazz;
  }

  /**
   * Sets the name to which this definition is associated.
   * 
   * @param name
   *          a name.
   */
  public void setName(String name) {
    _name = name;
  }

  /**
   * Returns the name associated to this definition.
   * 
   * @return a name.
   */
  public String getName() {
    return _name;
  }

  /**
   * Returns the name of the class to which this definition corresponds.
   * 
   * @return a class name.
   */
  public String getClazz() {
    return _class;
  }
  
  public boolean matches(String name){
    if(_name.equals(ANY)){
      return true;
    }
    else{
      return _name.equals(name);
    }
  }
}
