package org.sapia.archie;

import java.util.Iterator;
import java.util.Map;

import org.sapia.archie.impl.*;


/**
 * Abstract implementation of the <code>Node</code> interface.
 * 
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public abstract class AbstractNode implements Node {
  protected Map         _children;
  protected NodeFactory _fac;
  private NamePart      _name;
  private Name          _absolutePath = new Name();
  private Node          _parent;
  private NameParser    _parser;

  protected AbstractNode(NameParser parser, Map children, NodeFactory fac)
                  throws ProcessingException {
    _children = children;
    _fac      = fac;
    _parser   = parser;
    
    // TODO: this is a hack... Maybe name should be passed in ctor
    _name     = parser.parse("/").first();
    _absolutePath.add(_name);
  }

  protected AbstractNode(Map children, NodeFactory fac)
                  throws ProcessingException {
    this(new DefaultNameParser(), children, fac);
  }

  /**
   * @see org.sapia.archie.Node#createChild(NamePart)
   */
  public Node createChild(NamePart name)
                   throws DuplicateException, ProcessingException {
    if (_children.containsKey(name)) {
      throw new DuplicateException(name.asString());
    }

    Node n = _fac.newNode();
    n.setUp(this, name);
    _children.put(name, n);
    return n;
  }
  
  /**
   * @see org.sapia.archie.Node#getChild(NamePart)
   */
  public Node getChild(NamePart name) {
    return (Node)_children.get(name);
  }
  
  /**
   * @see org.sapia.archie.Node#removeChild(org.sapia.archie.NamePart)
   */
  public Node removeChild(NamePart name) {
    return (Node)_children.remove(name);
  }

  /**
   * @see org.sapia.archie.Node#getChildren()
   */
  public Iterator getChildren() {
    return _children.values().iterator();
  }

  /**
   * @see org.sapia.archie.Node#getName()
   */
  public NamePart getName() {
    return _name;
  }

  /**
   * @see Node#getAbsolutePath()
   */
  public Name getAbsolutePath() {
    return (Name)_absolutePath.clone();
  }

  /**
   * @see Node#getChildrenCount()
   */
  public int getChildrenCount() {
    return _children.size();
  }

  /**
   * @see Node#getChildrenNames()
   */
  public Iterator getChildrenNames() {
    return _children.keySet().iterator();
  }

  /**
   * @see Node#getParent()
   */
  public Node getParent() {
    return _parent;
  }

  /**
   * @see Node#getNameParser()
   */
  public NameParser getNameParser() {
    return _parser;
  }
  
  /**
   * @see Node#setUp(Node, NamePart)
   */
  public void setUp(Node parent, NamePart name) {
    _parent       = parent;
    _absolutePath = new Name().add(parent.getAbsolutePath());
    _name         = name;
    _absolutePath.add(name);
  }

  public String toString() {
    return _name.toString();
  }
}
