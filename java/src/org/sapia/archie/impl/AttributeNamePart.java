package org.sapia.archie.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.sapia.archie.NamePart;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class AttributeNamePart implements NamePart{
  
  static final String EMPTY_NAME = "";
  
  private String _name = EMPTY_NAME;
  
  private Properties _attributes = new Properties();
  
  void setName(String name){
    _name = name;
  }
  
  void addProperty(String name, String val){
     _attributes.setProperty(name, val);
  }
  
  /**
   * @return this instance's name (without the attributes).
   */
  public String getName(){
    return _name;
  }
  
  /**
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    return _name.hashCode();
  }
  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object o){
    try{
      return _name.equals(((AttributeNamePart)o).getName());
    }catch(ClassCastException e){
      return false;
    }
  }
  
  /**
   * Returns <code>true</code> if this instance's name is equal to the name
   * of the instance passed in, AND if the passed in instance's attributes are
   * also contained by this instance.
   *  
   * @param other the <code>AttributeNamePart</code> used to perform the test.
   * @return <code>true</code> if this instance "matches" the passed in instance.
   */
  public boolean matches(AttributeNamePart other){
    Map.Entry entry;
    Iterator entries = other._attributes.entrySet().iterator();
    String value;
    if(!other.getName().equals(_name)){
      return false;
    }
    while(entries.hasNext()){
      entry = (Map.Entry)entries.next();
      value = _attributes.getProperty(entry.getKey().toString());
      if(value == null && entry.getValue() == null){
        continue;
      }
      else if(value == null && entry.getValue() != null){
        return false;
      }
      else if(value != null && entry.getValue() == null){
        return false;
      }
      else if(value != null && entry.getValue() != null && 
              entry.getValue().toString().equals(value)){
        continue;
      }
      else{
        return false;
      }
    }
    return true;
  }
  
  /**
   * @return this instance's attributes.
   */
  public Properties getAttributes(){
    return _attributes;
  }
  
  /**
   * @see org.sapia.archie.NamePart#asString()
   */
  public String asString() {
    StringBuffer buf = new StringBuffer(_name);
    if (_attributes.size() > 0) {
      Map.Entry    entry;
      buf.append(AttributeNameParser.QMARK);

      Iterator itr   = _attributes.entrySet().iterator();
      int      count = 0;

      while (itr.hasNext()) {
        entry = (Map.Entry) itr.next();

        if (count > 0) {
          buf.append(AttributeNameParser.AMP);
        }

        buf.append(entry.getKey().toString()).append(AttributeNameParser.EQ)
           .append(entry.getValue());
        count++;
      }
    }
    return buf.toString();    
  }
  
  public String toString(){
    return asString();
  }
  
}
