package org.sapia.soto.ldap.jaas;

import java.security.Principal;
import java.util.Enumeration;

import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPAttributeSet;
import netscape.ldap.LDAPEntry;

/**
 * An instance of this class creates a {@link org.sapia.soto.ldap.jaas.LDAPPrincipal} out
 * of a given {@link netscape.ldap.LDAPEntry} and user id (corresponding to a username).
 * 
 * @author yduchesne
 */
public class DefaultPrincipalFactory implements PrincipalFactory{
  
  private String passwordPattern = "password";
  
  public Principal createPrincipalFrom(String uid, LDAPEntry entry) {
    LDAPAttributeSet set = entry.getAttributeSet();
    Enumeration attrs = set.getAttributes();
    LDAPPrincipal p = new LDAPPrincipal(uid);    
    while(attrs.hasMoreElements()){
      LDAPAttribute attr = (LDAPAttribute)attrs.nextElement();
      Enumeration values = attr.getStringValues();
      if(values.hasMoreElements()){
        if(attr.getName().toLowerCase().indexOf(passwordPattern) < 0){
          p.getProperties().setProperty(attr.getName(), (String)values.nextElement());
        }
      }
    }
    return p;
  }

}
