package org.sapia.soto.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.sapia.util.text.TemplateContextIF;

/**
 * An instance of this class is meant to encapsulate a list of <code>Properties</code>
 * objects that it searches for desired values (according to passed in property names).
 * @author Yanick Duchesne
 */
public class PropertiesContext implements TemplateContextIF{

  private TemplateContextIF _parent;
  private boolean _parentFirst;
  private List _props = new ArrayList();
 
  /**
   * @param parent a parent <code>TemplateContextIF</code> - will be ignored
   *  if <code>null</code>.
   * @param parentFirst if <code>true</code>, searches the parent prior through
   * searching the internal properties.
   */
  public PropertiesContext(TemplateContextIF parent, boolean parentFirst){
    _parent = parent;
    _parentFirst = parentFirst;
  }
  
  /**
   * This method searches through this instance's encapsulated list of
   * <code>Properties</code>, starting to from the end of the list.
   * 
   * @return the <code>Object</code> corresponding to the given name, or
   * <code>null</code> if no such object is found.
   * 
   * @see #addProperties(Properties)
   */
  public Object getValue(String name) {

    Object val = null;
    if(_parentFirst && _parent != null){
      val = _parent.getValue(name);
    }
    if(val == null){
      for(int i = _props.size() - 1; i >= 0; i--){
        Properties prop = (Properties)_props.get(i);
        val = prop.getProperty(name);
        if(val != null) break;
      } 
    }
    if(val == null && !_parentFirst && _parent != null){
      val = _parent.getValue(name);
    }
    return val;
  }
  
  /**
   * Empty implementation.
   */
  public void put(String name, Object value) {}
  
  /**
   * @param props a <code>Properties</code> instance that is added to the
   *  end of the list encapsulated by this instance.
   */
  public void addProperties(Properties props){
    _props.add(props);
  }
  
  public String toString(){
    return _props.toString();
  }
}
