package org.sapia.archie.jndi;

import java.util.Enumeration;
import java.util.Vector;

import javax.naming.InvalidNameException;
import javax.naming.Name;

import org.sapia.archie.NamePart;
import org.sapia.archie.impl.DefaultNamePart;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class JndiName implements Name{
  
  private org.sapia.archie.Name _name;
  
  public JndiName(){
    _name = new org.sapia.archie.Name();
  }
  public JndiName(org.sapia.archie.Name name){
    _name = name;
  }
  
  /**
   * @see javax.naming.Name#add(int, java.lang.String)
   */
  public Name add(int posn, String comp) throws InvalidNameException {
    _name.addAt(posn, new DefaultNamePart(comp));
    return this;
  }

  /**
   * @see javax.naming.Name#add(java.lang.String)
   */
  public Name add(String comp) throws InvalidNameException {
    _name.add(new DefaultNamePart(comp));
    return this;
  }

  /**
   * @see javax.naming.Name#addAll(int, javax.naming.Name)
   */
  public Name addAll(int posn, Name n) throws InvalidNameException {
    
    for(int i = 0; i < n.size(); i++){
      _name.addAt(posn++, new DefaultNamePart(n.get(i)));
    }
    return this;
  }

  /**
   * @see javax.naming.Name#addAll(javax.naming.Name)
   */
  public Name addAll(Name suffix) throws InvalidNameException {
    for(int i = 0; i < suffix.size(); i++){
      _name.add(new DefaultNamePart(suffix.get(i)));
    }
    return this;
  }

  /**
   * @see java.lang.Object#clone()
   */
  public Object clone() {
    return new JndiName(_name);
  }

  /**
   * @see javax.naming.Name#compareTo(java.lang.Object)
   */
  public int compareTo(Object obj) {
    return 0;
  }

  /**
   * @see javax.naming.Name#endsWith(javax.naming.Name)
   */
  public boolean endsWith(Name n) {
    return _name.endsWith(((JndiName)n)._name);
  }

  /**
   * @see javax.naming.Name#get(int)
   */
  public String get(int posn) {
    return ((NamePart)_name.get(posn)).asString();
  }
  
  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj) {
    try{
      return _name.equals(((JndiName)obj)._name);
    }catch(ClassCastException e){
      return false;
    }
  }


  /**
   * @see javax.naming.Name#getAll()
   */
  public Enumeration getAll() {
    Vector v = new Vector();
    for(int i = 0; i < _name.count(); i++){
      v.add(((NamePart)_name.get(i)).asString());
    }
    return v.elements();
  }

  /**
   * @see javax.naming.Name#getPrefix(int)
   */
  public Name getPrefix(int posn) {
    return new JndiName(_name.getTo(posn));
  }

  /**
   * @see javax.naming.Name#getSuffix(int)
   */
  public Name getSuffix(int posn) {
    return new JndiName(_name.getFrom(posn));
  }

  /**
   * @see javax.naming.Name#isEmpty()
   */
  public boolean isEmpty() {
    return _name.count() == 0;
  }

  /**
   * @see javax.naming.Name#remove(int)
   */
  public Object remove(int posn) throws InvalidNameException {
    return _name.removeAt(posn).asString();
  }

  /**
   * @see javax.naming.Name#size()
   */
  public int size() {
    return _name.count();
  }

  /**
   * @see javax.naming.Name#startsWith(javax.naming.Name)
   */
  public boolean startsWith(Name n) {
    return _name.startsWith(((JndiName)n)._name);
  }
  
  public String toString() {
    if (_name == null) {
      return "null";
    } else {
      StringBuffer buff = new StringBuffer();
      for(int i = 0; i < _name.count(); i++){
        buff.append(_name.get(i).asString());
        if(i < _name.count() - 1){
          buff.append('/');
        }
      }
      return buff.toString();
    }
  }
      
}
