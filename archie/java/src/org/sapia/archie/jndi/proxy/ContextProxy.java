package org.sapia.archie.jndi.proxy;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ContextProxy implements Context{
	
	protected Context _ctx;
	private NameParser _parser;
	private Name _absoluteName;
	
	public ContextProxy(Context ctx) throws NamingException{
		_ctx = ctx;	
		_parser = ctx.getNameParser("");
		_absoluteName = _parser.parse(ctx.getNameInNamespace());
	}

  /**
   * @see javax.naming.Context#addToEnvironment(java.lang.String, java.lang.Object)
   */
  public Object addToEnvironment(String propName, Object propVal)
    throws NamingException {
    return _ctx.addToEnvironment(propName, propVal);
  }

  /**
   * @see javax.naming.Context#bind(javax.naming.Name, java.lang.Object)
   */
  public void bind(Name name, Object obj) throws NamingException {
		_ctx.bind(name, onBind(name, obj));
  }

  /**
   * @see javax.naming.Context#bind(java.lang.String, java.lang.Object)
   */
  public void bind(String name, Object obj) throws NamingException {
		bind(_parser.parse(name), obj);
  }

  /**
   * @see javax.naming.Context#close()
   */
  public void close() throws NamingException {
    _ctx.close();
  }

  /**
   * @see javax.naming.Context#composeName(javax.naming.Name, javax.naming.Name)
   */
  public Name composeName(Name name, Name prefix) throws NamingException {
    return _ctx.composeName(name, prefix);
  }

  /**
   * @see javax.naming.Context#composeName(java.lang.String, java.lang.String)
   */
  public String composeName(String name, String prefix)
    throws NamingException {
    return _ctx.composeName(name, prefix);
  }

  /**
   * @see javax.naming.Context#createSubcontext(javax.naming.Name)
   */
  public Context createSubcontext(Name name) throws NamingException {
    return onSubContext(name, _ctx.createSubcontext(name));
  }

  /**
   * @see javax.naming.Context#createSubcontext(java.lang.String)
   */
  public Context createSubcontext(String name) throws NamingException {
		return createSubcontext(_parser.parse(name));
  }

  /**
   * @see javax.naming.Context#destroySubcontext(javax.naming.Name)
   */
  public void destroySubcontext(Name name) throws NamingException {
		_ctx.destroySubcontext(name);
  }

  /**
   * @see javax.naming.Context#destroySubcontext(java.lang.String)
   */
  public void destroySubcontext(String name) throws NamingException {
		_ctx.destroySubcontext(name);
  }

  /**
   * @see javax.naming.Context#getEnvironment()
   */
  public Hashtable getEnvironment() throws NamingException {
    return _ctx.getEnvironment();
  }

  /**
   * @see javax.naming.Context#getNameInNamespace()
   */
  public String getNameInNamespace() throws NamingException {
    return _ctx.getNameInNamespace();
  }

  /**
   * @see javax.naming.Context#getNameParser(javax.naming.Name)
   */
  public NameParser getNameParser(Name name) throws NamingException {
    return _ctx.getNameParser(name);
  }

  /**
   * @see javax.naming.Context#getNameParser(java.lang.String)
   */
  public NameParser getNameParser(String name) throws NamingException {
    return _ctx.getNameParser(name);
  }

  /**
   * @see javax.naming.Context#list(javax.naming.Name)
   */
  public NamingEnumeration list(Name name) throws NamingException {
    return onEnum(name, _ctx.list(name));
  }

  /**
   * @see javax.naming.Context#list(java.lang.String)
   */
  public NamingEnumeration list(String name) throws NamingException {
    return list(_parser.parse(name));
  }

  /**
   * @see javax.naming.Context#listBindings(javax.naming.Name)
   */
  public NamingEnumeration listBindings(Name name) throws NamingException {
		return onEnum(name, _ctx.listBindings(name));
  }

  /**
   * @see javax.naming.Context#listBindings(java.lang.String)
   */
  public NamingEnumeration listBindings(String name) throws NamingException {
		return listBindings(_parser.parse(name));
  }

  /**
   * @see javax.naming.Context#lookup(javax.naming.Name)
   */
  public Object lookup(Name name) throws NamingException {
    return onLookup(name, _ctx.lookup(name));
  }

  /**
   * @see javax.naming.Context#lookup(java.lang.String)
   */
  public Object lookup(String name) throws NamingException {
    return lookup(_parser.parse(name));
  }

  /**
   * @see javax.naming.Context#lookupLink(javax.naming.Name)
   */
  public Object lookupLink(Name name) throws NamingException {
    return onLookup(name, _ctx.lookupLink(name));
  }

  /**
   * @see javax.naming.Context#lookupLink(java.lang.String)
   */
  public Object lookupLink(String name) throws NamingException {
		return _ctx.lookupLink(_parser.parse(name));
  }

  /**
   * @see javax.naming.Context#rebind(javax.naming.Name, java.lang.Object)
   */
  public void rebind(Name name, Object obj) throws NamingException {
		_ctx.rebind(name, onRebind(name, obj));
  }

  /**
   * @see javax.naming.Context#rebind(java.lang.String, java.lang.Object)
   */
  public void rebind(String name, Object obj) throws NamingException {
		_ctx.rebind(name, onRebind(_parser.parse(name), obj));
  }

  /**
   * @see javax.naming.Context#removeFromEnvironment(java.lang.String)
   */
  public Object removeFromEnvironment(String propName) throws NamingException {
    return _ctx.removeFromEnvironment(propName);
  }

  /**
   * @see javax.naming.Context#rename(javax.naming.Name, javax.naming.Name)
   */
  public void rename(Name oldName, Name newName) throws NamingException {
  	_ctx.rename(oldName, newName);
  }

  /**
   * @see javax.naming.Context#rename(java.lang.String, java.lang.String)
   */
  public void rename(String oldName, String newName) throws NamingException {
  	_ctx.rename(oldName, newName);

  }

  /**
   * @see javax.naming.Context#unbind(javax.naming.Name)
   */
  public void unbind(Name name) throws NamingException {
		_ctx.unbind(name);
  }

  /**
   * @see javax.naming.Context#unbind(java.lang.String)
   */
  public void unbind(String name) throws NamingException {
  	_ctx.unbind(name);
  }
  
  protected Object onBind(Name name, Object toBind) throws NamingException{
  	return toBind;
  }
  
	protected Object onRebind(Name name, Object toBind) throws NamingException{
		return toBind;
	}  
	
	protected Object onLookup(Name name, Object lookedUp) throws NamingException{
		return lookedUp;
	}
	
	protected Context onSubContext(Name ctxName, Context subContext) throws NamingException{
		return subContext;
	}
	
	protected NamingEnumeration onEnum(Name enumName, NamingEnumeration enumeration) throws NamingException{
		return enumeration;
	}
	
	/**
	 * @return this context's absolute name.
	 */
	protected Name getAbsoluteName(){
		return (Name)_absoluteName.clone();
	}
	
	/**
	 * @return the <code>Context</code> kept within this instance.
	 */
	protected Context getInternalContext(){
		return _ctx;
	}
	
	/**
	 * @return the <code>NameParser</code> kept within this instance.
	 */
	protected NameParser getNameParser(){
		return _parser;		
	}
}
