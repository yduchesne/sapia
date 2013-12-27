package org.sapia.archie.jndi;

import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;

import org.sapia.archie.ProcessingException;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class JndiNameParser implements NameParser, java.io.Serializable{
  
  private org.sapia.archie.NameParser _parser;
  
  public JndiNameParser(org.sapia.archie.NameParser parser){
    _parser = parser;
  }
  /**
   * @see javax.naming.NameParser#parse(java.lang.String)
   */
  public Name parse(String name) throws NamingException {
    try{
      return new JndiName(_parser.parse(name));
    }catch(ProcessingException e){
      NamingException ne = new NamingException("Could not parse name: " + name);
      ne.setRootCause(e);
      throw ne;
    }
  }
}
