package org.sapia.soto.ldap.jaas;

import java.security.Principal;

import netscape.ldap.LDAPEntry;

/**
 * This interface models the behavior of factories that create
 * {@link java.security.Principal} instances out of LDAP entries.
 * 
 * @see netscape.ldap.LDAPEntry
 * 
 * @author yduchesne
 *
 */
public interface PrincipalFactory {

  public Principal createPrincipalFrom(String uid, LDAPEntry entry);
}
