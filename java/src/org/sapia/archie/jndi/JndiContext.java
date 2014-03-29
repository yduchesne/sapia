package org.sapia.archie.jndi;

import java.util.Hashtable;
import java.util.Iterator;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.sapia.archie.Archie;
import org.sapia.archie.DuplicateException;
import org.sapia.archie.Node;
import org.sapia.archie.NotFoundException;
import org.sapia.archie.ProcessingException;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class JndiContext implements Context{
  
  private Archie     _archie;
  private Hashtable  _env       = new Hashtable();
  private NameParser _parser;
  
  public JndiContext(Node node){
    _archie = new Archie(node);
    _parser = new JndiNameParser(node.getNameParser());
  }
  
  /**
   * @param create will create missing intermediary nodes if not found on lookup.
   */
  public void setCreateMissingNodesOnLookup(boolean create) {
    _archie.setCreateMissingNodesOnLookup(create);
  }

  /**
   * @see javax.naming.Context#addToEnvironment(java.lang.String, java.lang.Object)
   */
  public Object addToEnvironment(String propName, Object propVal)
    throws NamingException {
    return _env.put(propName, propVal);
  }

  /**
   * @see javax.naming.Context#bind(javax.naming.Name, java.lang.Object)
   */
  public synchronized void bind(Name name, Object obj) throws NamingException {
    try{
      _archie.bind(getNameFrom(name), obj);
    }catch(ProcessingException e){
      throw getNamingException("Could not bind under '" + name + "'", e);
    }
  }

  /**
   * @see javax.naming.Context#bind(java.lang.String, java.lang.Object)
   */
  public synchronized void bind(String name, Object obj) throws NamingException {
    try{
      _archie.bind(_archie.getNameParser().parse(name), obj);
    }catch(ProcessingException e){
      throw getNamingException("Could not bind under '" + name + "'", e);
    }
  }

  /**
   * @see javax.naming.Context#close()
   */
  public void close() throws NamingException {
  }

  /**
   * @see javax.naming.Context#composeName(javax.naming.Name, javax.naming.Name)
   */
  public Name composeName(Name name, Name prefix) throws NamingException {
    org.sapia.archie.Name toAdd = getNameFrom(name);
    org.sapia.archie.Name prfx  = getNameFrom(prefix);
    return new JndiName(prfx.add(toAdd));
  }
  
  /**
   * @see javax.naming.Context#composeName(java.lang.String, java.lang.String)
   */
  public String composeName(String name, String prefix)
    throws NamingException {
    try{
      org.sapia.archie.Name toAdd = _archie.getNameParser().parse(name);
      org.sapia.archie.Name prfx  = _archie.getNameParser().parse(prefix);      
      return _archie.getNameParser().asString(prfx.add(toAdd));
    }catch(ProcessingException e){
      throw getNamingException("Could not process name", e);
    }
  }

  /**
   * @see javax.naming.Context#createSubcontext(javax.naming.Name)
   */
  public synchronized Context createSubcontext(Name name) throws NamingException {
    try{
      return newChildContext(_archie.lookupNode(getNameFrom(name), true));
    }catch(ProcessingException e){
      throw getNamingException("Could not create subcontext", e);
    }catch(NotFoundException e){
      throw getNamingException("Could not create subcontext", e);
    }
  }

  /**
   * @see javax.naming.Context#createSubcontext(java.lang.String)
   */
  public synchronized Context createSubcontext(String name) throws NamingException {
    try{
      return newChildContext(_archie.lookupNode(_archie.getNameParser().parse(name), true));
    }catch(ProcessingException e){
      throw getNamingException("Could not create subcontext", e);
    }catch(NotFoundException e){
      throw getNamingException("Could not create subcontext", e);
    }
  }

  /**
   * @see javax.naming.Context#destroySubcontext(javax.naming.Name)
   */
  public synchronized void destroySubcontext(Name name) throws NamingException {
    if(name.size() == 0){
      return;
    }    
    try{
      org.sapia.archie.Name archName = getNameFrom(name);
      Node node = _archie.lookupNode(archName, false);
      if(node.getParent() != null){
        node.getParent().removeChild(archName.last());
      }
    }catch(ProcessingException e){
      throw getNamingException("Could not destroy subcontext", e);
    }catch(NotFoundException e){
      throw getNamingException("Context not found", e);
    }    
  }

  /**
   * @see javax.naming.Context#destroySubcontext(java.lang.String)
   */
  public synchronized void destroySubcontext(String name) throws NamingException {
    try{
      org.sapia.archie.Name archName = _archie.getNameParser().parse(name);
      if(archName.count() == 0) return;
      Node node = _archie.lookupNode(archName, false);
      if(node.getParent() != null){
        node.getParent().removeChild(archName.last());
      }
    }catch(ProcessingException e){
      throw getNamingException("Could not destroy subcontext", e);
    }catch(NotFoundException e){
      throw getNamingException("Context not found", e);
    }    

  }

  /**
   * @see javax.naming.Context#getEnvironment()
   */
  public Hashtable getEnvironment() throws NamingException {
    return _env;
  }

  /**
   * @see javax.naming.Context#getNameInNamespace()
   */
  public synchronized String getNameInNamespace() throws NamingException {
    return _archie.getNameParser().asString(_archie.getRoot().getAbsolutePath());
  }

  /**
   * @see javax.naming.Context#getNameParser(javax.naming.Name)
   */
  public NameParser getNameParser(Name name) throws NamingException {
    return _parser;
  }

  /**
   * @see javax.naming.Context#getNameParser(java.lang.String)
   */
  public NameParser getNameParser(String name) throws NamingException {
    return _parser;
  }

  /**
   * Returns a <code>NamingEnumeration</code> of <code>NameClassPair</code>s.
   *
   * @see javax.naming.Context#list(javax.naming.Name)
   */
  public synchronized NamingEnumeration list(Name name) throws NamingException {
    try{
    	Node node = _archie.lookupNode(getNameFrom(name), false);
    	return newNamingEnum(node.getEntries(), node.getChildren(), JndiNamingEnum.LIST_NAMECLASS_PAIRS);
   	}catch(ProcessingException e){
   		throw getNamingException("Could not list objects under: " + name, e);
   	}catch(NotFoundException e){
   		throw getNamingException("Could not list objects under: " + name, e);
   	}
  }

  /**
   * Returns a <code>NamingEnumeration</code> of <code>NameClassPair</code>s.
   *
   * @see javax.naming.Context#list(java.lang.String)
   */
  public synchronized NamingEnumeration list(String name) throws NamingException {
    try{
    	Node node = _archie.lookupNode(_archie.getNameParser().parse(name), false);
    	return newNamingEnum(node.getEntries(), node.getChildren(), JndiNamingEnum.LIST_NAMECLASS_PAIRS);
   	}catch(ProcessingException e){
   		throw getNamingException("Could not list objects under: " + name, e);
   	}catch(NotFoundException e){
   		throw getNamingException("Could not list objects under: " + name, e);
   	}
  }

  /**
   * Returns a <code>NamingEnumeration</code> of <code>Binding</code>s.
   *
   * @see javax.naming.Context#listBindings(javax.naming.Name)
   */
  public synchronized NamingEnumeration listBindings(Name name) throws NamingException {
    try{
    	Node node = _archie.lookupNode(getNameFrom(name), true);
    	return newNamingEnum(node.getEntries(), node.getChildren(), JndiNamingEnum.LIST_BINDINGS);
   	}catch(ProcessingException e){
   		throw getNamingException("Could not list objects under: " + name, e);
   	}catch(NotFoundException e){
   		throw getNamingException("Could not list objects under: " + name, e);
   	}
  }

  /**
   * Returns a <code>NamingEnumeration</code> of <code>Binding</code>s.
   *
   * @see javax.naming.Context#listBindings(java.lang.String)
   */
  public synchronized NamingEnumeration listBindings(String name) throws NamingException {
    try{
    	Node node = _archie.lookupNode(_archie.getNameParser().parse(name), true);
    	return newNamingEnum(node.getEntries(), node.getChildren(), JndiNamingEnum.LIST_BINDINGS);
   	}catch(ProcessingException e){
   		throw getNamingException("Could not list objects under: " + name, e);
   	}catch(NotFoundException e){
   		throw getNamingException("Could not list objects under: " + name, e);
   	} 
  }
  
  /**
   * Returns the <code>NamingEnumeration</code> of objects bound under
   * the context specified by the given name.
   * 
   * @return a <code>NamingEnumaration</code>.
   */
  public synchronized NamingEnumeration listObjects(Name name) throws NamingException {
    try{
    	Node node = _archie.lookupNode(getNameFrom(name), false);
    	return newNamingEnum(node.getEntries(), node.getChildren(), JndiNamingEnum.LIST_OBJECTS);
   	}catch(ProcessingException e){
   		throw getNamingException("Could not list objects under: " + name, e);
   	}catch(NotFoundException e){
   		throw getNamingException("Could not list objects under: " + name, e);
   	}
  }

  /**
   * Returns the <code>NamingEnumeration</code> of objects bound under
   * the context specified by the given name.
   * 
   * @return a <code>NamingEnumaration</code>.
   */
  public synchronized NamingEnumeration listObjects(String name) throws NamingException {
    try{
    	Node node = _archie.lookupNode(_archie.getNameParser().parse(name), false);
    	return newNamingEnum(node.getEntries(), node.getChildren(), JndiNamingEnum.LIST_OBJECTS);
   	}catch(ProcessingException e){
   		throw getNamingException("Could not list objects under: " + name, e);
   	}catch(NotFoundException e){
   		throw getNamingException("Could not list objects under: " + name, e);
   	}
  }  

  /**
   * @see javax.naming.Context#lookup(javax.naming.Name)
   */
  public synchronized Object lookup(Name name) throws NamingException {
    if(name.size() == 0){
      return this;
    }
    try{
    	return _archie.lookup(getNameFrom(name));
    }catch(ProcessingException e){
    	throw getNamingException("Could not perform lookup", e);
    }catch(NotFoundException e){
    	throw getNamingException("No object found for: " + name, e);    
    }
  }

  /**
   * @see javax.naming.Context#lookup(java.lang.String)
   */
  public synchronized Object lookup(String name) throws NamingException {
    if(name == null || name.length() == 0){
      return this;
    }
    try{
    	return _archie.lookup(_archie.getNameParser().parse(name));
    }catch(ProcessingException e){
    	throw getNamingException("Could not perform lookup", e);
    }catch(NotFoundException e){
    	throw getNamingException("No object found for: " + name, e);    
    }
  }

  /**
   * @see javax.naming.Context#lookupLink(javax.naming.Name)
   */
  public synchronized Object lookupLink(Name name) throws NamingException {
    return lookup(name);
  }

  /**
   * @see javax.naming.Context#lookupLink(java.lang.String)
   */
  public synchronized Object lookupLink(String name) throws NamingException {
    return lookup(name);
  }

  /**
   * @see javax.naming.Context#rebind(javax.naming.Name, java.lang.Object)
   */
  public synchronized void rebind(Name name, Object obj) throws NamingException {
    try{
    	_archie.rebind(getNameFrom(name), obj);
   	}catch(ProcessingException e){
   		throw getNamingException("Could not bind object for: " + name, e);
   	}
  }

  /**
   * @see javax.naming.Context#rebind(java.lang.String, java.lang.Object)
   */
  public synchronized void rebind(String name, Object obj) throws NamingException {
    try{
    	_archie.rebind(_archie.getNameParser().parse(name), obj);
   	}catch(ProcessingException e){
   		throw getNamingException("Could not bind object for: " + name, e);
   	}
  }

  /**
   * @see javax.naming.Context#removeFromEnvironment(java.lang.String)
   */
  public Object removeFromEnvironment(String propName) throws NamingException {
    return _env.remove(propName);
  }

  /**
   * NOT SUPPORTED.   
   * @see javax.naming.Context#rename(javax.naming.Name, javax.naming.Name)
   */
  public void rename(Name oldName, Name newName) throws NamingException {
		throw new NamingException("Rename not supported");
  }

  /**
   * NOT SUPPORTED.
   *
   * @see javax.naming.Context#rename(java.lang.String, java.lang.String)
   */
  public void rename(String oldName, String newName) throws NamingException {
		throw new NamingException("Rename not supported");
  }

  /**
   * @see javax.naming.Context#unbind(javax.naming.Name)
   */
  public synchronized void unbind(Name name) throws NamingException {
    try{
	  	_archie.unbind(getNameFrom(name));
	  }catch(ProcessingException e){
	  	throw getNamingException("Could not unbind: " + name, e);
	  }
  }

  /**
   * @see javax.naming.Context#unbind(java.lang.String)
   */
  public synchronized void unbind(String name) throws NamingException {
    try{
	  	_archie.unbind(_archie.getNameParser().parse(name));
	  }catch(ProcessingException e){
	  	throw getNamingException("Could not unbind: " + name, e);
	  }
  }
  
  protected org.sapia.archie.Name getNameFrom(Name name) throws NamingException{
    org.sapia.archie.Name n = new org.sapia.archie.Name();
    for(int i = 0; i < name.size(); i++){
      try{
        n.add(_archie.getNameParser().parseNamePart((name.get(i))));
      }catch(ProcessingException e){
        throw getNamingException("Could not process name: " + name, e);
      }
    }
    return n;
  }
  
  protected Context newChildContext(Node node){
    return new JndiContext(node);
  }
  
  protected Archie getArchie() {
    return _archie;
  }
  
  /**
   * Can be overridden to return an app-specific NamingEnumeration. The
   * method takes this instance's node's entries and child nodes.
   *
   * @param entries an iterator of <code>Entry</code> instances.
   * @param nodes  an iterator of <code>Node</code> instances.
   * @param boolean <code>true</code> if the returned enumeration should produce
   *  <code>Binding</code> instances.
   */
  protected NamingEnumeration newNamingEnum(Iterator entries, Iterator childNodes, int listType){
    return new JndiNamingEnum(entries, childNodes, listType);
  }
  
  private NamingException getNamingException(String msg, ProcessingException e){
    if(e instanceof DuplicateException){
      NameAlreadyBoundException nabe = new NameAlreadyBoundException(msg);
      nabe.setRootCause(e);
      return nabe;
    }
    else{
      NamingException ne = new NamingException(msg);
      ne.setRootCause(e);
      return ne;
    }
  }
  private NamingException getNamingException(String msg, NotFoundException e){
    NamingException ne = new NameNotFoundException(msg);
    ne.setRootCause(e);
    ne.setResolvedName(new JndiName(e.getResolvedName()));
    ne.setRemainingName(new JndiName(e.getRemainingName()));
    return ne;
  }  
}
