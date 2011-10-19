/*
 * Form.java
 *
 * Created on September 2, 2005, 5:16 PM
 *
 */

package org.sapia.soto.state.form;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author yduchesne
 */
public class Form {
  
  private int _id;
  private Object _obj;
  private Map _data = new HashMap();
  
  /**
   * @param the form's ID.
   */
  public Form(int id) {
    _id = id;
  }
  
  /**
   * This constructor creates a form with an ID if 0. Use
   * this constructor to instantiate a form as a normal bean, without
   * using the <code>FormStack</code>.
   *
   * @see FormStack
   */
  public Form() {
  }
  
  
  /**
   * @return the identifier of this form.
   */
  public int getId(){
    return _id;
  }
  
  /**
   * @param obj this form's <code>Object</code> (an arbitrary bean meant
   * to hold form data).
   */
  public void setObject(Object obj){
    _obj = obj;
  }
  
  /**
   * @return this form's <code>Object</code>.
   */
  public Object getObject(){
    return _obj;
  }
  
  /**
   * @param key the key under which to bind the given object.
   * @param val an <code>Object</code>.
   */
  public void put(String key, Object val){
    _data.put(key, val);
  }
  
  /**
   * This method has been added to allow interaction with the
   * <a href="http://jakarta.apache.org/commons/beanutils/api/index.html">BeanUtils</a> library.
   * <p>
   * A value can be set on this instance using the mapped property notation:
   * <pre>
   *   PropertyUtils.setMappedProperty(formInstance, value("someKey"), someValue);
   * </pre>
   *
   * @see put(String, Object)
   */
  public void setValue(String key, Object val){
    put(key, val);
  }
  
  /**
   * @param key the key for which the corresponding object should be returned.
   * @return the <code>Object</code> whose key corresponds to the given
   * one, or <code>null</code> if no such object exists.
   */
  public Object get(String key){
    return _data.get(key);
  }  
  
  /**
   * This method has been added to allow interaction with the
   * <a href="http://jakarta.apache.org/commons/beanutils/api/index.html">BeanUtils</a> library.
   * <p>
   * A value can acquired from this instance using the mapped property notation:
   * <pre>
   *   value = PropertyUtils.getMappedProperty(formInstance, value("someKey"));
   * </pre>
   *
   * @see put(String, Object)
   */
  public Object getValue(String key){
    return get(key);
  }  
  
  public String toString(){
    return new StringBuffer("[")
    .append("id=").append(_id)
    .append(", object=").append(_obj)
    .append(", data=").append(_data)
    .append("]")
    .toString();
  }
}
