package org.sapia.soto;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class TestContext implements Context {

  /**
   * @see javax.naming.Context#addToEnvironment(java.lang.String,
   *      java.lang.Object)
   */
  public Object addToEnvironment(String propName, Object propVal)
      throws NamingException {
    //  Auto-generated method stub
    return null;
  }

  /**
   * @see javax.naming.Context#bind(javax.naming.Name, java.lang.Object)
   */
  public void bind(Name name, Object obj) throws NamingException {
    //  Auto-generated method stub

  }

  /**
   * @see javax.naming.Context#bind(java.lang.String, java.lang.Object)
   */
  public void bind(String name, Object obj) throws NamingException {
    //  Auto-generated method stub

  }

  /**
   * @see javax.naming.Context#close()
   */
  public void close() throws NamingException {
    //  Auto-generated method stub

  }

  /**
   * @see javax.naming.Context#composeName(javax.naming.Name, javax.naming.Name)
   */
  public Name composeName(Name name, Name prefix) throws NamingException {
    //  Auto-generated method stub
    return null;
  }

  /**
   * @see javax.naming.Context#composeName(java.lang.String, java.lang.String)
   */
  public String composeName(String name, String prefix) throws NamingException {
    //  Auto-generated method stub
    return null;
  }

  /**
   * @see javax.naming.Context#createSubcontext(javax.naming.Name)
   */
  public Context createSubcontext(Name name) throws NamingException {
    //  Auto-generated method stub
    return null;
  }

  /**
   * @see javax.naming.Context#createSubcontext(java.lang.String)
   */
  public Context createSubcontext(String name) throws NamingException {
    //  Auto-generated method stub
    return null;
  }

  /**
   * @see javax.naming.Context#destroySubcontext(javax.naming.Name)
   */
  public void destroySubcontext(Name name) throws NamingException {
    //  Auto-generated method stub

  }

  /**
   * @see javax.naming.Context#destroySubcontext(java.lang.String)
   */
  public void destroySubcontext(String name) throws NamingException {
    //  Auto-generated method stub

  }

  /**
   * @see javax.naming.Context#getEnvironment()
   */
  public Hashtable getEnvironment() throws NamingException {
    //  Auto-generated method stub
    return null;
  }

  /**
   * @see javax.naming.Context#getNameInNamespace()
   */
  public String getNameInNamespace() throws NamingException {
    //  Auto-generated method stub
    return null;
  }

  /**
   * @see javax.naming.Context#getNameParser(javax.naming.Name)
   */
  public NameParser getNameParser(Name name) throws NamingException {
    //  Auto-generated method stub
    return null;
  }

  /**
   * @see javax.naming.Context#getNameParser(java.lang.String)
   */
  public NameParser getNameParser(String name) throws NamingException {
    //  Auto-generated method stub
    return null;
  }

  /**
   * @see javax.naming.Context#list(javax.naming.Name)
   */
  public NamingEnumeration list(Name name) throws NamingException {
    //  Auto-generated method stub
    return null;
  }

  /**
   * @see javax.naming.Context#list(java.lang.String)
   */
  public NamingEnumeration list(String name) throws NamingException {
    //  Auto-generated method stub
    return null;
  }

  /**
   * @see javax.naming.Context#listBindings(javax.naming.Name)
   */
  public NamingEnumeration listBindings(Name name) throws NamingException {
    //  Auto-generated method stub
    return null;
  }

  /**
   * @see javax.naming.Context#listBindings(java.lang.String)
   */
  public NamingEnumeration listBindings(String name) throws NamingException {
    //  Auto-generated method stub
    return null;
  }

  /**
   * @see javax.naming.Context#lookup(javax.naming.Name)
   */
  public Object lookup(Name name) throws NamingException {
    //  Auto-generated method stub
    return new Object();
  }

  /**
   * @see javax.naming.Context#lookup(java.lang.String)
   */
  public Object lookup(String name) throws NamingException {
    //  Auto-generated method stub
    return new Object();
  }

  /**
   * @see javax.naming.Context#lookupLink(javax.naming.Name)
   */
  public Object lookupLink(Name name) throws NamingException {
    //  Auto-generated method stub
    return null;
  }

  /**
   * @see javax.naming.Context#lookupLink(java.lang.String)
   */
  public Object lookupLink(String name) throws NamingException {
    //  Auto-generated method stub
    return null;
  }

  /**
   * @see javax.naming.Context#rebind(javax.naming.Name, java.lang.Object)
   */
  public void rebind(Name name, Object obj) throws NamingException {
    //  Auto-generated method stub

  }

  /**
   * @see javax.naming.Context#rebind(java.lang.String, java.lang.Object)
   */
  public void rebind(String name, Object obj) throws NamingException {
    //  Auto-generated method stub

  }

  /**
   * @see javax.naming.Context#removeFromEnvironment(java.lang.String)
   */
  public Object removeFromEnvironment(String propName) throws NamingException {
    //  Auto-generated method stub
    return null;
  }

  /**
   * @see javax.naming.Context#rename(javax.naming.Name, javax.naming.Name)
   */
  public void rename(Name oldName, Name newName) throws NamingException {
    //  Auto-generated method stub

  }

  /**
   * @see javax.naming.Context#rename(java.lang.String, java.lang.String)
   */
  public void rename(String oldName, String newName) throws NamingException {
    //  Auto-generated method stub

  }

  /**
   * @see javax.naming.Context#unbind(javax.naming.Name)
   */
  public void unbind(Name name) throws NamingException {
    //  Auto-generated method stub

  }

  /**
   * @see javax.naming.Context#unbind(java.lang.String)
   */
  public void unbind(String name) throws NamingException {
    //  Auto-generated method stub

  }

}
