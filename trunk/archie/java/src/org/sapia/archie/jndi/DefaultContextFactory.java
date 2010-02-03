package org.sapia.archie.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import org.sapia.archie.ProcessingException;
import org.sapia.archie.impl.DefaultNode;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class DefaultContextFactory implements InitialContextFactory{
  
  /**
   * @see javax.naming.spi.InitialContextFactory#getInitialContext(java.util.Hashtable)
   */
  public Context getInitialContext(Hashtable environment)
    throws NamingException {
    try{
      return new JndiContext(new DefaultNode());
    }catch(ProcessingException e){
      NamingException ne = new NamingException("Could not create initial context");
      ne.setRootCause(e);
      throw ne;
    }
  }


}
