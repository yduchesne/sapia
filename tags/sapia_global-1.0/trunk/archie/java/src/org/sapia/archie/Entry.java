package org.sapia.archie;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class Entry {
  
  private String _name;
  private Object _value;
  
  public Entry(String name, Object value){
    _name  = name;
    _value = value;
  }
  
  /**
   * @return this entry's name.
   */
  public String getName(){
    return _name;
  }
  
  /**
   * @return this entry's value.
   */
  public Object getValue(){
    return _value;
  }
}
