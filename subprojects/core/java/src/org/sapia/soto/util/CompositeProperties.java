package org.sapia.soto.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * An instance of this class is meant to encapsulate a list of <code>Properties</code>
 * objects that it searchs for desired values (according to passed in property names).
 * @author Yanick Duchesne
 */
public class CompositeProperties extends Properties{

  private List _props = new ArrayList();
 
  /**
   * This method searches through this instance's encapsulated list of
   * <code>Properties</code>, starting to from the end of the list.
   * 
   * @return the property corresponding to the given name, or
   * <code>null</code> if no such object is found.
   * 
   * @see #addProperties(Properties)
   */
  public String getProperty(String name) {
    String val = null;
    for(int i = _props.size() - 1; i >= 0; i--){
      Properties prop = (Properties)_props.get(i);
      val = prop.getProperty(name);
      if(val != null) return val;
    } 
    return super.getProperty(name);
  }  
  /**
   * @see Properties#getProperty(java.lang.String, java.lang.String)
   */
  public String getProperty(String name, String deflt){
    String val = getProperty(name);
    if(val == null){
      val = deflt;
    }
    return val;
  }
 
  /**
   * @see Properties#propertyNames()
   */
  public Enumeration propertyNames(){
    Set names = new HashSet();
    fill(super.propertyNames(), names);
    for(int i = _props.size() - 1; i >= 0; i--){
      Properties prop = (Properties)_props.get(i);
      fill(prop.propertyNames(), names);
    }
    
    final Iterator itr = names.iterator();

    return new Enumeration(){
      public boolean hasMoreElements() {
        return itr.hasNext();
      }
      
      public Object nextElement() {
        return itr.next();
      }
    }; 
  }
  
  /**
   * @param props a <code>Properties</code> instance that is added to the
   *  end of the list encapsulated by this instance.
   */
  public void addProperties(Properties props){
    _props.add(props);
  }
  
  public synchronized String toString() {
    return super.toString();
  }
  
  private void fill(Enumeration names, Set tofill){
    while(names.hasMoreElements()){
      tofill.add(names.nextElement());
    }
  }
}
