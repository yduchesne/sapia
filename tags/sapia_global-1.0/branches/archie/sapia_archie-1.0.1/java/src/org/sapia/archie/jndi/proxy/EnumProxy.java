package org.sapia.archie.jndi.proxy;

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * A proxy class around a <code>NamingEnumeration</code>.
 * 
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class EnumProxy implements NamingEnumeration{
	
	private NamingEnumeration _enumeration;
	private Name              _parentContextName;
	
	public EnumProxy(Name parentContextName, NamingEnumeration enumeration){
		_enumeration = enumeration;
		_parentContextName = (Name)parentContextName.clone();
	}
 
  /**
   * @see javax.naming.Namingenumerationeration#close()
   */
  public void close() throws NamingException {
    _enumeration.close();
  }

  /**
   * @see javax.naming.Namingenumerationeration#hasMore()
   */
  public boolean hasMore() throws NamingException {
    return _enumeration.hasMore();
  }

  /**
   * @see javax.naming.Namingenumerationeration#next()
   */
  public Object next() throws NamingException {
    return onNext(_parentContextName, _enumeration.next());
  }

  /**
   * @see java.util.enumerationeration#hasMoreElements()
   */
  public boolean hasMoreElements() {
    return _enumeration.hasMoreElements();
  }

  /**
   * @see java.util.enumerationeration#nextElement()
   */
  public Object nextElement(){
  	try {
			return onNext(_parentContextName, _enumeration.nextElement());      
    } catch (Exception e) {
      throw new IllegalStateException(e.getClass().getName() + " caught while returning object (message: " + e.getMessage() + ")");
    }
  }
  
  /**
   * This method is internally called by this class' <code>next()</code> and
   * <code>nextElement()</code> methods. The objects returned by the <code>next...()</code>
   * methods are passed to this method, in order to allow preprocessing. 
   * <p>
   * This method can be overridden to return an application-defined object,
   * instead of the given instance.
   * <p>
   * Note that the given object might be an instance of <code>Binding</code>.
   * 
   * @param parent the name of the parent JNDI context.
   * @param obj the object that was returned by the iteration.
   * @return the object to return to the client.
   * @see javax.naming.Binding
   */
  protected Object onNext(Name parent, Object obj) throws NamingException{
  	return obj; 
  }

}
