package org.sapia.archie.jndi;

import java.util.Iterator;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.sapia.archie.Entry;
import org.sapia.archie.Node;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class JndiNamingEnum implements NamingEnumeration{
    
  public static final int LIST_OBJECTS         = 0;
  public static final int LIST_BINDINGS        = 1;  
  public static final int LIST_NAMECLASS_PAIRS = 2;    
  
  protected Iterator _entries, _childNodes;
  protected int _listType;
  
  public JndiNamingEnum(Iterator entries, Iterator childNodes, int listType){
    _entries    = entries;
    _childNodes = childNodes;
    _listType   = listType;
  }
  /**
   * @see javax.naming.NamingEnumeration#close()
   */
  public void close() throws NamingException {
  }
  
  /**
   * @see javax.naming.NamingEnumeration#hasMore()
   */
  public boolean hasMore() throws NamingException {
     return _entries.hasNext() || _childNodes.hasNext();
  }
  
  /**
   * @see javax.naming.NamingEnumeration#next()
   */
  public Object next() throws NamingException {
    if(_listType == LIST_BINDINGS){
    	if(_entries.hasNext()){
				return toBinding((Entry)_entries.next());    		
    	}
    	else{
    		return toBinding((Node)_childNodes.next()); 
    	}

    }
    else if(_listType == LIST_NAMECLASS_PAIRS){
    	if(_entries.hasNext()){
        Entry  entry = (Entry)_entries.next();
				return new NameClassPair(entry.getName (), entry.getValue().getClass().getName(), true);    		
    	}
    	else{
        Node    node = (Node)_childNodes.next();
        Context ctx  = newJndiContext(node);
        return new NameClassPair(node.getName().asString(), ctx.getClass().getName(), true);
    	}
    }
    else {
    	if(_entries.hasNext()){
        return ((Entry)_entries.next()).getValue();
    	}
    	else{
    		return newJndiContext((Node)_childNodes.next());
    	}        
    }
  }
  
  /**
   * @see java.util.Enumeration#hasMoreElements()
   */
  public boolean hasMoreElements() {
    return _entries.hasNext() || _childNodes.hasNext();
  }
  
  /**
   * @see java.util.Enumeration#nextElement()
   */
  public Object nextElement() {
    try{
      return next();
    }catch(NamingException e){
      throw new IllegalStateException(e.getClass() + " caught; message: " + e.getMessage());
    }
  }
  
  protected Binding toBinding(Entry entry){
    return new Binding(entry.getName(), entry.getValue().getClass().getName(), entry.getValue(), true);
  }
  
  protected Binding toBinding(Node node){
    Context ctx = newJndiContext(node);
  	return new Binding(node.getName().asString(), ctx.getClass().getName(), ctx);
  }
  
  protected Context newJndiContext(Node node){
    return new JndiContext(node);
  }
}
