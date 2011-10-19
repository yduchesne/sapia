/*
 * Property.java
 *
 * Created on December 3, 2005, 3:46 PM
 */

package org.sapia.soto.jython.bean;

import org.python.core.Py;
import org.python.core.PyObject;

/**
 * An instance of this class holds information pertaining to invoking setter,
 * getter and adder methods on <code>PyObject</code> instances.
 *
 * @author yduchesne
 */
public class Property {
  
  private String _name;
  private String _set, _add;
  private String _get;
  
  /** Creates a new instance of Property */
  public Property(String name) {
    _name = name;
    _set = ("set"+capitalize(name)).intern();
    _add = ("add"+capitalize(name)).intern();
    _get = ("get"+capitalize(name)).intern();
  }
  
  /**
   * @return the name of this property.
   */
  public String getName(){
    return _name;
  }
  
  /** 
   * Invokes the getter corresponding to this property on the given instance.
   * 
   * @param py a <code>PyObject</code> on which to call the getter.
   * @return the <code>PyObject</code> instance results from the getter invocation.
   */
  public PyObject get(PyObject py){
    return py.invoke(_get);
  }

  /** 
   * Invokes the setter corresponding to this property on the given instance.
   * 
   * @param py a <code>PyObject</code> on which to call the setter.
   * @param val an <code>Object<code> to pass to the setter.
   */
  public void set(PyObject py, Object val){
    py.invoke(_set, convert(val));
  }
  
  /** 
   * Invokes the adder corresponding to this property on the given instance.
   * 
   * @param py a <code>PyObject</code> on which to call the adder.
   * @param val an <code>Object<code> to pass to the adder.
   */  
  public void add(PyObject py, Object val){
    py.invoke(_add, convert(val));
  }  
  
  public String toString(){
    return "[name=" + _name + "]";
  }
  
  private String capitalize(String name){
    StringBuffer newName = new StringBuffer();
    newName.append(Character.toUpperCase(name.charAt(0)));
    if(name.length() > 1){
      newName.append(name.substring(1));
    }
    return newName.toString();
  }  
  
  private PyObject convert(Object o){
    if(o instanceof PyObject){
      return (PyObject)o;
    }
    return Py.java2py(o);
  }
}
