package org.sapia.soto.ldap.jaas;

import java.io.Serializable;
import java.security.Principal;
import java.util.Properties;

/**
 * An instance of this class is created from a LDAP entry. It corresponds
 * to a user account in a LDAP server.
 * 
 * @author yduchesne
 *
 */
public class LDAPPrincipal implements Principal, Serializable{
  
  static final long serialVersionUID = 1L;
  
  private String _name;
  
  private Properties _properties = new Properties();

  /**
   * Create an instance of this class with a username.
   * <p>
   * 
   * @param name a username.
   */
  public LDAPPrincipal(String name) {
    _name = name;
  }

  /**
   * @return the username for this instance.
   */
  public String getName() {
    return _name;
  }
  
  /**
   * @return this instance's LDAP attributes (names and corresponding values)
   * in a <code>Properties</code> instance.
   */
  public Properties getProperties(){
    return _properties;
  }

  public String toString() {
    return (getClass() + "@" + _name);
  }

  public boolean equals(Object o) {
    if (o == null){
      return false;
    }

    if (this == o){
      return true;
    }

    if (!(o instanceof LDAPPrincipal)){
      return false;
    }
    
    LDAPPrincipal that = (LDAPPrincipal) o;

    if (this.getName().equals(that.getName())){
      return true;
    }
    return false;
  }

  public int hashCode() {
    return _name.hashCode();
  }
}
