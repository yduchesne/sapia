/*
 * PropertyPath.java
 *
 * Created on December 3, 2005, 3:50 PM
 */

package org.sapia.soto.jython.bean;

import java.util.ArrayList;
import java.util.List;

import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyObject;
import org.sapia.soto.util.Utils;

/**
 * This class models a "property path", in which nested access to bean properties
 * is represented as a dot-delimited path, such as in: <code>person.address.phoneNumber</code>.
 * <p>
 * An instance of this class is used to assign 
 *
 * @author yduchesne
 */
public class PropertyPath {
  
  
  private ArrayList _props;
  private boolean _lenient = true;
  
  /** Creates a new instance of PropertyPath */
  private PropertyPath(ArrayList props) {
    _props = props;
    _props.trimToSize();
  }
  
  /**
   * @param lenient if <code>true</code>, indicates to this instance that it
   * should not throw exceptions.
   */
  public void setLenient(boolean lenient){
    _lenient = lenient;
  }
  
  /**
   * @return the <code>List</code> of <code>Property</code> instances that this
   * instance holds.
   */
  public List getProperties(){
    return _props;
  }
  
  /**
   * @param instance a <code>PyObject</code> on which the given value should be
   * set.
   * 
   * @param value an <code>Object</code> to set on the given instance.
   */
  public void set(PyObject instance, Object value){
    if(_lenient){
      try{
        callMutable(instance, value, false);
      }catch(PyException e){}
    }
    else{
      callMutable(instance, value, false);
    }
  }
  
  /**
   * @param instance a <code>PyObject</code> to which the given value should be
   * added.
   * 
   * @param value an <code>Object</code> to add to the given instance.
   */  
  public void add(PyObject instance, Object value){
    if(_lenient){
      try{
        callMutable(instance, value, true);
      }catch(PyException e){}
    }
    else{
      callMutable(instance, value, true);
    }
  }  

  /**
   * @param instance the <code>PyObject</code> whose getter should be called.
   */
  public PyObject get(PyObject instance){
    for(int i = 0; i < _props.size(); i++){
      Property prop = (Property)_props.get(i);
      if(!_lenient){
        instance = prop.get(instance);
      }
      else{
        try{
          instance = prop.get(instance);
        }catch(RuntimeException e){
          return null;
        }
      }
      if(instance == null && i < _props.size() - 1){
        if(_lenient) {
          return null;
        }
        throw new NullPointerException("No instance for property: " + prop.getName());
      }
    }
    return instance;
  }  
  
  /**
   * @param instance a <code>PyObject</code> on which the given value should be
   * set/added.
   * 
   * @param value an <code>Object</code> to set on the given instance.
   */
  public void setOrAdd(PyObject instance, Object value){
    try{
      callMutable(instance, value, false);
    }catch(PyException e){
      if(_lenient){
        try{
          callMutable(instance, value, true);
        }catch(PyException e2){
        }
      }
      else{
        callMutable(instance, value, true);
      }
    }
  }  
  
  /**
   * Parses a nested property path and returns its object representation.
   *
   * @param path the nested property path to parse.
   * @return a <code>PropertyPath</code>.
   */
  public static PropertyPath parse(String path){
    if(path == null){
      throw new IllegalArgumentException("Path cannot be null");
    }    
    String[] tokens = Utils.split(path, '.', true);
    if(tokens.length == 0){
      throw new IllegalArgumentException("No tokens in path: " + path);
    }
    ArrayList props = new ArrayList();
    for(int i = 0; i < tokens.length; i++){
      props.add(new Property(tokens[i]));
    }
    return new PropertyPath(props);
  }
  
  public String toString(){
    StringBuffer sb = new StringBuffer();
    for(int i = 0; i < _props.size(); i++){
      Property prop = (Property)_props.get(i);
      sb.append(prop.getName());
      if(i < _props.size() - 1){
        sb.append('.');
      }
    }
    return sb.toString();
  }
  
  private void callMutable(PyObject instance, Object val, boolean add){
    for(int i = 0; i < _props.size(); i++){
      Property prop = (Property)_props.get(i);
      if(i == _props.size() - 1){
        if(add){
          prop.add(instance, Py.java2py(val));
          return;
        }
        else{
          prop.set(instance, Py.java2py(val));
          return;
        }
      }
      else{
        instance = prop.get(instance);
        if(instance == null){
          if(!_lenient){
            throw new NullPointerException("No instance for property: " + prop.getName());
          }
          return;
        }
      }
    }

    if(!_lenient) throw new IllegalStateException("Could not find property: " + toString());
  }
  
}
