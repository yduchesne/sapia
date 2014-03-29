package org.sapia.archie;

import org.sapia.archie.impl.*;
import org.sapia.archie.strategy.DefaultLookupNodeStrategy;
import org.sapia.archie.strategy.DefaultLookupStrategy;


/**
 * An instance of this class wraps a <code>Node</code> and offers a 
 * user-friendly API on top of the latter.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class Archie {
  private Node _root;
  private boolean createMissingNodes;

  public Archie() throws ProcessingException{
    _root = new DefaultNode();
  }

  public Archie(Node root) {
    _root = root;
  }
  
  /**
   * @param createMissingNodes <code>true</code> if missing nodes should be created on lookup.
   */
  public void setCreateMissingNodesOnLookup(boolean createMissingNodes) {
    this.createMissingNodes = createMissingNodes;
  }

  /**
   * The root node that this instance holds can be acquired to be manipulated
   * in an application-defined way - by applying a custom lookup algorithm, for
   * example.
   * 
   * @see org.sapia.archie.strategy.LookupStrategy
   * 
   * @return the root <code>Node</code> of this instance.
   */
  public Node getRoot() {
    return _root;
  }

  /**
   * Looks up the object with the given name.
   *  
   * @param n a <code>Name</code>
   * @return the <code>Object</code> that corresponds to the given name.
   * @throws NotFoundException if no object could be found for the given name.
   * @throws ProcessingException if a problem occurs while performing the lookup.
   */
  public Object lookup(Name n) throws NotFoundException, ProcessingException {
    n = (Name)n.clone();    
    DefaultLookupStrategy strat = new DefaultLookupStrategy(createMissingNodes);
    return strat.lookup(n, _root);
  }
  
  /**
   * This method can be used by client applications to acquire the 
   * <code>NameParser</code> that this instance holds, in order to create
   * object representation of string-based names.
   * <p>
   * Example:
   * <pre>
   * Name aName      = archie.getNameParser().parse("some/object/name");
   * Object anObject = archie.lookup(aName);
   * </pre>
   * 
   * @return the <code>NameParser</code> that this instance uses.
   */
  public NameParser getNameParser(){
    return _root.getNameParser();
  }

  /**
   * Looks up the <code>Node</code> with the given name.
   *  
   * @param n a <code>Name</code>
   * @return the <code>Node</code> that corresponds to the given name.
   * @throws NotFoundException if no node could be found for the given name.
   * @throws ProcessingException if a problem occurs while performing the lookup.
   */

  public Node lookupNode(Name n, boolean create)
                  throws NotFoundException, ProcessingException {
    n = (Name)n.clone();                    
    DefaultLookupNodeStrategy strat = new DefaultLookupNodeStrategy(create);

    return (Node) strat.lookup(n, _root);
  }
  
  /**
   * Unbinds the value under the given name.
   * 
   * @param n a <code>Name</code>.
   */
  public void unbind(Name n) throws ProcessingException{
    n = (Name)n.clone();
    if(n.count() == 0){
      return;
    }
    NamePart np = n.chopLast();
    try{
      lookupNode(n, false).removeValue(np);
    }catch(NotFoundException e){
      // noop
    }
  }

  /**
   * Binds the given object under the given name.
   * 
   * @param n a <code>Name</code>
   * @param o the <code>Object</code> to bind.
   * @throws DuplicateException if a object already exists for the given name.
   * @throws ProcessingException if a problem occurs while performing the binding.
   */
  public void bind(Name n, Object o)
            throws DuplicateException, ProcessingException {
    doBind(n, o, false);
  }
  
  /**
   * Binds the given object under the given name; if an object already
   * exists under the given name, it is overwritten.
   * 
   * @param n a <code>Name</code>
   * @param o the <code>Object</code> to bind.
   * @throws DuplicateException if a object already exists for the given name.
   * @throws ProcessingException if a problem occurs while performing the binding.
   */
  public void rebind(Name n, Object o)
            throws ProcessingException {
    doBind(n, o, true);
  }  
  
  
  
  private void doBind(Name n, Object o, boolean overwrite) throws ProcessingException{
    n = (Name)n.clone();
    NamePart last = n.last();

    if ((n.count() == 1) && (last.asString().length() == 0)) {
      throw new ProcessingException("Cannot bind object with empty name");
    }

    last = n.chopLast();

    DefaultLookupNodeStrategy strat = new DefaultLookupNodeStrategy(true);
    Node                      node = null;

    try {
      node = (Node) strat.lookup(n, _root);
    } catch (NotFoundException e) {
      throw new ProcessingException(e.getMessage());
    }

    if (!node.putValue(last, o, overwrite)) {
      throw new DuplicateException(last.asString());
    }
  }
}
